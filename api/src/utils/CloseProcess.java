package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class CloseProcess {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
     closeProcess("dnplayer.exe");
    }

    /**
     * 检测程序。
     *
     * @param processName 线程的名字，请使用准确的名字
     * @return 找到返回true, 没找到返回false
     */
    public static boolean closeProcess(String processName) {
        boolean flag = false;
        Properties props = System.getProperties();
        try {
            if (props.getProperty("os.name").contains("Windows")) {
                Process p = Runtime.getRuntime().exec("cmd /c tasklist ");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream os = p.getInputStream();
                byte b[] = new byte[256];
                while (os.read(b) > 0) {
                    baos.write(b);
                }
                String s = baos.toString();
                if (s.indexOf(processName) >= 0) {
                    Runtime.getRuntime().exec("taskkill /im " + processName + " /f");
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ps -ef | grep " + processName});
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream os = p.getInputStream();
                byte b[] = new byte[256];
                while (os.read(b) > 0) {
                    baos.write(b);
                }
                String s = baos.toString();
                if (s.indexOf(processName) >= 0) {
                    String[] cmd = {"sh", "-c", "killall -9 " + processName};
                    Runtime.getRuntime().exec(cmd);
                    flag = true;
                } else {
                    flag = false;
                }
            }
        } catch (IOException ioe) {
        }
        return flag;
    }
}