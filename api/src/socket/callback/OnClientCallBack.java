package socket.callback;

import io.netty.channel.ChannelHandlerContext;
import socket.message.NettyMessage;

public interface OnClientCallBack {

    void onConnectSuccess();

    void onConnectFail(Throwable e);

    void onLog(String log);

    void onHeartBeat(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onReceivedMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onSendMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onError(Throwable e);
}
