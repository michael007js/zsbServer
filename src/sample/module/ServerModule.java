package sample.module;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.MainController;
import sample.utils.MichaelUtils;
import sample.utils.UIUtils;
import socket.AppConstant;
import socket.ConnectClient;
import socket.ConnectServer;
import socket.OnServerCallBack;

public class ServerModule extends BaseTabModule implements EventHandler<ActionEvent>, OnServerCallBack {
    private MainController controller;
    private ConnectServer connectServer = new ConnectServer();
    private ConnectClient connectClient = new ConnectClient();

    @Override
    public void initialize(MainController mainController) {

        this.controller = mainController;
        connectServer.setOnServerCallBack(this);
        controller.getBtn_start_api().setOnAction(this::handle);
        controller.getBtn_stop_api().setOnAction(this::handle);
        controller.getBtn_client().setOnAction(this::handle);
    }

    @Override
    public boolean isBroadCast() {
        return false;
    }

    @Override
    public String onSendMessage(String receivedMsg) {
        UIUtils.setTextAreaLog(controller.getEdit_api_info(), "收到客户消息" + receivedMsg);
        return "来自服务端的消息" + System.currentTimeMillis();
    }

    @Override
    public void onLog(String log) {
        UIUtils.setTextAreaLog(controller.getEdit_api_info(), log);
    }

    @Override
    public void onError(Throwable e) {
        UIUtils.setTextAreaLog(controller.getEdit_api_error(), e.getLocalizedMessage() + "\n" + MichaelUtils.getStackTrace(e));
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == controller.getBtn_start_api()) {
            connectServer.start();
        } else if (event.getSource() == controller.getBtn_stop_api()) {
            connectServer.stop();
        } else if (event.getSource() == controller.getBtn_client()) {
            if (!connectClient.isConnected()) {
                connectClient.connect("127.0.0.1", AppConstant.PORT);
            }
            connectClient.send(System.currentTimeMillis() + "");
        }
    }
}
