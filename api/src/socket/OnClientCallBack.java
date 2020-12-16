package socket;

public interface OnClientCallBack {
    void onReceivedMessage(String message);

    void onError(Throwable e);
}
