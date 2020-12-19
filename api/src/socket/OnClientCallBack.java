package socket;

import io.netty.channel.ChannelHandlerContext;

public interface OnClientCallBack {
    void onReceivedMessage(String message);

    void onLog(String log);

    void onHeartBeat(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onReceivedMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onSendMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onError(Throwable e);
}
