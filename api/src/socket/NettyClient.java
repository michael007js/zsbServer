package socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import utils.LogUtils;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * NettyClient
 */
public class NettyClient {
    private String ip;
    private int port;
    Bootstrap bootstrap;
    ChannelFuture future;
    private EventExecutorGroup bizGroup = null;
    private OnClientCallBack onClientCallBack;

    public void setOnClientCallBack(OnClientCallBack onClientCallBack) {
        this.onClientCallBack = onClientCallBack;
    }

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void init() {
        bizGroup = new DefaultEventExecutorGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new IdleStateHandler(60, 20, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new NettyClientHeartBeatDuplexHandler());
                    ch.pipeline().addLast(new NettyMessageDecoder());
                    ch.pipeline().addLast(bizGroup, new NettyClientBusinessDuplexHandler(new ClientBusinessProcessor(),onClientCallBack));
                }
            });

            bootstrap = b;
            // future = b.connect(ip, port);
//            if (onClientCallBack != null) {
//                onClientCallBack.onLog("成功连接到" + ip + ":" + port);
//            }
//            LogUtils.e("成功连接到" + ip + ":" + port);
        } catch (Throwable t) {
            if (onClientCallBack != null) {
                onClientCallBack.onError(t);
            }
            LogUtils.e("异常", t.getLocalizedMessage());
        }
    }

    /**
     * 连接远程主机
     */
    public void connect() {
        try {
            future = bootstrap.connect(ip, port).sync();
            if (onClientCallBack != null) {
                onClientCallBack.onLog("成功连接到" + ip + ":" + port);
            }
            LogUtils.e("成功连接到" + ip + ":" + port);
        } catch (InterruptedException e) {
//			logger.info("连接服务器异常", e);
            if (onClientCallBack != null) {
                onClientCallBack.onLog("连接服务器异常" + e.getLocalizedMessage());
                onClientCallBack.onError(e);
            }
            LogUtils.e("连接服务器异常" + e.getLocalizedMessage());
        }
    }

    public void disConnect() {
        if (future != null) {
            future.channel().close();
            future = null;
        }
    }

    /**
     * 断开跟远程主机的连接，并关闭相应的线程等资源
     */
    public void close() {
        // 关闭业务线程池
        if (bizGroup != null) {
            bizGroup.shutdownGracefully();
        }
        if (future != null) {
            future.channel().close();
        }
        if (bootstrap != null) {
            if (bootstrap.config().group() != null) {
                bootstrap.config().group().shutdownGracefully();
            }
        }
        if (onClientCallBack != null) {
            onClientCallBack.onLog("成功关闭");
        }
        LogUtils.e("成功关闭");
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
//        NettyClient client = new NettyClient();

        // NettyClientHelper需要在合适的时机设置好NettyClient和UIUpdater，才能调用后面的方法
//        NettyClientHelper helper = new NettyClientHelper();
//        helper.setClient(client);

        // 添加Hook，以便程序退出时先关闭相应的线程
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
//                helper.stopAll();
//            }
//        });

    }
}