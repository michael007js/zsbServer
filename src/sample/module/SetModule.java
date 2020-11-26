package sample.module;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import sample.MainController;
import sample.utils.LogUtils;
import sample.utils.MouseKeyboardListenerHelper;
import sample.utils.UIUtils;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

public class SetModule extends BaseTabModule {
    private List<Point> list = new ArrayList<>();
    private boolean isRecord;
    private MainController controller;
    private boolean isRun;
    static Robot robot;

    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    private void run() {

        int x = 0, y = 0, time = 0;
        try {
            x = Integer.parseInt(controller.getEdit_x().getText());
            x = Integer.parseInt(controller.getEdit_y().getText());
            time = Integer.parseInt(controller.getEdit_interval().getText());
        } finally {
            while (isRun) {
                for (int i = 0; i < list.size(); i++) {
                    robot.mouseMove(list.get(i).x, list.get(i).y);
                    robot.delay(200);
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.delay(200);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    robot.delay(1000);
                }
                robot.delay(time < 1 ? 3000 : time * 1000);
            }
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
                    controller.getBtn_start().setDisable(list.size() == 0);
                    MouseKeyboardListenerHelper.unRegister();
                }
                if ("R".equals(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()))) {
                    list.add(MouseInfo.getPointerInfo().getLocation());
                    UIUtils.setTextAreaLog(controller.getEdit_point_info(), "X:" + list.get(list.size() - 1).x + ",Y:" + list.get(list.size() - 1).y);
                }
            } else {
                LogUtils.e("SSSSSSSSSSSSSSSSSSSSSSSSS",NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
                if ("F1".equals(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()))) {
                    isRun = false;
                    MouseKeyboardListenerHelper.unRegister();
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
        controller.getBtn_register_start_hot_key().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isRecord = true;
                MouseKeyboardListenerHelper.setNativeKeyListener(nativeKeyListener);
                MouseKeyboardListenerHelper.register(false);
                controller.getBtn_start().setDisable(true);
                controller.getEdit_point_info().setText("");
                list.clear();
            }
        });

        controller.getBtn_start().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MouseKeyboardListenerHelper.setNativeKeyListener(nativeKeyListener);
                MouseKeyboardListenerHelper.register(false);
                isRun = true;
                run();
            }
        });
    }
}
