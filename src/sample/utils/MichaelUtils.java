package sample.utils;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MichaelUtils {


    /**
     * 调用windows本地目录程序
     */
    public static boolean launchEXE(String exePath) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cmd /c " + exePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 调用windows本地目录程序
     */
    public static boolean launchEXEByRundll32(String exePath) {
        Runtime runtime = Runtime.getRuntime();
        final String cmd = "rundll32 url.dll FileProtocolHandler file://" + exePath;
        try {
            runtime.exec(cmd);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * RxJava包装
     */
    public static <T> void goObserver(Observable<T> observable, DisposableObserver<T> disposableObserver) {
        goObserver(observable, Schedulers.io(), disposableObserver);
    }

    public static <T> void goObserver(Observable<T> observable, Scheduler scheduler, DisposableObserver<T> disposableObserver) {
        observable.subscribeOn(scheduler)
                .subscribe(disposableObserver);
    }

    /**
     * 获取控件
     *
     * @param application 应用基类
     * @param layoutName  布局名称  main.fxml
     * @param widgetName  控件id #edit_database_address
     * @return 控件对象, 需强转
     */
    private static Object createView(Application application, String layoutName, String widgetName) throws IOException {
        Parent parent = FXMLLoader.load(application.getClass().getResource("main.fxml"));
        return parent.lookup("#edit_database_address");
    }


    /**
     * 取两个文本之间的文本值
     *
     * @param text  源文本 比如：欲取全文本为 12345
     * @param left  文本前面
     * @param right 后面文本
     * @return 返回 String
     */
    public static String getSubString(String text, String left, String right) {
        String result = "";
        int zLen;
        if (left == null || left.isEmpty()) {
            zLen = 0;
        } else {
            zLen = text.indexOf(left);
            if (zLen > -1) {
                zLen += left.length();
            } else {
                zLen = 0;
            }
        }
        int yLen = text.indexOf(right, zLen);
        if (yLen < 0 || right == null || right.isEmpty()) {
            yLen = text.length();
        }
        result = text.substring(zLen, yLen);
        return result;
    }


    /**
     * 获取栈轨迹
     */
    public static String getStackTrace() {
        StringBuffer err = new StringBuffer();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            err.append("\tat ");
            err.append(stack[i].toString());
            err.append("\n");
        }
        return err.toString();
    }

    /**
     * 获取错误轨迹
     */
    public static String getStackTrace(Throwable throwable) {
        StringBuffer err = new StringBuffer();
        err.append(throwable.getLocalizedMessage());
        StackTraceElement[] stack = throwable.getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            err.append("\tat ");
            err.append(stack[i].toString());
            err.append("\n");
        }
        return err.toString();
    }

    /**
     * 将Unicode转为UTF-8中文
     *
     * @param str 入参字符串
     * @return
     */
    public static String decodeUnicode(String str) {
        if (str == null) {
            return "";
        }
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        if (p == null) {
            return "";
        }
        Matcher m = p.matcher(str);
        int start = 0;
        int start2 = 0;
        StringBuffer sb = new StringBuffer();
        while (m.find(start)) {
            start2 = m.start();
            if (start2 > start) {
                String seg = str.substring(start, start2);
                sb.append(seg);
            }
            String code = m.group(1);
            int i = Integer.valueOf(code, 16);
            byte[] bb = new byte[4];
            bb[0] = (byte) ((i >> 8) & 0xFF);
            bb[1] = (byte) (i & 0xFF);
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append(String.valueOf(set.decode(b)).trim());
            start = m.end();
        }
        start2 = str.length();
        if (start2 > start) {
            String seg = str.substring(start, start2);
            sb.append(seg);
        }
        return sb.toString();
    }
}
