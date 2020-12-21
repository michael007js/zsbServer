package socket;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;

public interface OnServerCallBack {

    boolean isBroadCast();

    void onClientsChanged(ArrayList<ChannelHandlerContext> clients);

    void onLog(String log);

    void onHeartBeat(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onReceivedMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onSendMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx);

    void onError(Throwable e);
}
