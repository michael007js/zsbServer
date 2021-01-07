package sample.module;

import io.netty.channel.ChannelHandlerContext;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.MainController;
import socket.AppConstant;
import socket.callback.OnServerCallBack;
import socket.client.NettyClient;
import socket.message.NettyMessage;
import socket.server.NettyServer;
import utils.LogUtils;
import utils.UIUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class ServerModule extends BaseTabModule implements EventHandler<ActionEvent> {
    private MainController controller;
    private ArrayList<ChannelHandlerContext> clients = new ArrayList<>();
    private NettyServer server = new NettyServer(AppConstant.PORT);
    private NettyClient client = new NettyClient(AppConstant.HOST, AppConstant.PORT);
    private LeiDianModule leiDianModule;

    public void setLeiDianModule(LeiDianModule leiDianModule) {
        this.leiDianModule = leiDianModule;
    }

    @Override
    public void initialize(MainController mainController) {
        LeiDian.getInstance().setInstallDirectory(null);
        this.controller = mainController;
        controller.getBtn_start_api().setOnAction(this::handle);
        controller.getBtn_stop_api().setOnAction(this::handle);
        controller.getBtn_client_send().setOnAction(this::handle);
        controller.getBtn_client_connect().setOnAction(this::handle);
        controller.getBtn_client_disconnect().setOnAction(this::handle);
        controller.getCb_restart_auto_task().setOnAction(this::handle);
        controller.getEdit_ld_time_auto().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                createAutoTask();
            }
        });
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
                LogUtils.e(nettyMessage.bodyToString());
                if ("auto".equals(nettyMessage.bodyToString())) {
                    leiDianModule.autoCreateLaunchInstallRunApk(true);
                } else if ("success".equals(nettyMessage.bodyToString())) {
                    leiDianModule.autoSuccessTask();
                } else if ("fail".equals(nettyMessage.bodyToString())) {
                    leiDianModule.autoFailTask();
                }
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
        server.start();
        // 添加Hook，以便程序退出时先关闭相应的线程
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.close();
                client.disConnect();
                client.close();
            }
        });
        createAutoTask();
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
        } else if (event.getSource() == controller.getCb_restart_auto_task()) {
            createAutoTask();
        }
    }


    /**
     * 添加链接
     */
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

    /**
     * 移除链接
     */
    private void remove(ChannelHandlerContext c) {
        for (int i = 0; i < clients.size(); i++) {
            if (c.channel().remoteAddress().equals(clients.get(i).channel().remoteAddress())) {
                clients.remove(i);
                break;
            }
        }
        UIUtils.setText(controller.getLable_clients(), "实时客户数:" + clients.size());
    }

    /**
     * 移除失效的链接
     */
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

    private DisposableObserver disposableObserver;

    /**
     * 创建自动任务
     */
    private void createAutoTask() {
        if (controller.getCb_restart_auto_task().selectedProperty().getValue().booleanValue()) {
            if (disposableObserver != null) {
                if (!disposableObserver.isDisposed()) {
                    disposableObserver.dispose();
                }
            }
            disposableObserver = new DisposableObserver<Long>() {
                @Override
                public void onNext(Long aLong) {
                    removeDied();
                    if (clients.size() == 0) {
                        leiDianModule.autoCreateLaunchInstallRunApk(true);
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };
            int minute = 2;
            try {
                minute = Integer.parseInt(controller.getEdit_ld_time_auto().getText());
                minute = Math.max(minute, 1);
            } catch (Exception e) {
                minute = 2;
            } finally {
                Observable.interval(minute, TimeUnit.MINUTES, Schedulers.newThread())
                        .subscribe(disposableObserver);
            }
        } else {
            if (disposableObserver != null) {
                if (!disposableObserver.isDisposed()) {
                    disposableObserver.dispose();
                }
            }
        }


    }
}
