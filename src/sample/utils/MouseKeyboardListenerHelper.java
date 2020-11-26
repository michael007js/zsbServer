package sample.utils;


import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeMonitorInfo;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.*;

public class MouseKeyboardListenerHelper  implements NativeMouseInputListener, NativeKeyListener, NativeMouseWheelListener {
    private static NativeMouseInputListener nativeMouseInputListener;
    private static NativeKeyListener nativeKeyListener;
    private static NativeMouseWheelListener nativeMouseWheelListener;
    private static boolean exitByEsc = true;
    private static MouseKeyboardListenerHelper listener=new MouseKeyboardListenerHelper();
    public static void setNativeMouseInputListener(NativeMouseInputListener nativeMouseInputListener) {
        MouseKeyboardListenerHelper.nativeMouseInputListener = nativeMouseInputListener;
    }

    public static void setNativeKeyListener(NativeKeyListener nativeKeyListener) {
        MouseKeyboardListenerHelper.nativeKeyListener = nativeKeyListener;
    }

    public static void setNativeMouseWheelListener(NativeMouseWheelListener nativeMouseWheelListener) {
        MouseKeyboardListenerHelper.nativeMouseWheelListener = nativeMouseWheelListener;
    }

    public static void register(boolean exitByEsc) {
        MouseKeyboardListenerHelper.exitByEsc = exitByEsc;
        try {
            GlobalScreen.addNativeKeyListener(listener);
            GlobalScreen.addNativeMouseListener(listener);
            GlobalScreen.addNativeMouseMotionListener(listener);
            GlobalScreen.addNativeMouseWheelListener(listener);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }
    }

    public static NativeMonitorInfo[] getNativeMonitorInfo() {
        return GlobalScreen.getNativeMonitors();
    }

    public static void unRegister() {
        try {
            GlobalScreen.removeNativeKeyListener(listener);
            GlobalScreen.removeNativeMouseListener(listener);
            GlobalScreen.removeNativeMouseMotionListener(listener);
            GlobalScreen.removeNativeMouseWheelListener(listener);
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException nativeHookException) {
            nativeHookException.printStackTrace();
        }
    }

    public void nativeMouseClicked(NativeMouseEvent e) {
        System.out.println("鼠标点击: " + e.getClickCount());
        if (nativeMouseInputListener != null) {
            nativeMouseInputListener.nativeMouseClicked(e);
        }
    }

    public void nativeMousePressed(NativeMouseEvent e) {
        System.out.println("鼠标按下: " + e.getButton());
        if (nativeMouseInputListener != null) {
            nativeMouseInputListener.nativeMousePressed(e);
        }
    }

    public void nativeMouseReleased(NativeMouseEvent e) {
        System.out.println("鼠标弹起: " + e.getButton());
        if (nativeMouseInputListener != null) {
            nativeMouseInputListener.nativeMouseReleased(e);
        }
    }

    public void nativeMouseMoved(NativeMouseEvent e) {
        System.out.println("鼠标移动: " + e.getX() + ", " + e.getY());
        if (nativeMouseInputListener != null) {
            nativeMouseInputListener.nativeMouseMoved(e);
        }
    }

    public void nativeMouseDragged(NativeMouseEvent e) {
        System.out.println("鼠标拖动: " + e.getX() + ", " + e.getY());
        if (nativeMouseInputListener != null) {
            nativeMouseInputListener.nativeMouseDragged(e);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        if (nativeKeyListener != null) {
            nativeKeyListener.nativeKeyTyped(nativeKeyEvent);
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        System.out.println("按键按下: " + NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
        if (nativeKeyListener != null) {
            nativeKeyListener.nativeKeyPressed(nativeKeyEvent);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        System.out.println("按键释放: " + NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
        if (nativeKeyListener != null) {
            nativeKeyListener.nativeKeyReleased(nativeKeyEvent);
        }
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE && exitByEsc) {
            unRegister();
        }
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
        System.out.println("鼠标滚轮滑动: " + nativeMouseWheelEvent.getWheelRotation());
        if (nativeMouseWheelListener != null) {
            nativeMouseWheelListener.nativeMouseWheelMoved(nativeMouseWheelEvent);
        }
    }
}