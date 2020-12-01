package sample.utils;


import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeMonitorInfo;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

public class MouseKeyboardListenerHelper extends GlobalScreen implements NativeMouseInputListener, NativeKeyListener, NativeMouseWheelListener {
    private NativeMouseInputListener nativeMouseInputListener;
    private NativeKeyListener nativeKeyListener;
    private NativeMouseWheelListener nativeMouseWheelListener;
    private boolean exitByEsc = true;

    public void setNativeMouseInputListener(NativeMouseInputListener nativeMouseInputListener) {
        MouseKeyboardListenerHelper.this.nativeMouseInputListener = nativeMouseInputListener;
    }

    public void setNativeKeyListener(NativeKeyListener nativeKeyListener) {
        MouseKeyboardListenerHelper.this.nativeKeyListener = nativeKeyListener;
    }

    public void setNativeMouseWheelListener(NativeMouseWheelListener nativeMouseWheelListener) {
        MouseKeyboardListenerHelper.this.nativeMouseWheelListener = nativeMouseWheelListener;
    }

    public void register(boolean exitByEsc) {
        MouseKeyboardListenerHelper.this.exitByEsc = exitByEsc;
        try {
            addNativeKeyListener(this);
            addNativeMouseListener(this);
            addNativeMouseMotionListener(this);
            addNativeMouseWheelListener(this);
            registerNativeHook();
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }
    }

    public NativeMonitorInfo[] getNativeMonitorInfo() {
        return getNativeMonitors();
    }

    public void unRegister() {
        try {
            removeNativeKeyListener(this);
            removeNativeMouseListener(this);
            removeNativeMouseMotionListener(this);
            removeNativeMouseWheelListener(this);
            unregisterNativeHook();
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