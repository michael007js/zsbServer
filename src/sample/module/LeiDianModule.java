package sample.module;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.MainController;
import sample.adapter.BaseChoiceBoxAdapter;
import sample.bean.LeiDianSimulatorBean;
import sample.bean.MobileBrand;
import sample.module.adapter.LdActionAdapter;
import sample.module.adapter.LdSimulatorAdapter;
import utils.*;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class LeiDianModule extends BaseTabModule implements EventHandler<ActionEvent> {
    private MainController controller;
    private BaseChoiceBoxAdapter installAdapter = new BaseChoiceBoxAdapter<>();
    private BaseChoiceBoxAdapter actionAdapter = new BaseChoiceBoxAdapter<>();
    private LdSimulatorAdapter simulatorAdapter = new LdSimulatorAdapter(new LdSimulatorAdapter.OnLdSimulatorAdapterCallBack() {
        @Override
        public void onItemClick(LeiDianSimulatorBean item) {
            LeiDian.getInstance().launch(item.getPosition());
        }
    });
    private LdActionAdapter ldActionAdapter = new LdActionAdapter();

    @Override
    public void initialize(MainController mainController) {
        LeiDian.getInstance().setInstallDirectory(null);
        this.controller = mainController;
        controller.getBtn_ld_create().setOnAction(this::handle);
        controller.getBtn_ld_remove().setOnAction(this::handle);
        controller.getBtn_ld_get_directory().setOnAction(this::handle);
        controller.getBtn_ld_action_add().setOnAction(this::handle);
        controller.getBtn_ld_action_delete().setOnAction(this::handle);
        controller.getBtn_ld_action_do().setOnAction(this::handle);
        controller.getCb_install_by_leidian().setOnAction(this::handle);
        controller.getCb_install_by_adb().setOnAction(this::handle);
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

        createActionList();
        createAutoAction(true);
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
            getInstallDirectory();
        } else if (event.getSource() == controller.getBtn_ld_action_add()) {
            ldActionAdapter.list.add(actions.get(controller.getChoice_action().getSelectionModel().getSelectedIndex()));
            UIUtils.setData(ldActionAdapter, controller.getLv_ld_simulator_action(), ldActionAdapter.list);
        } else if (event.getSource() == controller.getBtn_ld_action_delete()) {
            if (controller.getLv_ld_simulator_action().getSelectionModel().getSelectedIndex() > -1) {
                ldActionAdapter.list.remove(controller.getLv_ld_simulator_action().getSelectionModel().getSelectedIndex());
                UIUtils.setData(ldActionAdapter, controller.getLv_ld_simulator_action(), ldActionAdapter.list);
            }
        } else if (event.getSource() == controller.getBtn_ld_action_do()) {
            autoCreateLaunchInstallRunApk(true);
        }else if (event.getSource() == controller.getCb_install_by_leidian()) {
            controller.getCb_install_by_leidian().selectedProperty().setValue(true);
            controller.getCb_install_by_adb().selectedProperty().setValue(false);
            createAutoAction(true);
        }else if(event.getSource() == controller.getCb_install_by_adb()) {
            controller.getCb_install_by_leidian().selectedProperty().setValue(false);
            controller.getCb_install_by_adb().selectedProperty().setValue(true);
            createAutoAction(false);
        }
    }

    private ArrayList<LeiDian.Action> actions = new ArrayList<>();

    /**
     * 创建自动任务列表
     */
    private void createAutoAction(boolean installByLeidian) {
        ldActionAdapter.list.clear();
        ldActionAdapter.list.add(LeiDian.Action.QUIT);
        ldActionAdapter.list.add(LeiDian.Action.REMOVE);
        ldActionAdapter.list.add(LeiDian.Action.ADD);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_DISPLAY);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_MANUFACTURER);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_MODEL);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_IMEI);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_IMSI);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_SIM_SERIAL);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_ANDROID_ID);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_MAC);
        ldActionAdapter.list.add(LeiDian.Action.MODIFY_PHONE_NUMBER);

        if (installByLeidian){
            ldActionAdapter.list.add(LeiDian.Action.INSTALL_APP);
        }else {
            ldActionAdapter.list.add(LeiDian.Action.LAUNCH);
            ldActionAdapter.list.add(LeiDian.Action.ADB_INSTALL_APP);
        }
        ldActionAdapter.list.add(LeiDian.Action.RUN_APP);
        UIUtils.setData(ldActionAdapter, controller.getLv_ld_simulator_action(), ldActionAdapter.list);
    }

    /**
     * 创建动作列表
     */
    private void createActionList() {
        actions.add(LeiDian.Action.ADD);
        actions.add(LeiDian.Action.REMOVE);
        actions.add(LeiDian.Action.LAUNCH);
        actions.add(LeiDian.Action.REBOOT);
        actions.add(LeiDian.Action.QUIT);
        actions.add(LeiDian.Action.MODIFY_DISPLAY);
        actions.add(LeiDian.Action.MODIFY_CPU);
        actions.add(LeiDian.Action.MODIFY_MEMORY);
        actions.add(LeiDian.Action.MODIFY_MANUFACTURER);
        actions.add(LeiDian.Action.MODIFY_MODEL);
        actions.add(LeiDian.Action.MODIFY_IMEI);
        actions.add(LeiDian.Action.MODIFY_IMSI);
        actions.add(LeiDian.Action.MODIFY_SIM_SERIAL);
        actions.add(LeiDian.Action.MODIFY_ANDROID_ID);
        actions.add(LeiDian.Action.MODIFY_MAC);
        actions.add(LeiDian.Action.MODIFY_PHONE_NUMBER);
        actions.add(LeiDian.Action.RUN_APP);
        actions.add(LeiDian.Action.ADB_INSTALL_APP);
        UIUtils.setData(actionAdapter, controller.getChoice_action(), actions, 0);
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

    /**
     * 全自动运行一条龙
     *
     * @param deteleAll 删除全部模拟器
     */
    public void autoCreateLaunchInstallRunApk(boolean deteleAll) {
        Observable.create(new ObservableOnSubscribe<LeiDian.Action>() {
            @Override
            public void subscribe(ObservableEmitter<LeiDian.Action> emitter) throws Exception {
                for (int i = 0; i < ldActionAdapter.list.size(); i++) {
                    LogUtils.e("准备执行" + ldActionAdapter.list.get(i).toString());
                    if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.ADD.toString())) {
                        LeiDian.getInstance().add(controller.getEdit_ld_name().getText());
                        getSimulatorList();
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.REMOVE.toString())) {
                        ArrayList<LeiDianSimulatorBean> simulator = LeiDian.getInstance().getSimulatorList();
                        for (int j = 0; j < simulator.size(); j++) {
                            if (deteleAll || simulator.get(j).getName().equals(controller.getEdit_ld_name().getText())) {
                                LeiDian.getInstance().removeByIndex(simulator.get(j).getPosition());
                            }
                        }
                        getSimulatorList();
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.LAUNCH.toString())) {
                        LeiDian.getInstance().launch(1);
                        int delay = 50000;
                        try {
                            delay = Integer.parseInt(controller.getEdit_ld_delay().getText());
                        } catch (Exception e) {
                            delay = 5000;
                        } finally {
                            Thread.sleep(delay);
                        }
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.REBOOT.toString())) {
                        LeiDian.getInstance().reboot(1);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.QUIT.toString())) {
                        LeiDian.getInstance().quit(-1);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_DISPLAY.toString())) {
                        LeiDian.getInstance().modifyDisplay(1, 720, 1080, 240);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_CPU.toString())) {
                        LeiDian.getInstance().modifyCpu(1, 4);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_MEMORY.toString())) {
                        LeiDian.getInstance().modifyMemory(1, 3000);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_MANUFACTURER.toString()) || ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_MODEL.toString())) {
                        MobileBrand mobileBrand = MobileBrandUtils.getRandomMobileBrand();
                        LeiDian.getInstance().modifyManufacturer(1, mobileBrand.getBrand());
                        LeiDian.getInstance().modifyModel(1, mobileBrand.getTitle());
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_IMEI.toString())) {
                        LeiDian.getInstance().modifyImei(1, true, 0);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_IMSI.toString())) {
                        LeiDian.getInstance().modifyImsi(1, true, 0);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_SIM_SERIAL.toString())) {
                        LeiDian.getInstance().modifySimSerial(1, true, 0);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_ANDROID_ID.toString())) {
                        LeiDian.getInstance().modifyAndroidId(1, true, 0);
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_MAC.toString())) {
                        LeiDian.getInstance().modifyMac(1, true, "");
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.MODIFY_PHONE_NUMBER.toString())) {
                        LeiDian.getInstance().modifyPhoneNumber(1, RandomPhoneNumber.createMobile(1));
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.RUN_APP.toString())) {
                        LeiDian.getInstance().runApp(1, StringUtils.isEmpty(controller.getEdit_ld_package().getText()) ? "com.sss.michael" : controller.getEdit_ld_package().getText());
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.INSTALL_APP.toString())) {
                        LeiDian.getInstance().installApp(1, StringUtils.isEmpty(controller.getEdit_ld_path().getText()) ? "C:/Users/Administrator/Desktop/ZSHY_AD.apk" : controller.getEdit_ld_path().getText());
                        int delay = 50000;
                        try {
                            delay = Integer.parseInt(controller.getEdit_ld_delay().getText());
                        } catch (Exception e) {
                            delay = 5000;
                        } finally {
                            Thread.sleep(delay);
                        }
                    } else if (ldActionAdapter.list.get(i).toString().equals(LeiDian.Action.ADB_INSTALL_APP.toString())) {
                        LeiDian.getInstance().adbInstallApp(1, StringUtils.isEmpty(controller.getEdit_ld_path().getText()) ? "C:/Users/Administrator/Desktop/ZSHY_AD.apk" : controller.getEdit_ld_path().getText());
                        int delay = 50000;
                        try {
                            delay = Integer.parseInt(controller.getEdit_ld_delay_adb().getText());
                        } catch (Exception e) {
                            delay = 5000;
                        } finally {
                            Thread.sleep(delay);
                        }
                    }
                    emitter.onNext(ldActionAdapter.list.get(i));
                }

                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .subscribe(new DisposableObserver<LeiDian.Action>() {
                    @Override
                    public void onNext(LeiDian.Action s) {
                        LogUtils.e(s.toString() + "执行完毕");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
