package sample.module.adapter;

import javafx.scene.Node;
import javafx.scene.control.Label;
import sample.adapter.BaseListViewAdapter;
import sample.module.LeiDian;

public class LdActionAdapter extends BaseListViewAdapter<LeiDian.Action> {


    @Override
    public int getPosition(LeiDian.Action item) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).toString().equals(item.toString())) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected Node bindView(LeiDian.Action item) {
        return  new Label(item.toString());
    }

}
