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
                '}';
    }

    public MouseRobotBean(String tag, int x, int y, int interval, int action) {
        this.tag = tag;
        this.x = x;
        this.y = y;
        this.interval = interval;
        this.action = action;
    }

    public boolean isSame(MouseRobotBean mouseRobotBean) {
        if (mouseRobotBean.tag != null && tag == null) {
            return false;
        }

        if (mouseRobotBean.tag == null && tag != null) {
            return false;
        }
        if (mouseRobotBean.tag != null && !mouseRobotBean.tag.equals(tag)) {
            return false;
        }
        if (mouseRobotBean.x != x) {
            return false;
        }

        if (mouseRobotBean.y != y) {
            return false;
        }

        if (mouseRobotBean.interval != interval) {
            return false;
        }

        if (mouseRobotBean.action != action) {
            return false;
        }

        if (mouseRobotBean.close != close) {
            return false;
        }

        if (mouseRobotBean.level != level) {
            return false;
        }

//        if (child != null) {
//            for (int i = 0; i < child.size(); i++) {
//                if (!child.get(i).isSame(this)) {
//                    return false;
//                }
//            }
//        }
        return true;
    }
}
