package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
//https://www.cnblogs.com/jtlgb/p/8757587.html
public class ConnectServer {
    private boolean started = false;
    private ServerSocket ss = null;
    private static List<Client> clients = new ArrayList<Client>();
    private OnServerCallBack onServerCallBack;


    public void setOnServerCallBack(OnServerCallBack onServerCallBack) {
        this.onServerCallBack = onServerCallBack;
    }

    public static void main(String[] args) {
        new ConnectServer().start();
    }

    public void start() {
        try {
            ss = new ServerSocket(AppConstant.PORT);
            started = true;
            if (onServerCallBack != null) {
                onServerCallBack.onLog("端口已开启,占用" + AppConstant.PORT + "端口号......");
            }
        } catch (Exception e) {
            if (onServerCallBack != null) {
                onServerCallBack.onLog("端口使用中......");
                onServerCallBack.onLog("请关掉相关程序并重新运行服务......");
                onServerCallBack.onError(e);
            }
            started = false;
        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (onServerCallBack != null) {
                        onServerCallBack.onLog("等待客户端连接中......");
                    }
                    while (started) {
                        if (!ss.isClosed()) {
                            Client client = new Client(ss.accept(), onServerCallBack);
                            if (onServerCallBack != null) {
                                onServerCallBack.onLog("客户端已连接......");
                            }
                            client.run();
                            clients.add(client);
                        }
                    }
                } catch (IOException e) {
                    if (onServerCallBack != null) {
                        onServerCallBack.onError(e);
                    }
                } finally {
                    try {
                        closeSocket(ss);
                    } catch (IOException e) {
                        if (onServerCallBack != null) {
                            onServerCallBack.onError(e);
                        }
                    }
                }
            }
        }.start();

    }

    private void closeSocket(ServerSocket socket) throws IOException {
        if (ss != null) {
            socket.close();
        }
    }

    public void stop() {
        started = false;
        try {
            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).close();
            }
            clients.clear();
            closeSocket(ss);
            if (onServerCallBack != null) {
                onServerCallBack.onLog("服务已关闭......");
            }
        } catch (IOException e) {
            if (onServerCallBack != null) {
                onServerCallBack.onError(e);
            }
        }
    }

    public static class Client extends Thread {
        private Socket s;
        private InputStream in;
        private OutputStream out;
        private DataInputStream dis = null;
        private DataOutputStream dos = null;
        private boolean bConnected = false;
        private OnServerCallBack onServerCallBack;


        private Client(Socket s, OnServerCallBack onServerCallBack) {
            this.s = s;
            System.out.println(s.getInetAddress().getHostAddress());
            this.onServerCallBack = onServerCallBack;
            try {
                in=s.getInputStream();
                out=s.getOutputStream();
                dis = new DataInputStream(in);
                dos = new DataOutputStream(out);
                bConnected = true;
            } catch (IOException e) {
                if (onServerCallBack != null) {
                    onServerCallBack.onError(e);
                }
            }
        }

        public void send(String str) {
            try {
                dos.writeUTF(str);
            } catch (IOException e) {

                if (onServerCallBack != null) {
                    onServerCallBack.onLog("向客户端出现异常，该客户端已被移除......");
                    onServerCallBack.onError(e);
                }
                close();
                clients.remove(this);

            }
        }


        @Override
        public void run() {
            try {
                while (bConnected) {
                    System.out.println(111);
                    String str = dis.readUTF();
                    System.out.println(222);
                    if (onServerCallBack != null) {
                        if (onServerCallBack.isBroadCast()) {
                            for (int i = 0; i < clients.size(); i++) {
                                Client c = clients.get(i);
                                c.send(str);
                                c.start();
                            }
                        } else {
                            send(onServerCallBack.onSendMessage(str));
                        }
                    }
                }
            } catch (EOFException e) {
                if (onServerCallBack != null) {
                    onServerCallBack.onLog("发现客户端已断开，该客户端已被移除......");
                    onServerCallBack.onError(e);
                }
                close();
                clients.remove(this);
            } catch (IOException e) {
                if (onServerCallBack != null) {
                    onServerCallBack.onError(e);
                }
            } finally {
                close();
            }
        }

        public void close() {
            bConnected = false;
            try {
                if (in != null)
                    in.close();
                if (dis != null)
                    dis.close();
                if (out != null)
                    out.close();
                if (dos != null)
                    dos.close();
                if (s != null) {
                    s.close();
                }
                this.interrupt();

            } catch (IOException e) {
                if (onServerCallBack != null) {
                    onServerCallBack.onError(e);
                }
            }
        }
    }


}
