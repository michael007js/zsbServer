package sample.module;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.MainController;
import sample.adapter.BaseChoiceBoxAdapter;
import sample.module.adapter.LdSimulatorAdapter;
import utils.LogUtils;
import utils.StringUtils;
import utils.UIUtils;
import utils.WinRegistry;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class LeiDianModule extends BaseTabModule implements EventHandler<ActionEvent> {
    private MainController controller;
    private BaseChoiceBoxAdapter installAdapter = new BaseChoiceBoxAdapter<>();
    private LdSimulatorAdapter simulatorAdapter = new LdSimulatorAdapter();

    @Override
    public void initialize(MainController mainController) {
        LeiDian.getInstance().setInstallDirectory(null);
        this.controller = mainController;
        controller.getBtn_ld_create().setOnAction(this::handle);
        controller.getBtn_ld_remove().setOnAction(this::handle);
        controller.getBtn_ld_get_directory().setOnAction(this::handle);
        controller.getChoice_ld_install_path().getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() == -1) {
                    return;
                }
                initLD((String) controller.getChoice_ld_install_path().getItems().get(newValue.intValue()));
                getSimulatorList();
            }
        });
        controller.getEdit_ld_name().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (StringUtils.isEmpty(newValue.trim())) {
                    controller.getEdit_ld_name().setText("刷广告专用");
                }
            }
        });


        getInstallDirectory();

    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == controller.getBtn_ld_create()) {
            LeiDian.getInstance().add(controller.getEdit_ld_name().getText());
            getSimulatorList();
        } else if (event.getSource() == controller.getBtn_ld_remove()) {
            LeiDian.getInstance().removeByName(controller.getEdit_ld_name().getText());
            getSimulatorList();
        } else if (event.getSource() == controller.getBtn_ld_get_directory()) {
            LeiDian.getInstance().fps(0,50);
            //getInstallDirectory();
            // LogUtils.e(LeiDian.getInstance().setProParameter(0,LeiDian.MANUFACTURER,"333"));
            //  LogUtils.e(LeiDian.getInstance().getProParameter(0,LeiDian.MANUFACTURER));
        }
    }

    /**
     * 初始化雷电目录
     */
    private void initLD(String installDirectory) {
        LeiDian.getInstance().setInstallDirectory(installDirectory);
    }

    /**
     * 通过注册表获取安装目录
     */
    private void getInstallDirectory() {
        ArrayList<String> list = new ArrayList<>();
        String res = "";
        res = WinRegistry.valueForKey(WinRegistry.HKEY_CURRENT_USER, "Software\\ChangZhi\\dnplayer", "InstallDir");
        if (!StringUtils.isEmpty(res)) {
            list.add(res);
            res = "";
        }
        res = WinRegistry.valueForKey(WinRegistry.HKEY_CURRENT_USER, "Software\\ChangZhi2\\dnplayer", "InstallDir");
        if (!StringUtils.isEmpty(res)) {
            list.add(res);
            res = "";
        }
        res = WinRegistry.valueForKey(WinRegistry.HKEY_CURRENT_USER, "Software\\leidian\\ldplayer", "InstallDir");
        if (!StringUtils.isEmpty(res)) {
            list.add(res);
            res = "";
        }
        res = WinRegistry.valueForKey(WinRegistry.HKEY_CURRENT_USER, "Software\\leidian\\ldplayer64", "InstallDir");
        if (!StringUtils.isEmpty(res)) {
            list.add(res);
            res = "";
        }
        res = WinRegistry.valueForKey(WinRegistry.HKEY_CURRENT_USER, "Software\\baizhi\\lsplayer", "InstallDir");
        if (!StringUtils.isEmpty(res)) {
            list.add(res);
            res = "";
        }
        if (list.size() > 0) {
            UIUtils.setData(installAdapter, controller.getChoice_ld_install_path(), list, 0);
            initLD(list.get(0));
            getSimulatorList();
        }
    }

    /**
     * 获取模拟器列表
     */
    private void getSimulatorList() {
        UIUtils.setData(simulatorAdapter, controller.getLv_ld_simulator_list(), LeiDian.getInstance().getSimulatorList());
    }
}
