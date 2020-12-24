package sample.bean;

public class LeiDianSimulatorBean {
    //索引
    private int position;
    //标题
    private String name;
    //顶层窗口句柄
    private long parentWindowHandle;
    //绑定窗口句柄
    private long bindWindowHandle;
    //是否进入android
    private boolean isAndroid;
    //进程PID
    private long pid;
    //VBox进程PID
    private long vBoxPid;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentWindowHandle() {
        return parentWindowHandle;
    }

    public void setParentWindowHandle(long parentWindowHandle) {
        this.parentWindowHandle = parentWindowHandle;
    }

    public long getBindWindowHandle() {
        return bindWindowHandle;
    }

    public void setBindWindowHandle(long bindWindowHandle) {
        this.bindWindowHandle = bindWindowHandle;
    }

    public boolean isAndroid() {
        return isAndroid;
    }

    public void setAndroid(boolean android) {
        isAndroid = android;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getvBoxPid() {
        return vBoxPid;
    }

    public void setvBoxPid(long vBoxPid) {
        this.vBoxPid = vBoxPid;
    }

    @Override
    public String toString() {
        return "LeiDianSimulatorBean{" +
                "position=" + position +
                ", name='" + name + '\'' +
                ", parentWindowHandle=" + parentWindowHandle +
                ", bindWindowHandle=" + bindWindowHandle +
                ", isAndroid=" + isAndroid +
                ", pid=" + pid +
                ", vBoxPid=" + vBoxPid +
                '}';
    }
}
