package sample.bean;

import java.util.ArrayList;

public class SaveConfigBean {
    private int totalInterval;
    private ArrayList<MouseRobotBean> mouse;

    public int getTotalInterval() {
        return totalInterval;
    }

    public void setTotalInterval(int totalInterval) {
        this.totalInterval = totalInterval;
    }

    public ArrayList<MouseRobotBean> getMouse() {
        return mouse;
    }

    public void setMouse(ArrayList<MouseRobotBean> mouse) {
        this.mouse = mouse;
    }
}
