package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.module.LeiDianModule;
import sample.module.MouseRobotModule;
import sample.module.ServerModule;

import java.io.IOException;

public class MainApplication extends Application {

    private MainController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("main.fxml"));
        controller = new MainController();
        loader.setController(controller);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
        }
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("脚本工具");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        initialize(primaryStage);
    }

    private void initialize(Stage primaryStage) {
        MouseRobotModule mouseRobotModule = new MouseRobotModule();
        mouseRobotModule.initialize(controller);
        ServerModule serverModule=new ServerModule();
        serverModule.initialize(controller);
        LeiDianModule leiDianModule=new LeiDianModule();
        leiDianModule.initialize(controller);
    }
}
