package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("ALL")
public class MainController implements Initializable {
    @FXML
    private Button btn_register_start_hot_key;

    @FXML
    private Button btn_start;

    @FXML
    private TextField edit_interval;

    @FXML
    private TextArea edit_point_info;

    @FXML
    private TextField edit_x;

    @FXML
    private TextField edit_y;

    public Button getBtn_register_start_hot_key() {
        return btn_register_start_hot_key;
    }

    public Button getBtn_start() {
        return btn_start;
    }

    public TextField getEdit_interval() {
        return edit_interval;
    }

    public TextArea getEdit_point_info() {
        return edit_point_info;
    }

    public TextField getEdit_x() {
        return edit_x;
    }

    public TextField getEdit_y() {
        return edit_y;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}