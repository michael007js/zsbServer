package socket.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import socket.message.NettyMessage;
import socket.callback.OnClientCallBack;
import utils.LogUtils;

/**
 * <p>
 * 心跳Handler,本示例中,心跳采用Ping-Ping模式,即服务端跟客户端互相发送,接收方收到直接丢弃,无需响应.
 * <p>
 * 心跳检测规则：如果20秒未发送任何内容，则发送心跳；如果60秒未收到任何内容，则认为对方超时，关闭连接；规则对双方都适用。
 */
public class NettyClientHeartBeatDuplexHandler extends ChannelDuplexHandler {
    private OnClientCallBack onClientCallBack;

    public void setOnClientCallBack(OnClientCallBack onClientCallBack) {
        this.onClientCallBack = onClientCallBack;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e("连接关闭" + ctx.channel().remoteAddress());
        if (onClientCallBack != null) {
            onClientCallBack.onLog("连接关闭" + ctx.channel().remoteAddress());
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e("连接建立" + ctx.channel().remoteAddress());
        if (onClientCallBack != null) {
            onClientCallBack.onLog("连接建立" + ctx.channel().remoteAddress());
        }
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) { // 20s
                // throw new Exception("idle exception");
                LogUtils.e("尝试向目标发送心跳包" + ctx.channel().remoteAddress());
                if (onClientCallBack != null) {
                    onClientCallBack.onLog("尝试向目标发送心跳包" + ctx.channel().remoteAddress());
                }
                ByteBuf buf = Unpooled.copiedBuffer(NettyMessage.HEATBEAT_MSG.composeFull());
                ctx.writeAndFlush(buf);
            } else if (state == IdleState.READER_IDLE) { // 60s
                LogUtils.e("连接超时,请求关闭" + ctx.channel().remoteAddress());
                if (onClientCallBack != null) {
                    onClientCallBack.onLog("连接超时,请求关闭" + ctx.channel().remoteAddress());
                }
                ctx.close();
            }
            // 注意事项：
            // 因为我实现的逻辑是所有IdleStateEvent只由NettyHeartBeatHandler一个Handler处理即可；
            // 所以可以不需要将事件继续向pipeline后续的Handler传递，当然传递了也没什么事，因为其他的地方不能处理；
            // 在某些情况下，如果你定义的事件需要通知多个Handler处理，那么一定要加上下面这一句才行。
            super.userEventTriggered(ctx, evt);
        } else {
            // 其他事件转发给Pipeline中其他的Handler处理
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtils.e("异常: ", cause.getLocalizedMessage());
        if (onClientCallBack != null) {
            onClientCallBack.onError(cause);
        }
        ctx.close();
    }
}