package sample.module.adapter;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sample.adapter.BaseListViewAdapter;
import sample.bean.LeiDianSimulatorBean;

public class LdSimulatorAdapter extends BaseListViewAdapter<LeiDianSimulatorBean> {
    private OnLdSimulatorAdapterCallBack onLdSimulatorAdapterCallBack;

    public LdSimulatorAdapter(OnLdSimulatorAdapterCallBack onLdSimulatorAdapterCallBack) {
        this.onLdSimulatorAdapterCallBack = onLdSimulatorAdapterCallBack;
    }


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
        Label label = new Label(item.getName() + "(" + item.getPosition() + ")" + (item.isAndroid() ? "已启动" : "未启动"));
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2 && event.getButton().name().equals(MouseButton.PRIMARY.name())){
                    if (onLdSimulatorAdapterCallBack != null) {
                        onLdSimulatorAdapterCallBack.onItemClick(item);
                    }
                }
            }
        });
        return label;
    }

    public interface OnLdSimulatorAdapterCallBack {
        void onItemClick(LeiDianSimulatorBean item);
    }
}
