package sample.bean;

public class RobotBean {
    public String tag;
    public int x, y;
    public int interval;
    public int action = 1;//1左键  2右键

    public RobotBean(String tag, int x, int y, int interval, int action) {
        this.tag = tag;
        this.x = x;
        this.y = y;
        this.interval = interval;
        this.action = action;
    }

    public boolean isSame(RobotBean robotBean) {
        if (robotBean.tag != null && tag == null) {
            return false;
        }

        if (robotBean.tag == null && tag != null) {
            return false;
        }
        if (robotBean.tag != null && !robotBean.tag.equals(tag)) {
            return false;
        }
        if (robotBean.x != x) {
            return false;
        }

        if (robotBean.y != y) {
            return false;
        }

        if (robotBean.interval != interval) {
            return false;
        }

        if (robotBean.action != action) {
            return false;
        }

        return true;
    }
}
