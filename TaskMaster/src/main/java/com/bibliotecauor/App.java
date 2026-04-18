package com.bibliotecauor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        
        
        Scene loginScene = new Scene(null, 400, 300); 
        primaryStage.setTitle("Biblioteca UOR - Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
