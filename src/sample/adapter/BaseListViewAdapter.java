package sample.adapter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import sample.utils.LogUtils;

import java.util.ArrayList;

public abstract class BaseListViewAdapter<T> implements Callback<ListView<T>, ListCell<T>> {
    private ListView<T> listView;


    public void refreshData(ArrayList<T> list) {
        listView.setItems(null);
        ObservableList observableList = FXCollections.observableArrayList(list);
        listView.setItems(observableList);
    }

    public void setData(ListView<T> listView, ArrayList<T> list) {
        this.listView = listView;
        listView.getItems().clear();
        listView.setItems(FXCollections.observableArrayList(list));
        listView.setCellFactory(this);
    }


    @Override
    public ListCell<T> call(ListView<T> param) {
        return new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    this.setGraphic(bindView(item));
                } else {
                    this.setGraphic(new Label());
                }
            }
        };
    }


    protected abstract Node bindView(T item);


}
