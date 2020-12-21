package socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import utils.LogUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 使用Netty创建Server程序.
 * <p>
 * Tips:
 * <ol>
 * <li>Netty默认的线程数量为CPU个数*2；</li>
 * <li>netty默认使用SLF4j日志组件，可以通过代码设置:{@code InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);}</li>
 * <li>pipeline添加Handler时，可以通过参数指定执行Handler的线程池，默认为workerGroup执行；</li>
 * <li>LoggingHandler可以很方便观察到Netty方法执行过程，日志级别设置为DEBUG，log4j配置文件中也需要设置为debug；</li>
 * <li>注意Pipeline中事件处理，如果需要后续Handler接着处理，一定不要忘记调用ctx.fireXXX方法；</li>
 * </ol>
 */
public class NettyServer {

    private static final int DEFAULT_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2;
    private int port = AppConstant.PORT;
    private int nioThreadNum = DEFAULT_THREAD_NUM;
    private int bizThreadNum = DEFAULT_THREAD_NUM;
    private ChannelFuture future;
    private ServerBootstrap bootstrap;
    private EventExecutorGroup bizGroup = null;
    private OnServerCallBack onServerCallBack;

    public void setOnServerCallBack(OnServerCallBack onServerCallBack) {
        this.onServerCallBack = onServerCallBack;
    }

    public NettyServer(int port) {
        this.port = port;
    }

    public NettyServer(int port, int nioThreadNum, int bizThreadNum) {
        this.port = port;
        this.nioThreadNum = nioThreadNum;
        this.bizThreadNum = bizThreadNum;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(nioThreadNum); // netty默认NIO线程数为CPU数*2

        // netty默认使用SLF4j日志组件
        // InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
        // LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
        // 业务线程池，用于执行业务
        bizGroup = new DefaultEventExecutorGroup(bizThreadNum);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            // 设置用于ServerSocketChannel的属性和handler
            b.handler(new ChannelInitializer<ServerSocketChannel>() {

                protected void initChannel(ServerSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                }
            });
            b.option(ChannelOption.SO_BACKLOG, 128);
            b.option(ChannelOption.SO_REUSEADDR, true);

            // 设置用于SocketChannel的属性和handler
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new IdleStateHandler(60, 20, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new NettyServerHeartBeatDuplexHandler(onServerCallBack));
                    ch.pipeline().addLast(new NettyMessageDecoder());
                    ch.pipeline().addLast(bizGroup, new NettyServerBusinessDuplexHandler(new ServerBusinessProcessor(),onServerCallBack));

                }
            });

            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_REUSEADDR, true);

            // Bind and start to accept incoming connections.
            future = b.bind(port).sync();
            bootstrap = b;
            if (onServerCallBack != null) {
                onServerCallBack.onLog("服务已启动，监听端口：" + port);
            }
            LogUtils.e("服务已启动，监听端口：" + port);
        } catch (Throwable t) {
            if (onServerCallBack != null) {
                onServerCallBack.onError(t);
            }
            LogUtils.e("异常", t);
        }
    }

    public void close() {
        // 关闭业务线程池
        if (bizGroup != null) {
            bizGroup.shutdownGracefully();
        }

        if (future != null) {
            future.channel().close();
//             future.channel().closeFuture().sync();
        }

        if (bootstrap != null) {
            if (bootstrap.config().group() != null) {
                bootstrap.config().group().shutdownGracefully();
            }
            if (bootstrap.config().childGroup() != null) {
                bootstrap.config().childGroup().shutdownGracefully();
            }
        }
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
//        NettyServer server = new NettyServer(AppConstant.PORT);
//        server.start();

//        try {
//            waitToQuit(server);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void waitToQuit(NettyServer server) throws IOException {
        if (onServerCallBack != null) {
            onServerCallBack.onLog("Server is running on port {}, press q to quit." + server.getPort());
        }
        LogUtils.e("Server is running on port {}, press q to quit." + server.getPort());
        boolean input = true;
        while (input) {
            int b = System.in.read();
            switch (b) {
                case 'q':
                    LogUtils.e("Server关闭...");
                    server.close();
                    input = false;
                    break;
                case '\r':
                case '\n':
                    break;
                default:
                    LogUtils.e("q -- quit.");
                    break;
            }
        }
    }
}