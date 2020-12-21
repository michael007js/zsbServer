package sample.module;

import io.netty.channel.ChannelHandlerContext;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.util.ArrayList;

public class ServerModule extends BaseTabModule implements EventHandler<ActionEvent> {
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
        controller.getBtn_start_api().setOnAction(this::handle);
        controller.getBtn_stop_api().setOnAction(this::handle);
        controller.getBtn_client_send().setOnAction(this::handle);
        controller.getBtn_client_connect().setOnAction(this::handle);
        controller.getBtn_client_disconnect().setOnAction(this::handle);
        server.setOnServerCallBack(new OnServerCallBack() {
            @Override
            public boolean isBroadCast() {
                return false;
            }

            @Override
            public void onClientsChanged(ArrayList<ChannelHandlerContext> clients) {
                UIUtils.setText(controller.getLable_clients(), "实时客户数:" + clients.size());
            }

            @Override
            public void onLog(String log) {
                UIUtils.setTextAreaLog(controller.getEdit_api_info(), log);
            }

            @Override
            public void onHeartBeat(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
                UIUtils.setTextAreaLog(controller.getEdit_api_info(), " 收到客户" + ctx.channel().remoteAddress() + "心跳:" + nettyMessage.toString());
            }

            @Override
            public void onReceivedMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
                UIUtils.setTextAreaLog(controller.getEdit_api_info(), " 收到客户" + ctx.channel().remoteAddress() + "消息:" + nettyMessage.toString());
            }

            @Override
            public void onSendMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
                UIUtils.setTextAreaLog(controller.getEdit_api_info(), "向客户" + ctx.channel().remoteAddress() + "发送消息:" + nettyMessage.toString());
            }

            @Override
            public void onError(Throwable e) {
                UIUtils.setTextAreaLog(controller.getEdit_api_error(), e.getLocalizedMessage() + "\n" + MichaelUtils.getStackTrace(e));
            }
        });

        // 添加Hook，以便程序退出时先关闭相应的线程
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.close();
                client.disConnect();
                client.close();
                helper.stopAll();
            }
        });
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == controller.getBtn_start_api()) {
            server.start();
        } else if (event.getSource() == controller.getBtn_stop_api()) {
            server.close();
        } else if (event.getSource() == controller.getBtn_client_connect()) {
            helper.connect();
        } else if (event.getSource() == controller.getBtn_client_disconnect()) {
            client.disConnect();
//            client.close();
//            helper.stopAll();
        } else if (event.getSource() == controller.getBtn_client_send()) {
            helper.sendMessage("666");
        }
    }
}
