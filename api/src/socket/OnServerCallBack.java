package socket;

public interface OnServerCallBack {

    boolean isBroadCast();

    String onSendMessage(String receivedMsg);

    void onLog(String log);

    void onError(Throwable e);
}
