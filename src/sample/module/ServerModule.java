package sample.module;

import io.netty.channel.ChannelHandlerContext;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.MainController;
import socket.AppConstant;
import socket.OnServerCallBack;
import socket.NettyClient;
import socket.NettyClientHelper;
import socket.NettyMessage;
import socket.NettyServer;
import utils.MichaelUtils;
import utils.UIUtils;

public class ServerModule extends BaseTabModule implements EventHandler<ActionEvent>, OnServerCallBack {
    private MainController controller;
    private NettyServer server = new NettyServer(AppConstant.PORT);
    private NettyClient client = new NettyClient(AppConstant.HOST, AppConstant.PORT);

    {
        client.init();
    }

    private NettyClientHelper helper = new NettyClientHelper(client);

    @Override
    public void initialize(MainController mainController) {

        this.controller = mainController;
        server.setOnServerCallBack(this);
        controller.getBtn_start_api().setOnAction(this::handle);
        controller.getBtn_stop_api().setOnAction(this::handle);
        controller.getBtn_client().setOnAction(this::handle);
    }

    @Override
    public boolean isBroadCast() {
        return false;
    }

    @Override
    public void onLog(String log) {
        UIUtils.setTextAreaLog(controller.getEdit_api_info(), log);
    }

    @Override
    public void onHeartBeat(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
        UIUtils.setTextAreaLog(controller.getEdit_api_info(), "收到客户心跳" + ctx.channel().remoteAddress());
    }

    @Override
    public void onReceivedMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
        UIUtils.setTextAreaLog(controller.getEdit_api_info(), "收到客户消息" +  ctx.channel().remoteAddress());
    }

    @Override
    public void onSendMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
        UIUtils.setTextAreaLog(controller.getEdit_api_info(), "向客户发送消息" +  ctx.channel().remoteAddress());
    }

    @Override
    public void onError(Throwable e) {
        UIUtils.setTextAreaLog(controller.getEdit_api_error(), e.getLocalizedMessage() + "\n" + MichaelUtils.getStackTrace(e));
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == controller.getBtn_start_api()) {
            server.start();
        } else if (event.getSource() == controller.getBtn_stop_api()) {
            server.close();
        } else if (event.getSource() == controller.getBtn_client()) {
            helper.connect();
            helper.sendMessage("666");

            // 添加Hook，以便程序退出时先关闭相应的线程
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    helper.stopAll();
                }
            });

        }
    }
}
