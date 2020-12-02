package sample.bean;

import java.util.ArrayList;

public class MouseRobotBean {
    public String tag;
    public int x, y;
    public int interval;
    public int action = 1;//1左键  2右键
    public boolean close = true;
    public int level;
    public ArrayList<MouseRobotBean> child = new ArrayList<>();

    @Override
    public String toString() {
        return "RobotBean{" +
                "tag='" + tag + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", interval=" + interval +
                ", action=" + action +
                ", close=" + close +
                ", level=" + level +
                '}';
    }

    public MouseRobotBean(String tag, int x, int y, int interval, int action, boolean close, int level, ArrayList<MouseRobotBean> child) {
        this.tag = tag;
        this.x = x;
        this.y = y;
        this.interval = interval;
        this.action = action;
        this.close = close;
        this.level = level;
        this.child = child;
    }


    public boolean isSame(MouseRobotBean mouseRobotBean) {
       return mouseRobotBean.toString().equals(toString());
    }
}
