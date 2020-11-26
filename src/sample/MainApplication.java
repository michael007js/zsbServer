package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.module.SetModule;

import java.io.IOException;
import java.net.URL;

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
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("刷钱吧");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        initialize(primaryStage);
    }

    private void initialize(Stage primaryStage) {
        SetModule setModule = new SetModule();
        setModule.initialize(controller);
    }
}
