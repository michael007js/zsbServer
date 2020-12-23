package sample.module;

import io.netty.channel.ChannelHandlerContext;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.MainController;
import socket.*;
import socket.callback.OnServerCallBack;
import socket.client.NettyClient;
import socket.message.NettyMessage;
import socket.server.NettyServer;
import utils.UIUtils;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class ServerModule extends BaseTabModule implements EventHandler<ActionEvent> {
    private MainController controller;
    private ArrayList<ChannelHandlerContext> clients = new ArrayList<>();
    private NettyServer server = new NettyServer(AppConstant.PORT);
    private NettyClient client = new NettyClient(AppConstant.HOST, AppConstant.PORT);




    @Override
    public void initialize(MainController mainController) {

        this.controller = mainController;
        controller.getBtn_start_api().setOnAction(this::handle);
        controller.getBtn_stop_api().setOnAction(this::handle);
        controller.getBtn_client_send().setOnAction(this::handle);
        controller.getBtn_client_connect().setOnAction(this::handle);
        controller.getBtn_client_disconnect().setOnAction(this::handle);
        client.init();
        server.setOnServerCallBack(new OnServerCallBack() {
            @Override
            public boolean isBroadCast() {
                return false;
            }

            @Override
            public void onClientConnected(ChannelHandlerContext client) {
                add(client);
            }

            @Override
            public void onClientDisconnected(ChannelHandlerContext client) {
                remove(client);
            }

            @Override
            public void onLog(String log) {
//                UIUtils.setTextAreaLog(controller.getEdit_api_info(), log);
            }

            @Override
            public void onHeartBeat(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
//                UIUtils.setTextAreaLog(controller.getEdit_api_info(), " 收到客户" + ctx.channel().remoteAddress() + "心跳:" + nettyMessage.toString());
            }

            @Override
            public void onReceivedMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
//                UIUtils.setTextAreaLog(controller.getEdit_api_info(), " 收到客户" + ctx.channel().remoteAddress() + "消息:" + nettyMessage.toString());
            }

            @Override
            public String onSendMessage(NettyMessage nettyMessage, ChannelHandlerContext ctx) {
//                UIUtils.setTextAreaLog(controller.getEdit_api_info(), "向客户" + ctx.channel().remoteAddress() + "发送消息:" + nettyMessage.toString());
                return System.currentTimeMillis() + "";
            }

            @Override
            public void onError(Throwable e) {
//                UIUtils.setTextAreaLog(controller.getEdit_api_error(), e.getLocalizedMessage() + "\n" + MichaelUtils.getStackTrace(e));
            }
        });

        // 添加Hook，以便程序退出时先关闭相应的线程
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.close();
                client.disConnect();
                client.close();
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
            client.connect();
        } else if (event.getSource() == controller.getBtn_client_disconnect()) {
            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).channel().close();
            }
            client.disConnect();
//            client.close();
        } else if (event.getSource() == controller.getBtn_client_send()) {
            client.sendMessage("666");
        }
    }


    private void add(ChannelHandlerContext c) {
        boolean exist = false;
        for (int i = 0; i < clients.size(); i++) {
            if (c.channel().remoteAddress().equals(clients.get(i).channel().remoteAddress())) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            clients.add(c);
        }
        UIUtils.setText(controller.getLable_clients(), "实时客户数:" + clients.size());
    }

    private void remove(ChannelHandlerContext c) {
        for (int i = 0; i < clients.size(); i++) {
            if (c.channel().remoteAddress().equals(clients.get(i).channel().remoteAddress())) {
                clients.remove(i);
                break;
            }
        }
        UIUtils.setText(controller.getLable_clients(), "实时客户数:" + clients.size());
    }
    private void removeDied() {
        for (int i = 0; i < clients.size(); i++) {
            if (!clients.get(i).channel().isActive()) {
                clients.get(i).channel().close();
                clients.remove(i);
                break;
            }
        }
        UIUtils.setText(controller.getLable_clients(), "实时客户数:" + clients.size());
    }
}
