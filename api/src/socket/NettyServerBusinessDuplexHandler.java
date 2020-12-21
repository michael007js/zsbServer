package socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import utils.LogUtils;

/**
 * <p>
 * 负责业务处理的Handler，运行在业务线程组<code>DefaultEventExecutorGroup</code>中，以免任务太耗时而导致NIO线程阻塞；
 */
public class NettyServerBusinessDuplexHandler extends ChannelDuplexHandler {

    private AppBusinessProcessor bizProcessor = null;
    private OnServerCallBack onServerCallBack;


    public NettyServerBusinessDuplexHandler(AppBusinessProcessor appBizHandler, OnServerCallBack onServerCallBack) {
        super();
        this.bizProcessor = appBizHandler;
        this.onServerCallBack = onServerCallBack;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage bizMsg = (NettyMessage) msg; // 拆分好的消息

        if (bizMsg.getMessageType() == NettyMessage.MESSAGE_TYPE_HB) {
            LogUtils.e("收到心跳", bizMsg.toString());
            if (onServerCallBack != null) {
                onServerCallBack.onHeartBeat(bizMsg, ctx);
            }
        } else {
            // 处理业务消息
            if (onServerCallBack != null) {
                onServerCallBack.onReceivedMessage(bizMsg, ctx);
            }
            LogUtils.e("收到消息", bizMsg.toString());
            bizProcessor.process(bizMsg);
            // 如果接收到的是请求，则需要写回响应消息
            if (bizMsg.getFlag() == NettyMessage.MESSAGE_FLAG_RESPONSE) {
                bizMsg.setFlag((byte) NettyMessage.MESSAGE_FLAG_REQUEST);
                if (onServerCallBack != null) {
                    bizMsg.setMessageBody(onServerCallBack.onSendMessage(bizMsg, ctx));
                }
                LogUtils.e("写回消息", bizMsg.toString());
                ByteBuf rspMsg = Unpooled.copiedBuffer(bizMsg.composeFull());
                ctx.writeAndFlush(rspMsg);
            }
        }
//		 继续传递给Pipeline下一个Handler
        super.channelRead(ctx, msg);
        ctx.fireChannelRead(msg);
    }
}