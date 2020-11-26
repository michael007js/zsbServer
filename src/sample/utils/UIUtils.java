package sample.utils;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import sample.adapter.BaseChoiceBoxAdapter;
import sample.adapter.BaseListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class UIUtils {

    public static void setColorPickerValue(ColorPicker colorPicker, String value) {
        if (!PlatformImpl.isFxApplicationThread()) {
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    colorPicker.setValue(Color.valueOf(value));
                }
            });
        } else {
            colorPicker.setValue(Color.valueOf(value));
        }
    }

    public static void setText(Button button, String text) {
        if (!PlatformImpl.isFxApplicationThread()) {
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    button.setText(text);
                }
            });
        } else {
            button.setText(text);
        }
    }


    public static void setTextAreaLog(TextInputControl textArea, String content) {
        if (textArea == null || content == null) {
            return;
        }
        content = TimeUtils.getNowString() + ":" + content;

        if (!PlatformImpl.isFxApplicationThread()) {
            String finalContent = content;
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    textArea.setText((StringUtils.isEmpty(textArea.getText()) ? "" : textArea.getText() + "\n") + finalContent);
                    textArea.selectPositionCaret(textArea.getLength());
                }
            });
        } else {
            textArea.setText((StringUtils.isEmpty(textArea.getText()) ? "" : textArea.getText() + "\n") + content);
            textArea.selectPositionCaret(textArea.getLength());
        }
    }

    public static void setText(TextField textField, String text) {
        if (!PlatformImpl.isFxApplicationThread()) {
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    textField.setText(text);
                }
            });
        } else {
            textField.setText(text);
        }
    }


    public static void setText(TextArea textArea, String text) {
        if (!PlatformImpl.isFxApplicationThread()) {
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    textArea.setText(text);
                }
            });
        } else {
            textArea.setText(text);
        }
    }


    public static void setText(Label label, String text) {
        if (!PlatformImpl.isFxApplicationThread()) {
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    label.setText(text);
                }
            });
        } else {
            label.setText(text);
        }
    }

    public static <T> void setData(BaseListViewAdapter adapter, ListView<?> listView, ArrayList<T> list) {
        if (!PlatformImpl.isFxApplicationThread()) {
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    adapter.setData(listView, list);

                }
            });
        } else {
            adapter.setData(listView, list);
        }
    }

    public static <T> void setData(BaseChoiceBoxAdapter adapter, ChoiceBox<?> choiceBox, ArrayList<T> list) {
        if (!PlatformImpl.isFxApplicationThread()) {
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    adapter.setData(choiceBox, list);

                }
            });
        } else {
            adapter.setData(choiceBox, list);
        }
    }
    public static <T> void setData(BaseChoiceBoxAdapter adapter, ChoiceBox<?> choiceBox, List<T> list, int selectionPosition) {
        setData(adapter,choiceBox,(ArrayList<T>)list,selectionPosition);
    }
    public static <T> void setData(BaseChoiceBoxAdapter adapter, ChoiceBox<?> choiceBox, ArrayList<T> list, int selectionPosition) {
        if (!PlatformImpl.isFxApplicationThread()) {
            PlatformImpl.runLater(new Runnable() {
                @Override
                public void run() {
                    adapter.setData(choiceBox, list, selectionPosition);

                }
            });
        } else {
            adapter.setData(choiceBox, list, selectionPosition);
        }
    }
}
