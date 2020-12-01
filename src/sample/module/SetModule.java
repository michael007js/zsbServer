package sample.module;

import io.reactivex.Observable;
import javafx.scene.control.Button;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import sample.MainController;
import sample.adapter.BaseListViewAdapter;
import sample.bean.RobotBean;
import sample.utils.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class SetModule extends BaseTabModule {
    private BaseListViewAdapter robotAdapter;
    private ArrayList<RobotBean> list = new ArrayList<>();
    private boolean isRecord;
    private MainController controller;
    private boolean isRun;
    private MouseKeyboardListenerHelper mouseKeyboardListenerHelper;
    static Robot robot;
    private DisposableObserver<Long> disposableObserver;

    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private long lastActionIntervalCountTime = 0;

    private int totalIntervalTime = 3;

    private void run() {
        try {
            totalIntervalTime = Integer.parseInt(controller.getEdit_interval().getText());
        } catch (Exception e) {
            e.printStackTrace();
            totalIntervalTime = 3;
        } finally {
            if (disposableObserver != null) {
                if (!disposableObserver.isDisposed()) {
                    disposableObserver.dispose();
                }
                disposableObserver = null;
            }
            disposableObserver = new DisposableObserver<Long>() {

                @Override
                protected void onStart() {
                    super.onStart();
                    onNext((long) totalIntervalTime);
                }

                @Override
                public void onNext(Long along) {
                    change();
                    if (along % totalIntervalTime == 0) {
                        for (int i = 0; i < list.size(); i++) {
                            robot.mouseMove(list.get(i).x, list.get(i).y);
                            robot.delay(20);
                            robot.mousePress(list.get(i).action == 1 ? InputEvent.BUTTON1_MASK : InputEvent.BUTTON2_MASK);
                            robot.delay(20);
                            robot.mouseRelease(list.get(i).action == 1 ? InputEvent.BUTTON1_MASK : InputEvent.BUTTON2_MASK);
                            robot.delay(list.get(i).interval);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };

            MichaelUtils.goObserver(Observable.interval(1, TimeUnit.SECONDS)
                    .flatMap(new Function<Long, ObservableSource<Long>>() {
                        @Override
                        public ObservableSource<Long> apply(Long aLong) throws Exception {
                            return Observable.just(aLong);
                        }
                    }), Schedulers.io(), disposableObserver);
        }
    }

    private NativeKeyListener nativeKeyListener = new NativeKeyListener() {
        @Override
        public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

        }

        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
            if (isRecord) {
                if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
                    isRecord = false;
                    change();
                    mouseKeyboardListenerHelper.unRegister();
                }
                if ("R".equals(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()))) {
                    Point point = MouseInfo.getPointerInfo().getLocation();
                    RobotBean robotBean = new RobotBean("步骤" + (list.size() + 1), point.x, point.y, 0, 1);
                    list.add(robotBean);
                    showList();
                }
            } else {
                if ("F1".equals(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()))) {
                    if (disposableObserver != null) {
                        if (!disposableObserver.isDisposed()) {
                            disposableObserver.dispose();
                        }
                        disposableObserver = null;
                    }
                    isRun = false;
                    change();
                    mouseKeyboardListenerHelper.unRegister();
                }
            }

        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        }
    };

    @Override
    public void initialize(MainController mainController) {
        this.controller = mainController;
        change();
        controller.getBtn_register_start_hot_key().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isRecord = true;
                if (mouseKeyboardListenerHelper != null) {
                    mouseKeyboardListenerHelper.unRegister();
                }
                mouseKeyboardListenerHelper = new MouseKeyboardListenerHelper();
                mouseKeyboardListenerHelper.setNativeKeyListener(nativeKeyListener);
                mouseKeyboardListenerHelper.register(false);
                change();
            }
        });

        controller.getBtn_start().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mouseKeyboardListenerHelper != null) {
                    mouseKeyboardListenerHelper.unRegister();
                }
                mouseKeyboardListenerHelper = new MouseKeyboardListenerHelper();
                mouseKeyboardListenerHelper.setNativeKeyListener(nativeKeyListener);
                mouseKeyboardListenerHelper.register(false);
                isRun = true;
                run();
            }
        });

        controller.getBtn_load().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String path = AlertUtils.showFile("C:/Users/Administrator/Desktop", "机器人储存文件");
                if (StringUtils.isEmpty(path) || new File(path).isDirectory()) {
                    AlertUtils.showError("读取", "非法文件或文件目录为空");
                    return;
                }
                try {
                    list = JsonUtils.formatToList(IOHelper.readString(path), RobotBean.class);
                    AlertUtils.showInfo("读取", "读取成功！");
                    showList();
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.showError("读取", "读取失败！");
                }

            }
        });
        controller.getBtn_save().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String path = AlertUtils.showFile("C:/Users/Administrator/Desktop", "机器人储存文件", "*.rcfg");
                if (!new File(path).isFile()) {
                    AlertUtils.showError("保存", "非法路径！");
                    return;
                }
                try {
                    if (IOHelper.writeString(new File(path).isFile() ? path : path + "/robot.rcfg", false, JsonUtils.formatToJsonString(list))) {
                        AlertUtils.showInfo("保存", "保存成功！");
                    } else {
                        AlertUtils.showError("保存", "保存失败！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.showError("保存", "保存失败！");
                }
            }
        });

        controller.getBtn_clear().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (AlertUtils.showConfirm("清空", "警告", "确定要清空当前所有的动作吗？如果未保存将全部丢失！")) {
                    list.clear();
                    showList();
                }
            }
        });
    }


    private void change() {
        controller.getBtn_start().setDisable(isRecord);
        controller.getBtn_register_start_hot_key().setDisable(isRun);
        controller.getBtn_load().setDisable(isRun);
        controller.getBtn_save().setDisable(isRun);
        controller.getBtn_clear().setDisable(isRun);
    }

    private void showList() {
        if (robotAdapter == null) {
            robotAdapter = new BaseListViewAdapter<RobotBean>(new BaseListViewAdapter.OnBaseListViewAdapterCallBacK<RobotBean>() {
                @Override
                public Node bindView(RobotBean item) {
                    int position = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isSame(item)) {
                            position = i;
                            break;
                        }
                    }
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.BASELINE_LEFT);
                    hbox.getChildren().add(createTagTextField(item, position));
                    hbox.getChildren().add(createLabel("x:" + item.x + ",y:" + item.y));
                    hbox.getChildren().add(createActionButton(item, position));
                    hbox.getChildren().add(createIntervalTextField(item, position));
                    hbox.getChildren().add(createLabel("毫秒后开始下一个动作"));
                    return hbox;
                }

                @Override
                public void onItemClick(RobotBean item) {

                }
            });
        }
        UIUtils.setData(robotAdapter, controller.getLv_action(), list);
    }


    private Label createLabel(String name) {
        Label label = new Label();
        label.setPadding(new Insets(0, 5, 0, 5));
        label.setText(name);
        return label;
    }

    private Button createActionButton(RobotBean item, int position) {
        Button button = new Button();
        button.setText(item.action == 1 ? "左键" : "右键");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.get(position).action = list.get(position).action == 1 ? 2 : 1;
                controller.getLv_action().setItems(null);
                ObservableList observableList = FXCollections.observableArrayList(list);
                controller.getLv_action().setItems(observableList);
            }
        });
        return button;
    }

    private TextField createTagTextField(RobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(80);
        textField.setText(item.tag);
        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    list.get(position).tag = textField.getText().trim();
                    controller.getLv_action().setItems(null);
                    ObservableList observableList = FXCollections.observableArrayList(list);
                    controller.getLv_action().setItems(observableList);
                }
            }
        });
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    list.get(position).tag = textField.getText().trim();
                    controller.getLv_action().setItems(null);
                    ObservableList observableList = FXCollections.observableArrayList(list);
                    controller.getLv_action().setItems(observableList);
                }
            }
        });
        return textField;
    }

    private TextField createIntervalTextField(RobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(50);
        textField.setText(item.interval + "");

        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    try {
                        list.get(position).interval = Integer.parseInt(textField.getText().trim());
                        controller.getLv_action().setItems(null);
                        ObservableList observableList = FXCollections.observableArrayList(list);
                        controller.getLv_action().setItems(observableList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    try {
                        list.get(position).interval = Integer.parseInt(textField.getText().trim());
                        controller.getLv_action().setItems(null);
                        ObservableList observableList = FXCollections.observableArrayList(list);
                        controller.getLv_action().setItems(observableList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return textField;
    }
}
