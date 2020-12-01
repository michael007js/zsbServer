package sample.module;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;
import sample.MainController;
import sample.adapter.BaseListViewAdapter;
import sample.bean.MouseRobotBean;
import sample.bean.SaveConfigBean;
import sample.utils.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class MouseRobotModule extends BaseTabModule implements EventHandler<ActionEvent>, NativeKeyListener, NativeMouseMotionListener {
    private BaseListViewAdapter robotAdapter;
    private ArrayList<MouseRobotBean> list = new ArrayList<>();
    private boolean isRecord;
    private MainController controller;
    private boolean isRun;
    private MouseKeyboardListenerHelper mouseKeyboardListenerHelper;
    static Robot robot;
    private DisposableObserver<Long> disposableObserver;
    private int totalIntervalTime = 3;

    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        if (!isRun) {
            isRun = true;
        }
        change();
        if (disposableObserver != null) {
            if (!disposableObserver.isDisposed()) {
                disposableObserver.dispose();
            }
            disposableObserver = null;
        }
        disposableObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long along) {
                change();
                if (along % totalIntervalTime == 0) {
                    for (int i = 0; i < list.size(); i++) {
                        if (!isRun) {
                            break;
                        }
                        robot.mouseMove(list.get(i).x, list.get(i).y);
                        if (!isRun) {
                            break;
                        }
                        robot.delay(20);
                        if (!isRun) {
                            break;
                        }
                        robot.mousePress(list.get(i).action == 1 ? InputEvent.BUTTON1_MASK : InputEvent.BUTTON3_MASK);
                        if (!isRun) {
                            break;
                        }
                        robot.delay(20);
                        if (!isRun) {
                            break;
                        }
                        robot.mouseRelease(list.get(i).action == 1 ? InputEvent.BUTTON1_MASK : InputEvent.BUTTON3_MASK);
                        if (!isRun) {
                            break;
                        }
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

    @Override
    public void initialize(MainController mainController) {
        this.controller = mainController;
        change();
        controller.getBtn_register_start_hot_key().setOnAction(this::handle);
        controller.getBtn_start().setOnAction(this::handle);
        controller.getBtn_load().setOnAction(this::handle);
        controller.getBtn_save().setOnAction(this::handle);
        controller.getBtn_clear().setOnAction(this::handle);
    }

    /**
     * 计算列表中所有动作的执行时间
     */
    private int getCalculationTime() {
        int totalIntervalTime = getTotalIntervalTime();
        if (list.size() > 0 && controller.getCb_auto_calculation_time().isSelected()) {
            totalIntervalTime = 0;
            for (int i = 0; i < list.size(); i++) {
                totalIntervalTime += list.get(i).interval;
            }
            //判断列表总时长取余是否不等于0，如果是，则代表不能整除，在原有的时间上加十秒防止误差
            totalIntervalTime = totalIntervalTime % 1000 == 0 ? totalIntervalTime / 1000 : totalIntervalTime / 1000 + 10;
        }
        return totalIntervalTime;
    }

    /**
     * 获取文本框中用户自定义的循环间隔时长
     */
    private int getTotalIntervalTime() {
        int totalIntervalTime = 3;
        try {
            totalIntervalTime = Integer.parseInt(controller.getEdit_interval().getText());
        } catch (Exception e) {
            e.printStackTrace();
            totalIntervalTime = 3;
        } finally {
            return totalIntervalTime;
        }
    }

    /**
     * 切换按钮状态
     */
    private void change() {
        controller.getBtn_start().setDisable(isRecord);
        controller.getBtn_register_start_hot_key().setDisable(isRun);
        controller.getBtn_load().setDisable(isRun || isRecord);
        controller.getBtn_save().setDisable(isRun || isRecord);
        controller.getBtn_clear().setDisable(isRun || isRecord);
    }

    /**
     * 显示列表
     */
    private void showList() {
        if (robotAdapter == null) {
            robotAdapter = new BaseListViewAdapter<MouseRobotBean>(new BaseListViewAdapter.OnBaseListViewAdapterCallBacK<MouseRobotBean>() {
                @Override
                public Node bindView(MouseRobotBean item) {
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
                    hbox.getChildren().add(createLabel(position < list.size() - 1 ? "毫秒后开始下一个动作" : "毫秒后开始一轮循环"));
                    return hbox;
                }

                @Override
                public void onItemClick(MouseRobotBean item) {

                }
            });
        }
        UIUtils.setText(controller.getEdit_interval(), getCalculationTime() + "");
        UIUtils.setData(robotAdapter, controller.getLv_action(), list);
    }

    /**
     * 创建动作意图tag文本框
     */
    private TextField createTagTextField(MouseRobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(200);
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

    /**
     * 创建标签
     */
    private Label createLabel(String name) {
        Label label = new Label();
        label.setPadding(new Insets(0, 5, 0, 5));
        label.setText(name);
        return label;
    }

    /**
     * 创建动作意图切换按钮
     */
    private Button createActionButton(MouseRobotBean item, int position) {
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

    /**
     * 创建动作意图执行完等待时间文本框
     */
    private TextField createIntervalTextField(MouseRobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(50);
        textField.setText(item.interval + "");

        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    try {
                        list.get(position).interval = Integer.parseInt(textField.getText().trim());
                        showList();
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
                        showList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return textField;
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == controller.getBtn_register_start_hot_key()) {
            isRecord = true;
            if (mouseKeyboardListenerHelper != null) {
                mouseKeyboardListenerHelper.unRegister();
            }
            mouseKeyboardListenerHelper = null;
            mouseKeyboardListenerHelper = new MouseKeyboardListenerHelper();
            mouseKeyboardListenerHelper.setNativeKeyListener(this);
            mouseKeyboardListenerHelper.setNativeMouseMotionListener(this);
            mouseKeyboardListenerHelper.register(false);
            change();
        } else if (event.getSource() == controller.getBtn_start()) {
            if (mouseKeyboardListenerHelper != null) {
                mouseKeyboardListenerHelper.unRegister();
            }
            mouseKeyboardListenerHelper = null;
            mouseKeyboardListenerHelper = new MouseKeyboardListenerHelper();
            mouseKeyboardListenerHelper.setNativeKeyListener(this);
            mouseKeyboardListenerHelper.setNativeMouseMotionListener(this);
            mouseKeyboardListenerHelper.register(false);
            long time = 0;
            for (int i = 0; i < list.size(); i++) {
                time += list.get(i).interval;
            }

            totalIntervalTime = getTotalIntervalTime();
            if (totalIntervalTime * 1000 < time) {
                AlertUtils.showError("运行", "列表动作循环间隔时间必须大于或等于列表中所有动作的停顿时间的总和！");
                return;
            }
            run();
        } else if (event.getSource() == controller.getBtn_load()) {
            String path = AlertUtils.showFile("C:/Users/Administrator/Desktop", "机器人储存文件");
            if (StringUtils.isEmpty(path) || new File(path).isDirectory()) {
                AlertUtils.showError("读取", "非法文件或文件目录为空");
                return;
            }
            try {
                SaveConfigBean saveConfigBean = JsonUtils.formatToObject(IOHelper.readString(path), SaveConfigBean.class);
                list = saveConfigBean.getMouse();
                totalIntervalTime = saveConfigBean.getTotalInterval();
                controller.getEdit_interval().setText(totalIntervalTime + "");
                AlertUtils.showInfo("读取", "读取成功！");
                showList();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("读取", "读取失败！");
            }
        } else if (event.getSource() == controller.getBtn_save()) {
            String path = AlertUtils.showFile("C:/Users/Administrator/Desktop", "机器人储存文件", "*.rcfg");
            if (!new File(path).isFile()) {
                AlertUtils.showError("保存", "非法路径！");
                return;
            }
            try {
                SaveConfigBean saveConfigBean = new SaveConfigBean();
                totalIntervalTime = getTotalIntervalTime();
                saveConfigBean.setMouse(list);
                saveConfigBean.setTotalInterval(totalIntervalTime);
                AlertUtils.showInfo("保存", IOHelper.writeString(new File(path).isFile() ? path : path + "/robot.rcfg", false, JsonUtils.formatToJsonString(saveConfigBean)) ? "保存成功！" : "保存失败！");
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("保存", "保存失败！");
            }
        } else if (event.getSource() == controller.getBtn_clear()) {
            if (AlertUtils.showConfirm("清空", "警告", "确定要清空当前所有的动作吗？如果未保存将全部丢失！")) {
                list.clear();
                showList();
            }
        }
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
                MouseRobotBean mouseRobotBean = new MouseRobotBean("步骤" + (list.size() + 1), point.x, point.y, 1000, 1);
                list.add(mouseRobotBean);
                showList();
            }
        } else {
            if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
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
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.getLable_point().setText("x:" + nativeMouseEvent.getX() + ",y:" + nativeMouseEvent.getY());
            }
        });
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }
}
