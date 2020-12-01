package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("ALL")
public class MainController implements Initializable {
    @FXML
    private Button btn_register_start_hot_key;

    @FXML
    private Button btn_clear;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_load;

    @FXML
    private Button btn_start;

    @FXML
    private ListView<?> lv_action;

    @FXML
    private TextField edit_interval;

    public Button getBtn_register_start_hot_key() {
        return btn_register_start_hot_key;
    }

    public Button getBtn_start() {
        return btn_start;
    }

    public TextField getEdit_interval() {
        return edit_interval;
    }

    public ListView<?> getLv_action() {
        return lv_action;
    }

    public Button getBtn_load() {
        return btn_load;
    }

    public Button getBtn_clear() {
        return btn_clear;
    }

    public Button getBtn_save() {
        return btn_save;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}