package sample.module;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.MainController;
import sample.adapter.BaseChoiceBoxAdapter;
import sample.bean.LeiDianSimulatorBean;
import sample.bean.MobileBrand;
import sample.module.adapter.LdSimulatorAdapter;
import utils.*;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class LeiDianModule extends BaseTabModule implements EventHandler<ActionEvent> {
    private MainController controller;
    private BaseChoiceBoxAdapter installAdapter = new BaseChoiceBoxAdapter<>();
    private LdSimulatorAdapter simulatorAdapter = new LdSimulatorAdapter(new LdSimulatorAdapter.OnLdSimulatorAdapterCallBack() {
        @Override
        public void onItemClick(LeiDianSimulatorBean item) {
            LeiDian.getInstance().launch(item.getPosition());
        }
    });

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
            //getInstallDirectory();
            autoCreateLaunchInstallRunApk(true);
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

    private void autoCreateLaunchInstallRunApk(boolean deteleAll) {
       new Thread(){
           @Override
           public void run() {
               super.run();
               ArrayList<LeiDianSimulatorBean> list = LeiDian.getInstance().getSimulatorList();
               LeiDian.getInstance().quit(-1);
               for (int i = 0; i < list.size(); i++) {
                   if (deteleAll || list.get(i).getName().equals(controller.getEdit_ld_name().getText())) {
                       LeiDian.getInstance().removeByIndex(list.get(i).getPosition());
                   }
               }
               LeiDian.getInstance().add(controller.getEdit_ld_name().getText());
               getSimulatorList();
               LeiDian.getInstance().modifyDisplay(1, 720, 1080, 240);
               MobileBrand mobileBrand = MobileBrandUtils.getRandomMobileBrand();
               LeiDian.getInstance().modifyManufacturer(1, mobileBrand.getBrand());
               LeiDian.getInstance().modifyModel(1, mobileBrand.getTitle());
               LeiDian.getInstance().modifyPhoneNumber(1, RandomPhoneNumber.createMobile(1));
               LeiDian.getInstance().modifyImsi(1, true, 0);
               LeiDian.getInstance().modifyImei(1, true, 0);
               LeiDian.getInstance().modifySimSerial(1, true, 0);
               LeiDian.getInstance().modifyAndroidId(1, true, 0);
               LeiDian.getInstance().modifyMac(1, true, "");
               LeiDian.getInstance().installApp(1, "C:/Users/Administrator/Desktop/d0f205e61a15ed8c5658c5c102107b5c_40578.apk");
               try {
                   sleep(20000);
                   LeiDian.getInstance().runApp(1,"com.supercell.clashofclans.guopan");
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }.start();
    }
}
