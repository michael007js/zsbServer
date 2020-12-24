package sample.module.adapter;

import javafx.scene.Node;
import javafx.scene.control.Label;
import sample.adapter.BaseListViewAdapter;
import sample.bean.LeiDianSimulatorBean;

public class LdSimulatorAdapter extends BaseListViewAdapter<LeiDianSimulatorBean> {


    @Override
    public int getPosition(LeiDianSimulatorBean item) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).toString().equals(item.toString())) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected Node bindView(LeiDianSimulatorBean item) {
        return new Label(item.getName() + "(" + item.getPosition() + ")" + (item.isAndroid() ? "已启动" : "未启动"));
    }
}
