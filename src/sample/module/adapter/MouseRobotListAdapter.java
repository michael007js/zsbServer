package sample.module.adapter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import sample.adapter.BaseListViewAdapter;
import sample.bean.MouseRobotBean;

import java.util.ArrayList;

@SuppressWarnings("all")
public class MouseRobotListAdapter extends BaseListViewAdapter<MouseRobotBean> {

    private OnMouseRobotListAdapterCallBack onMouseRobotListAdapterCallBack;

    public void setOnMouseRobotListAdapterCallBack(OnMouseRobotListAdapterCallBack onMouseRobotListAdapterCallBack) {
        this.onMouseRobotListAdapterCallBack = onMouseRobotListAdapterCallBack;
    }

    @Override
    public int getPosition(MouseRobotBean item) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSame(item)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected Node bindView(MouseRobotBean item) {
        int position = getPosition(item);
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BASELINE_LEFT);

        if (item.level == 0) {
            hbox.getChildren().add(createChildrenOpenButton(item, position));
            hbox.getChildren().add(createTagTextField(item, position));
            if (!item.close && item.child.size() > 0) {
                if (onMouseRobotListAdapterCallBack != null) {
                    hbox.getChildren().add(onMouseRobotListAdapterCallBack.onCreateChildList(item,item.child, MouseRobotListAdapter.this));
                    hbox.getChildren().add(createCountTextField(item, position));
                }

            }
            hbox.getChildren().add(createXTextField(item, position));
            hbox.getChildren().add(createYTextField(item, position));
            hbox.getChildren().add(createActionButton(item, position));
            hbox.getChildren().add(createIntervalTextField(item, position));
            hbox.getChildren().add(createDeleteButton(item, position));
        } else {
            hbox.getChildren().add(createTagTextField(item, position));
            hbox.getChildren().add(createXTextField(item, position));
            hbox.getChildren().add(createYTextField(item, position));
            hbox.getChildren().add(createActionButton(item, position));
            hbox.getChildren().add(createIntervalTextField(item, position));
            hbox.getChildren().add(createDeleteButton(item, position));
        }


        return hbox;
    }


    /**
     * 创建循环次数文本框
     */
    private TextField createCountTextField(MouseRobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(60);
        textField.setText(item.count + "");
        textField.setPromptText("循环次数");
        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    try {
                        list.get(position).count = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.onChildTaskCount(item, position, MouseRobotListAdapter.this);
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
                        list.get(position).count = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.onChildTaskCount(item, position, MouseRobotListAdapter.this);
                        }
                    }
                }
            }
        });
        return textField;
    }

    /**
     * 创建动作意图tag文本框
     */
    private TextField createTagTextField(MouseRobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(150);
        textField.setText(item.tag);
        textField.setPromptText("动作名称");
        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    if (onMouseRobotListAdapterCallBack != null) {
                        onMouseRobotListAdapterCallBack.onTagChanged(textField.getText().trim(), item, position, MouseRobotListAdapter.this);
                    }
                }
            }
        });
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    if (onMouseRobotListAdapterCallBack != null) {
                        onMouseRobotListAdapterCallBack.onTagChanged(textField.getText().trim(), item, position, MouseRobotListAdapter.this);
                    }
                }
            }
        });
        return textField;
    }

    /**
     * 创建动坐标点X文本框
     */
    private TextField createXTextField(MouseRobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(60);
        textField.setText(item.x + "");
        textField.setPromptText("光标X轴");

        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    try {
                        list.get(position).x = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.onPointChanged(item, position, MouseRobotListAdapter.this);
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
                            onMouseRobotListAdapterCallBack.onPointChanged(item, position, MouseRobotListAdapter.this);
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
        textField.setPrefWidth(60);
        textField.setText(item.y + "");
        textField.setPromptText("光标Y轴");

        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    try {
                        list.get(position).y = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.onPointChanged(item, position, MouseRobotListAdapter.this);
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
                            onMouseRobotListAdapterCallBack.onPointChanged(item, position, MouseRobotListAdapter.this);
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
                if (onMouseRobotListAdapterCallBack != null) {
                    onMouseRobotListAdapterCallBack.onActionChanged(item, position, MouseRobotListAdapter.this);
                }
            }
        });
        return button;
    }

    /**
     * 创建动作意图执行完等待时间文本框
     */
    private TextField createIntervalTextField(MouseRobotBean item, int position) {
        TextField textField = new TextField();
        textField.setPrefWidth(60);
        textField.setText(item.interval + "");
        textField.setPromptText("等待时间");
        textField.focusedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    try {
                        list.get(position).interval = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.onIntervalChanged(item, position, MouseRobotListAdapter.this, list);
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
                        list.get(position).interval = Integer.parseInt(textField.getText().trim());
                    } catch (Exception e) {
                    } finally {
                        if (onMouseRobotListAdapterCallBack != null) {
                            onMouseRobotListAdapterCallBack.onIntervalChanged(item, position, MouseRobotListAdapter.this, list);
                        }
                    }
                }
            }
        });
        return textField;
    }

    /**
     * 创建子节点动作按钮
     */
    private Button createChildrenOpenButton(MouseRobotBean item, int position) {
        Button button = new Button();
        button.prefWidthProperty().setValue(25);
        button.prefHeightProperty().setValue(25);
        button.setText(item.close ? "+" : "-");
        button.setStyle(item.child.size() == 0 ? null : "-fx-background-color: gray");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean original = item.close;
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).close = true;
                }

                list.get(position).close = !original;
                refreshData(list);
            }
        });
        return button;
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
                if (onMouseRobotListAdapterCallBack != null) {
                    onMouseRobotListAdapterCallBack.onDelete(item, position, MouseRobotListAdapter.this, listView);
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
        return -1;
    }


    public interface OnMouseRobotListAdapterCallBack {

        void onDelete(MouseRobotBean item, int position, MouseRobotListAdapter adapter, ListView<MouseRobotBean> listView);

        void onTagChanged(String text, MouseRobotBean item, int position, MouseRobotListAdapter adapter);

        void onChildTaskCount(MouseRobotBean item, int position, MouseRobotListAdapter adapter);

        void onActionChanged(MouseRobotBean item, int position, MouseRobotListAdapter adapter);

        void onPointChanged(MouseRobotBean item, int position, MouseRobotListAdapter adapter);

        void onIntervalChanged(MouseRobotBean item, int position, MouseRobotListAdapter adapter, ArrayList<MouseRobotBean> list);

        ListView onCreateChildList(MouseRobotBean parent, ArrayList<MouseRobotBean> child, MouseRobotListAdapter parentAdapter);
    }

}
