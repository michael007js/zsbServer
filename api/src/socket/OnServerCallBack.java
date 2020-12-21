package socket;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;

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
