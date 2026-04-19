package com.bibliotecauor.view;

import com.bibliotecauor.dao.EmprestimoDAO;
import com.bibliotecauor.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class AdminView extends BorderPane {
    private EmprestimoDAO dao = new EmprestimoDAO();
    public AdminView(Stage stage) {
        setPadding(new Insets(10));
        TabPane tabPane = new TabPane();
        // Tab 1: Gerir Livros
        Tab tab1 = new Tab("Gerir Livros");
        VBox livrosBox = new VBox(10);
        livrosBox.setPadding(new Insets(10));
        TableView<Livro> livrosTable = new TableView<>();
        livrosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Livro, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Livro, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        TableColumn<Livro, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        TableColumn<Livro, String> colAutor = new TableColumn<>("autor");
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        TableColumn<Livro, Boolean> colDisp = new TableColumn<>("Disponível");
        colDisp.setCellValueFactory(new PropertyValueFactory<>("disponivel"));
        livrosTable.getColumns().addAll(colId, colIsbn, colTitulo, colAutor, colDisp);
        Button addLivroBtn = new Button("Adicionar Livro");
        Button editLivroBtn = new Button("Editar Livro");
        livrosBox.getChildren().addAll(livrosTable, addLivroBtn, editLivroBtn);
        tab1.setContent(livrosBox);
        
        
        
        // Tab 2: Utilizadores
        Tab tab2 = new Tab("Utilizadores");
        VBox usersBox = new VBox(10);
        usersBox.setPadding(new Insets(10));
        TableView<Usuario> usersTable = new TableView<>();
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Usuario, Integer> colUId = new TableColumn<>("ID");
        colUId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Usuario, String> colUName = new TableColumn<>("Username");
        colUName.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<Usuario, String> colUNome = new TableColumn<>("Nome Completo");
        colUNome.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        TableColumn<Usuario, String> colURole = new TableColumn<>("Role");
        colURole.setCellValueFactory(new PropertyValueFactory<>("role"));
        usersTable.getColumns().addAll(colUId, colUName, colUNome, colURole);
        Button addUserBtn = new Button("Adicionar Usuário");
        Button editUserBtn = new Button("Editar Usuário");
        usersBox.getChildren().addAll(usersTable, addUserBtn, editUserBtn);
        tab2.setContent(usersBox);
        
        
        
        
        
        
        
        
        
        // Tab 3: Relatórios Gerais
        Tab tab3 = new Tab("Relatórios Gerais");
        VBox relatorioBox = new VBox(10);
        relatorioBox.setPadding(new Insets(10));
        ComboBox<String> relatorioCombo = new ComboBox<>();
        relatorioCombo.getItems().addAll("Livros", "Usuários", "Empréstimos");
        relatorioCombo.setPromptText("Categoria");
        Button gerarRelatorioBtn = new Button("Gerar");
        TextArea relatorioArea = new TextArea();
        relatorioArea.setEditable(false);
        relatorioArea.setPrefHeight(200);
        relatorioBox.getChildren().addAll(relatorioCombo, gerarRelatorioBtn, relatorioArea);
        tab3.setContent(relatorioBox);
        tabPane.getTabs().addAll(tab1, tab2, tab3);
        setCenter(tabPane);
        
        
        // Inicialização
        atualizarLivros(livrosTable);
        atualizarUsuarios(usersTable);
        
        gerarRelatorioBtn.setOnAction(e -> {
            String cat = relatorioCombo.getValue();
            StringBuilder sb = new StringBuilder();
            if (cat == null) return;
            if (cat.equals("Livros")) {
                for (Livro l : dao.getLivrosDisponiveis()) {
                    sb.append(l.getTitulo()).append(" - ").append(l.getAutor()).append("\n");
                }
            } else if (cat.equals("Usuários")) {
                // Exemplo: listar usuários
                // ...
            } else if (cat.equals("Empréstimos")) {
                // Exemplo: listar empréstimos
                // ...
            }
            relatorioArea.setText(sb.toString());
        });
    }
    private void atualizarLivros(TableView<Livro> table) {
        List<Livro> livros = dao.getLivrosDisponiveis();
        ObservableList<Livro> obs = FXCollections.observableArrayList(livros);
        table.setItems(obs);
    }
    private void atualizarUsuarios(TableView<Usuario> table) {
        // Exemplo: não implementado, pois não há método DAO
        table.setItems(FXCollections.observableArrayList());
    }
}
