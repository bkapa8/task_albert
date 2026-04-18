package com.bibliotecauor;

import com.bibliotecauor.view;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Instancia LoginView e usa como root do Scene
        LoginView loginView = new LoginView(primaryStage);
        Scene loginScene = new Scene(loginView, 400, 300);
        primaryStage.setTitle("Biblioteca UOR - Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
