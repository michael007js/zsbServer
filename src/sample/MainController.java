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
    private Button btn_client_connect;

    @FXML
    private Button btn_client_disconnect;

    @FXML
    private Button btn_client_send;

    @FXML
    private Label lable_clients;

    @FXML
    private Button btn_ld_get_directory;

    @FXML
    private Button btn_ld_create;

    @FXML
    private Button btn_ld_remove;

    @FXML
    private ChoiceBox<?> choice_ld_install_path;

    @FXML
    private ListView<?> lv_ld_simulator_list;

    @FXML
    private TextField edit_ld_name;

    @FXML
    private ListView<?> lv_ld_simulator_action;

    @FXML
    private ChoiceBox<?> choice_action;

    @FXML
    private Button btn_ld_action_delete;

    @FXML
    private Button btn_ld_action_add;

    @FXML
    private TextField edit_ld_delay;

    @FXML
    private TextField edit_ld_package;

    @FXML
    private TextField edit_ld_path;

    @FXML
    private Button btn_ld_action_do;

    @FXML
    private TextField edit_ld_delay_adb;

    @FXML
    private CheckBox cb_install_by_leidian;

    @FXML
    private CheckBox cb_install_by_adb;

    @FXML
    private TextField edit_ld_time_auto;


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

    public Button getBtn_client_connect() {
        return btn_client_connect;
    }

    public Button getBtn_client_disconnect() {
        return btn_client_disconnect;
    }

    public Button getBtn_client_send() {
        return btn_client_send;
    }

    public Label getLable_clients() {
        return lable_clients;
    }

    public Button getBtn_ld_get_directory() {
        return btn_ld_get_directory;
    }

    public Button getBtn_ld_create() {
        return btn_ld_create;
    }

    public Button getBtn_ld_remove() {
        return btn_ld_remove;
    }

    public ChoiceBox<?> getChoice_ld_install_path() {
        return choice_ld_install_path;
    }

    public ListView<?> getLv_ld_simulator_list() {
        return lv_ld_simulator_list;
    }

    public TextField getEdit_ld_name() {
        return edit_ld_name;
    }

    public ChoiceBox<?> getChoice_action() {
        return choice_action;
    }

    public ListView<?> getLv_ld_simulator_action() {
        return lv_ld_simulator_action;
    }

    public Button getBtn_ld_action_delete() {
        return btn_ld_action_delete;
    }

    public Button getBtn_ld_action_add() {
        return btn_ld_action_add;
    }

    public TextField getEdit_ld_delay() {
        return edit_ld_delay;
    }

    public TextField getEdit_ld_package() {
        return edit_ld_package;
    }

    public TextField getEdit_ld_path() {
        return edit_ld_path;
    }

    public Button getBtn_ld_action_do() {
        return btn_ld_action_do;
    }

    public TextField getEdit_ld_delay_adb() {
        return edit_ld_delay_adb;
    }

    public CheckBox getCb_install_by_leidian() {
        return cb_install_by_leidian;
    }

    public CheckBox getCb_install_by_adb() {
        return cb_install_by_adb;
    }

    public TextField getEdit_ld_time_auto() {
        return edit_ld_time_auto;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}