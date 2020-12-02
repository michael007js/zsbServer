package sample.adapter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.ArrayList;

public abstract class BaseListViewAdapter<T> implements Callback<ListView<T>, ListCell<T>> {
    protected ListView<T> listView;


    public void refreshData(ArrayList<T> list) {
        listView.setItems(null);
        ObservableList observableList = FXCollections.observableArrayList(list);
        listView.setItems(observableList);
    }

    public void setData(ListView<T> listView, ArrayList<T> list) {
        this.listView = listView;
        listView.setItems(null);
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
                    this.setGraphic(null);
                }
            }
        };
    }


    protected abstract Node bindView(T item);


}
