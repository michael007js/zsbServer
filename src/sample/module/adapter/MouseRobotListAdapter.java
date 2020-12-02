package sample.module.adapter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import sample.adapter.BaseListViewAdapter;
import sample.bean.MouseRobotBean;
import sample.utils.AlertUtils;
import sample.utils.LogUtils;
import sample.utils.UIUtils;

import java.util.ArrayList;

public class MouseRobotListAdapter extends BaseListViewAdapter<MouseRobotBean> {
    public ArrayList<MouseRobotBean> list = new ArrayList<>();
    private OnMouseRobotListAdapterCallBack onMouseRobotListAdapterCallBack;
    private boolean isParent;

    public MouseRobotListAdapter(boolean isParent) {
        this.isParent = isParent;
    }

    public void setOnMouseRobotListAdapterCallBack(OnMouseRobotListAdapterCallBack onMouseRobotListAdapterCallBack) {
        this.onMouseRobotListAdapterCallBack = onMouseRobotListAdapterCallBack;
    }

    private int getPosition(MouseRobotBean item, ArrayList<MouseRobotBean> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSame(item)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected Node bindView(MouseRobotBean item) {

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BASELINE_LEFT);
        hbox.getChildren().add(createTagTextField(item, getPosition(item, list)));
        hbox.getChildren().add(createLabel("x:"));
        hbox.getChildren().add(createXTextField(item, getPosition(item, list)));
        hbox.getChildren().add(createLabel("y:"));
        hbox.getChildren().add(createYTextField(item, getPosition(item, list)));
        hbox.getChildren().add(createActionButton(item, getPosition(item, list)));
        hbox.getChildren().add(createIntervalTextField(item, getPosition(item, list)));
        hbox.getChildren().add(createLabel(getPosition(item, list) < list.size() - 1 ? "毫秒后开始下一个动作" : "毫秒后开始一轮循环"));
        if (isParent) {
            hbox.getChildren().add(createChildrenHBox(item, getPosition(item, list)));
        }
        hbox.getChildren().add(createDeleteButton(item, getPosition(item, list)));
        return hbox;
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
                    refreshData(list);
                }
            }
        });
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    list.get(position).tag = textField.getText().trim();
                    refreshData(list);
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
        label.setPadding(new Insets(0, 2, 0, 2));
        label.setText(name);
        return label;
    }

    /**
     * 创建动坐标点X文本框
     */
    private TextField createXTextField(MouseRobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(50);
        textField.setText(item.x + "");

        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    try {
                        list.get(position).x = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.showList();
                        }
                    }
                }
            }
        });
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    try {
                        list.get(position).x = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.showList();
                        }
                    }
                }
            }
        });
        return textField;
    }


    /**
     * 创建动坐标点Y文本框
     */
    private TextField createYTextField(MouseRobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(50);
        textField.setText(item.y + "");

        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    try {
                        list.get(position).y = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.showList();
                        }
                    }
                }
            }
        });
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    try {
                        list.get(position).y = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.showList();
                        }
                    }
                }
            }
        });
        return textField;
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
                refreshData(list);
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
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.showList();
                        }
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
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.showList();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return textField;
    }

    /**
     * 创建子节点动作按钮与列表
     */
    private HBox createChildrenHBox(MouseRobotBean item, int position) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BASELINE_LEFT);
        Button button = new Button();
        button.setText(item.close ? "+" : "-");
        button.setStyle(item.child.size() == 0 ? null : "-fx-background-color: gray");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).close = true;
                }
                list.get(position).close = false;
                refreshData(list);
            }
        });
        hbox.getChildren().add(button);
        if (!item.close && item.child.size() > 0) {
            hbox.getChildren().add(createChildren(item.child));
        }
        return hbox;
    }


    private ListView createChildren(ArrayList<MouseRobotBean> child) {
        MouseRobotListAdapter childAdapter = new MouseRobotListAdapter(false);
        ListView listView = new ListView();
        listView.prefHeightProperty().setValue(30 * child.size());
        UIUtils.setData(childAdapter, listView, child);
        childAdapter.setOnMouseRobotListAdapterCallBack(new MouseRobotListAdapter.OnMouseRobotListAdapterCallBack() {
            @Override
            public void showList() {
                UIUtils.setData(childAdapter, listView, childAdapter.list);
            }
        });
        return listView;
    }

    /**
     * 创建动作意图删除按钮
     */
    private Button createDeleteButton(MouseRobotBean item, int position) {
        Button button = new Button();
        button.setText("删除");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (item.level == 0) {
                    if (AlertUtils.showConfirm("警告", "删除", "是否要删除该动作？如果有子节点动作，将一并删除")) {
                        list.remove(position);
                        refreshData(list);
                    }
                } else {
                    if (AlertUtils.showConfirm("警告", "删除", "是否要删除该动作？")) {
                        item.child.remove(getPosition(item, item.child));
                        refreshData(list);
                    }

                }
            }
        });
        return button;
    }


    /**
     * 获取当前被打开的子节点
     */
    public int getCurrentSelectChildren() {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).close) {
                return i;
            }
        }
        return 0;
    }

    public interface OnMouseRobotListAdapterCallBack {

        void showList();
    }

}
