package com.bibliotecauor.view;

import com.bibliotecauor.dao.EmprestimoDAO;
import com.bibliotecauor.model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView extends BorderPane {
    private TextField usernameField;
    private PasswordField passwordField;
    private Label feedbackLabel;
    private Button entrarButton;
    private Button sairButton;
    private EmprestimoDAO dao = new EmprestimoDAO();

    public LoginView(Stage primaryStage) {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));

        Label title = new Label("Login Biblioteca UOR");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        usernameField = new TextField();
        usernameField.setPromptText("Usuário");
        usernameField.setMaxWidth(220);

        passwordField = new PasswordField();
        passwordField.setPromptText("Senha");
        passwordField.setMaxWidth(220);

        feedbackLabel = new Label("");
        feedbackLabel.setStyle("-fx-text-fill: red;");

        entrarButton = new Button("Entrar");
        entrarButton.setDefaultButton(true);
        entrarButton.setOnAction(e -> handleLogin(primaryStage));

        sairButton = new Button("Sair");
        sairButton.setOnAction(e -> primaryStage.close());

        vbox.getChildren().addAll(title, usernameField, passwordField, entrarButton, sairButton, feedbackLabel);
        setCenter(vbox);
    }

    private void handleLogin(Stage primaryStage) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText("Preencha todos os campos.");
            return;
        }
        Usuario user = dao.login(username, password);
        if (user == null) {
            feedbackLabel.setText("Usuário ou senha inválidos.");
            // showAlert(Alert.AlertType.ERROR, "Login falhou", "Usuário ou senha inválidos.");
        } else {
            feedbackLabel.setText("");
            if ("ADMIN".equals(user.getRole())) {
                Stage adminStage = new Stage();
                adminStage.setTitle("BibliotecaUOR - Admin");
                adminStage.setScene(new Scene(new AdminView(adminStage, user), 900, 600));
                adminStage.show();
                primaryStage.close();
            } else if ("FUNCIONARIO".equals(user.getRole())) {
                Stage funcStage = new Stage();
                funcStage.setTitle("BibliotecaUOR - Funcionário");
                funcStage.setScene(new Scene(new FuncionarioView(funcStage, user), 1000, 650));
                funcStage.show();
                primaryStage.close();
            } else if ("LEITOR".equals(user.getRole())) {
                Stage leitorStage = new Stage();
                leitorStage.setTitle("BibliotecaUOR - Leitor");
                leitorStage.setScene(new Scene(new LeitorView(leitorStage, user), 1000, 650));
                leitorStage.show();
                primaryStage.close();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        
        alert.showAndWait();
    }
}
