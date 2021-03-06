package utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MichaelUtils {

    /**
     * 显示或隐藏windows程序
     *
     * @param title windows程序窗口标题
     * @param hide  是否隐藏
     */
    public static void hideOrShowWindowsProgram(String title, boolean hide) {
        // 第一个参数是Windows窗体的窗体类，第二个参数是窗体的标题。
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, title);
        if (hwnd == null) {
//            LogUtils.e("目标未启动");
            return;
        }

//        User32.INSTANCE.ShowWindow(hwnd, 9);        // SW_RESTORE
//        User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front

        //User32.INSTANCE.GetForegroundWindow() //获取现在前台窗口
        WinDef.RECT qqwin_rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, qqwin_rect);
        int qqwin_width = qqwin_rect.right - qqwin_rect.left;
        int qqwin_height = qqwin_rect.bottom - qqwin_rect.top;

        User32.INSTANCE.MoveWindow(hwnd, hide ? -9999 : 100, 100, qqwin_width, qqwin_height, true);
        //User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null);  // can be WM_QUIT in some occasio
    }

    /**
     * 重置windows 托盘
     *
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @param delay        复位延时
     * @throws InterruptedException
     */
    public static void resetWindowsToolbar(int screenWidth, int screenHeight, int delay) throws InterruptedException {
        //任务栏窗口
        WinDef.HWND hShellTrayWnd = User32.INSTANCE.FindWindow("Shell_TrayWnd", null);
        //任务栏右边托盘图标+时间区
        WinDef.HWND hTrayNotifyWnd = User32.INSTANCE.FindWindowEx(hShellTrayWnd, null, "TrayNotifyWnd", null);
        //不同系统可能有可能没有这层
        WinDef.HWND hSysPager = User32.INSTANCE.FindWindowEx(hTrayNotifyWnd, null, "SysPager", null);
        //托盘图标窗口
        WinDef.HWND hToolbarWindow32;
        if (hSysPager == null) {
            hToolbarWindow32 = User32.INSTANCE.FindWindowEx(hSysPager, null, "ToolbarWindow32", null);
        } else {
            hToolbarWindow32 = User32.INSTANCE.FindWindowEx(hTrayNotifyWnd, null, "ToolbarWindow32", null);
        }
        if (hToolbarWindow32 != null) {
            WinDef.RECT r = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hToolbarWindow32, r);
            int width = screenWidth - r.left;
            int height = screenHeight - r.top;
//            //从任务栏中间从左到右 MOUSEMOVE一遍，所有图标状态会被更新
            WinDef.POINT orig = new WinDef.POINT();
            if (User32.INSTANCE.GetCursorPos(orig)) {
                LogUtils.e(orig.x, orig.y);
            }
            LogUtils.e(width);
            for (int x = 0; x < width; x++) {
//                User32.INSTANCE.SendMessage(hToolbarWindow32, 0x0200/*WM_MOUSEMOVE,获取焦点*/, null, MAKELPARAM(x, height / 2));
                User32.INSTANCE.SetCursorPos(screenWidth - x, screenHeight - (height / 2));
            }
            Thread.sleep(delay);
            User32.INSTANCE.SetCursorPos(orig.x, orig.y);
        }
    }

    public static String launchCmd(String commandStr) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line = null;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return sb.toString();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
