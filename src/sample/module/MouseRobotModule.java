package sample.module;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;
import sample.MainController;
import sample.bean.MouseRobotBean;
import sample.bean.SaveConfigBean;
import sample.module.adapter.MouseRobotListAdapter;
import sample.utils.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class MouseRobotModule extends BaseTabModule implements EventHandler<ActionEvent>, NativeKeyListener, NativeMouseMotionListener {
    private MouseRobotListAdapter robotAdapter;
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

    @Override
    public void initialize(MainController mainController) {
        this.controller = mainController;
        change();
        controller.getEdit_wait_time().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                showList(true);
            }
        });
        controller.getBtn_register_start_hot_key().setOnAction(this::handle);
        controller.getBtn_start().setOnAction(this::handle);
        controller.getBtn_load().setOnAction(this::handle);
        controller.getBtn_save().setOnAction(this::handle);
        controller.getBtn_clear().setOnAction(this::handle);
        robotAdapter = new MouseRobotListAdapter();
        robotAdapter.setOnMouseRobotListAdapterCallBack(new MouseRobotListAdapter.OnMouseRobotListAdapterCallBack() {
            @Override
            public void onDelete(MouseRobotBean item, int position, MouseRobotListAdapter adapter, ListView<MouseRobotBean> listView) {
                if (item.child.size() > 0 ? AlertUtils.showConfirm("警告", "该动作包含子节点动作", "是否要删除该动作？子节点将一并删除") : AlertUtils.showConfirm("警告", "删除", "是否要删除该动作？")) {
                    adapter.list.remove(position);
                    adapter.refreshData(adapter.list);
                    if (item.level == 1) {
                        listView.prefHeightProperty().setValue(29 * adapter.list.size() + 10);
                    }
                    MouseRobotModule.this.showList(true);
                }
            }

            @Override
            public void onTagChanged(String text, MouseRobotBean item, int position, MouseRobotListAdapter adapter) {
                adapter.list.get(position).tag = text;
                adapter.refreshData(adapter.list);
            }

            @Override
            public void onChildTaskCount(MouseRobotBean item, int position, MouseRobotListAdapter adapter) {
                showList(true);
                adapter.refreshData(adapter.list);
            }

            @Override
            public void onActionChanged(MouseRobotBean item, int position, MouseRobotListAdapter adapter) {
                adapter.refreshData(adapter.list);
            }

            @Override
            public void onPointChanged(MouseRobotBean item, int position, MouseRobotListAdapter adapter) {
                MouseRobotModule.this.showList(true);
                adapter.refreshData(adapter.list);
            }

            @Override
            public void onIntervalChanged(MouseRobotBean item, int position, MouseRobotListAdapter adapter, ArrayList<MouseRobotBean> list) {
                adapter.refreshData(adapter.list);
                MouseRobotModule.this.showList(true);
            }

            @Override
            public ListView onCreateChildList(MouseRobotBean parent, ArrayList<MouseRobotBean> child, MouseRobotListAdapter parentAdapter) {
                MouseRobotListAdapter childAdapter = new MouseRobotListAdapter();
                ListView listView = new ListView();
                listView.prefHeightProperty().setValue(29 * child.size() + 10);
                listView.prefWidthProperty().setValue(500);
                childAdapter.list = child;
                UIUtils.setData(childAdapter, listView, child);
                childAdapter.setOnMouseRobotListAdapterCallBack(this);
                return listView;
            }


        });
    }

    /**
     * 开始运行机器人
     */
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
        int totalIntervalTime = getCalculationTime();
        disposableObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long along) {
                change();
                if (along % totalIntervalTime == 0) {
                    for (int i = 0; i < robotAdapter.list.size(); i++) {
                        if (!isRun) {
                            break;
                        }
                        if (robotAdapter.list.get(i).child.size() > 0) {//有子任务，该主任务不执行
                            for (int count = 0; count < robotAdapter.list.get(i).count; count++) {
                                if (!isRun) {
                                    break;
                                }
                                for (int j = 0; j < robotAdapter.list.get(i).child.size(); j++) {
                                    LogUtils.e(i, j);
                                    if (!isRun) {
                                        break;
                                    }
                                    taskAction(i, j, robotAdapter.list.get(i).child.get(j));
                                }
                            }
                        } else {
                            taskAction(i, 0, robotAdapter.list.get(i));
                        }

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


    /**
     * 执行动作
     */
    private void taskAction(int i, int j, MouseRobotBean item) {
        if (j == 0) {
            UIUtils.setText(controller.getLable_current_action(), "正在执行主任务" + (i + 1) + ":" + item.tag + "[x:" + item.x + "y:" + item.y + "],等待" + item.interval + (i < robotAdapter.list.size() - 1 ? "毫秒后开始下一个动作" : "开始下一轮循环"));
        } else {
            UIUtils.setText(controller.getLable_current_action(), "正在执行主任务" + (i + 1) + "的分支任务(" + (j + 1) + ")" + item.tag + "[x:" + item.x + "y:" + item.y + "],等待" + item.interval + "毫秒后继续下一个动作");
        }
        robot.mouseMove(item.x, item.y);
        robot.delay(5);
        robot.mousePress(item.action == 1 ? InputEvent.BUTTON1_MASK : InputEvent.BUTTON3_MASK);
        robot.delay(5);
        robot.mouseRelease(item.action == 1 ? InputEvent.BUTTON1_MASK : InputEvent.BUTTON3_MASK);
        robot.delay(item.interval);

    }


    /**
     * 计算列表中所有动作的执行时间
     */
    private int getCalculationTime() {
        int time = 3;
        if (robotAdapter.list.size() > 0 && controller.getCb_auto_calculation_time().isSelected()) {
            time = 0;
            for (int i = 0; i < robotAdapter.list.size(); i++) {
                for (int count = 0; count < robotAdapter.list.get(i).count; count++) {
                    for (int j = 0; j < robotAdapter.list.get(i).child.size(); j++) {
                        time += robotAdapter.list.get(i).child.get(j).interval;
                    }
                }
                //当主任务有子任务时该主任务步骤不执行，故不加执行时间（0）
                time += robotAdapter.list.get(i).child.size() > 0 ? 0 : robotAdapter.list.get(i).interval;
            }
            //判断列表总时长取余是否不等于0，如果是，则代表不能整除，在原有的时间上加十秒防止误差
            time = time % 1000 == 0 ? time / 1000 : time / 1000 + 1;
        }
        try {
            return time += Integer.parseInt(controller.getEdit_wait_time().getText());
        } catch (Exception e) {
        } finally {
            return time;
        }
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
    private void showList(boolean justCalc) {
        if (!justCalc) {
            UIUtils.setData(robotAdapter, controller.getLv_action(), robotAdapter.list);
        }
        UIUtils.setText(controller.getEdit_interval(), getCalculationTime() + "");
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
            for (int i = 0; i < robotAdapter.list.size(); i++) {
                time += robotAdapter.list.get(i).interval;
            }

            int totalIntervalTime = getTotalIntervalTime();
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
                robotAdapter.list = saveConfigBean.getMouse();
                for (int i = 0; i < robotAdapter.list.size(); i++) {
                    robotAdapter.list.get(i).close = true;
                    if (robotAdapter.list.get(i).child == null) {
                        robotAdapter.list.get(i).child = new ArrayList<>();
                    }
                }
                int totalIntervalTime = saveConfigBean.getTotalInterval();
                controller.getEdit_interval().setText(totalIntervalTime + "");
                AlertUtils.showInfo("读取", "读取成功！");
                showList(false);
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
                int totalIntervalTime = getTotalIntervalTime();
                saveConfigBean.setMouse(robotAdapter.list);
                saveConfigBean.setTotalInterval(totalIntervalTime);
                AlertUtils.showInfo("保存", IOHelper.writeString(new File(path).isFile() ? path : path + "/robot.rcfg", false, JsonUtils.formatToJsonString(saveConfigBean)) ? "保存成功！" : "保存失败！");
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("保存", "保存失败！");
            }
        } else if (event.getSource() == controller.getBtn_clear()) {
            if (AlertUtils.showConfirm("清空", "警告", "确定要清空当前所有的动作吗？如果未保存将全部丢失！")) {
                robotAdapter.list.clear();
                showList(false);
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
                int currentSelectChildren = robotAdapter.getCurrentSelectChildren();
                if (currentSelectChildren == -1) {
                    robotAdapter.list.add(new MouseRobotBean("步骤" + (robotAdapter.list.size() + 1), point.x, point.y, 1000, 1, 1, true, 0, new ArrayList()));
                } else {
                    robotAdapter.list.get(currentSelectChildren).child.add(new MouseRobotBean("步骤" + (robotAdapter.list.get(currentSelectChildren).child.size() + 1), point.x, point.y, 1000, 1, 1, true, 1, new ArrayList()));
                }

                showList(false);
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
                UIUtils.setText(controller.getLable_current_action(), "当前动作");
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
