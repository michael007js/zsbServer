package socket.callback;

import io.netty.channel.ChannelHandlerContext;
import socket.message.NettyMessage;

public interface OnServerCallBack {

    boolean isBroadCast();

    void onClientConnected(ChannelHandlerContext client);

    void onClientDisconnected(ChannelHandlerContext client);

    void onLog(String log);

    void onHeartBeat(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onReceivedMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    String onSendMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onError(Throwable e);
}
