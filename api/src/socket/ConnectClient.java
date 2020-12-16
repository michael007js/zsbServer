package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Michael Huang
 */
public class ConnectClient {
    Socket s = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    private boolean bConnected = false;

    public boolean isConnected() {
        return bConnected;
    }

    public void connect(String ip,int port) {
        try {
            s = new Socket(ip, port);
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            System.out.println("~~~~~~~~连接成功~~~~~~~~!");
            bConnected = true;
        } catch (IOException e) {
            bConnected = false;
            e.printStackTrace();
        }

    }

    public void disconnect() {
        try {
            bConnected = false;
            dos.close();
            dis.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send(String msg) {
        new Thread(new Client()).start();
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    class Client implements Runnable {

        public void run() {
            try {
                while (bConnected) {
                    String str = dis.readUTF();
                    System.out.println(str);
                }
            } catch (Exception e) {
                System.out.println("退出了，bye!");
                e.printStackTrace();
            }

        }

    }
}
