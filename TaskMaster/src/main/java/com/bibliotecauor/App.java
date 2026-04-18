package com.bibliotecauor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Instancia LoginView e usa como root do Scene
        com.bibliotecauor.view.LoginView loginView = new com.bibliotecauor.view.LoginView(primaryStage);
        Scene loginScene = new Scene(loginView, 400, 300);
        primaryStage.setTitle("Biblioteca UOR - Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
