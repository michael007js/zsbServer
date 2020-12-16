package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
    private Label lable_point;

    @FXML
    private Label lable_current_action;

    @FXML
    private ListView<?> lv_action;

    @FXML
    private TextField edit_interval;

    @FXML
    private TextField edit_wait_time;

    @FXML
    private CheckBox cb_auto_calculation_time;

    @FXML
    private TextArea edit_api_info;

    @FXML
    private TextArea edit_api_error;

    @FXML
    private Button btn_start_api;

    @FXML
    private Button btn_stop_api;

    @FXML
    private Button btn_client;

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

    public Label getLable_point() {
        return lable_point;
    }

    public CheckBox getCb_auto_calculation_time() {
        return cb_auto_calculation_time;
    }

    public Label getLable_current_action() {
        return lable_current_action;
    }

    public TextField getEdit_wait_time() {
        return edit_wait_time;
    }

    public TextArea getEdit_api_info() {
        return edit_api_info;
    }

    public TextArea getEdit_api_error() {
        return edit_api_error;
    }

    public Button getBtn_start_api() {
        return btn_start_api;
    }

    public Button getBtn_stop_api() {
        return btn_stop_api;
    }

    public Button getBtn_client() {
        return btn_client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}