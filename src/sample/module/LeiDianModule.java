package sample.module;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
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
import sample.module.adapter.LdSimulatorAdapter;
import utils.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                LogUtils.e("退出所有模拟器");
                emitter.onNext(LeiDian.getInstance().quit(-1));
            }
        }).map(new Function<String, ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> apply(String s) throws Exception {
                ArrayList<Integer> list = new ArrayList<>();
                ArrayList<LeiDianSimulatorBean> simulator = LeiDian.getInstance().getSimulatorList();
                for (int i = 0; i < simulator.size(); i++) {
                    if (deteleAll || simulator.get(i).getName().equals(controller.getEdit_ld_name().getText())) {
                        list.add(simulator.get(i).getPosition());
                    }
                }
                LogUtils.e("统计符合条件的待移除模拟器");
                return list;
            }
        }).map(new Function<ArrayList<Integer>, String>() {
            @Override
            public String apply(ArrayList<Integer> integers) throws Exception {
                for (int i = 0; i < integers.size(); i++) {
                    LeiDian.getInstance().removeByIndex(integers.get(i));
                }
                LogUtils.e("移除指定的模拟器，size:" + integers.size());
                LogUtils.e("添加模拟器");
                return LeiDian.getInstance().add(controller.getEdit_ld_name().getText());
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
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
                LogUtils.e("修改模拟器配置、安装APK");
                return LeiDian.getInstance().installApp(1, "C:/Users/Administrator/Desktop/ZSHY无限重启视频.apk");
            }
        })
                .delay(20, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        LogUtils.e("启动APK");
                        LeiDian.getInstance().runApp(1, "com.sss.michael");
                        onComplete();
                        if (!isDisposed()) {
                            dispose();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        getSimulatorList();
                        LogUtils.e("完毕");
                    }
                });

    }
}
