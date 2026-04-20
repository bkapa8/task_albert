package com.bibliotecauor.view;

import com.bibliotecauor.dao.EmprestimoDAO;
import com.bibliotecauor.dao.LivroDAO;
import com.bibliotecauor.dao.CategoriaDAO;
import com.bibliotecauor.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * FuncionarioView: tela para FUNCIONARIO
 * Pode gerenciar (CRUD) empréstimos, reservas pendentes e livros
 */
public class FuncionarioView extends BorderPane {
    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private LivroDAO livroDAO = new LivroDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Stage stage;
    
    private TableView<Emprestimo> emprestimosTable;
    private TableView<Livro> livrosTable;
    private Button aprovarBtn, negarBtn, atualizarBtn, adicionarLivroBtn, editarLivroBtn, deletarLivroBtn, sairBtn;
    private Label usuarioLabel;

    public FuncionarioView(Stage stage, Usuario usuario) {
        this.stage = stage;
        setPadding(new Insets(10));
        
        // TOPO: Info do usuário e logout
        HBox topoInfo = new HBox(10);
        topoInfo.setAlignment(Pos.CENTER_LEFT);
        usuarioLabel = new Label("Funcionário: " + usuario.getNomeCompleto());
        usuarioLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        sairBtn = new Button("Terminar Sessão");
        sairBtn.setStyle("-fx-font-size: 12;");
        sairBtn.setOnAction(e -> handleTerminarSessao());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topoInfo.getChildren().addAll(usuarioLabel, spacer, sairBtn);
        setTop(topoInfo);
        
        // CENTRO: Tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // TAB 1: GERENCIAR EMPRÉSTIMOS E RESERVAS
        Tab tabEmprestimos = new Tab("Empréstimos e Reservas");
        VBox emprestimosBox = new VBox(10);
        emprestimosBox.setPadding(new Insets(10));
        
        Label infoLabel = new Label("Empréstimos e Reservas Pendentes:");
        infoLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        emprestimosTable = new TableView<>();
        emprestimosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Emprestimo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Emprestimo, Integer> colUsuarioId = new TableColumn<>("UsuárioID");
        colUsuarioId.setCellValueFactory(new PropertyValueFactory<>("usuarioId"));
        
        TableColumn<Emprestimo, Integer> colLivroId = new TableColumn<>("LivroID");
        colLivroId.setCellValueFactory(new PropertyValueFactory<>("livroId"));
        
        TableColumn<Emprestimo, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Emprestimo, LocalDate> colDataEmp = new TableColumn<>("Data Empréstimo");
        colDataEmp.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        
        TableColumn<Emprestimo, LocalDate> colDataDev = new TableColumn<>("Data Devolução Prevista");
        colDataDev.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoPrevista"));
        
        emprestimosTable.getColumns().addAll(colId, colUsuarioId, colLivroId, colStatus, colDataEmp, colDataDev);
        
        HBox acoesEmprestimos = new HBox(10);
        acoesEmprestimos.setAlignment(Pos.CENTER_LEFT);
        
        aprovarBtn = new Button("Aprovar");
        aprovarBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        aprovarBtn.setOnAction(e -> handleAproviarEmprestimo());
        
        negarBtn = new Button("Negar");
        negarBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        negarBtn.setOnAction(e -> handleNegarEmprestimo());
        
        atualizarBtn = new Button("Atualizar");
        atualizarBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        atualizarBtn.setOnAction(e -> atualizarEmprestimos());
        
        acoesEmprestimos.getChildren().addAll(aprovarBtn, negarBtn, atualizarBtn);
        
        emprestimosBox.getChildren().addAll(infoLabel, emprestimosTable, acoesEmprestimos);
        tabEmprestimos.setContent(emprestimosBox);
        
        // TAB 2: GERENCIAR LIVROS
        Tab tabLivros = new Tab("Gerenciar Livros");
        VBox livrosBox = new VBox(10);
        livrosBox.setPadding(new Insets(10));
        
        livrosTable = new TableView<>();
        livrosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Livro, Integer> colLId = new TableColumn<>("ID");
        colLId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Livro, String> colISBN = new TableColumn<>("ISBN");
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        
        TableColumn<Livro, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        
        TableColumn<Livro, String> colAutor = new TableColumn<>("Autor");
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        
        TableColumn<Livro, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        
        TableColumn<Livro, Integer> colQtd = new TableColumn<>("Quantidade");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        
        TableColumn<Livro, Boolean> colDisp = new TableColumn<>("Disponível");
        colDisp.setCellValueFactory(new PropertyValueFactory<>("disponivel"));
        
        livrosTable.getColumns().addAll(colLId, colISBN, colTitulo, colAutor, colCategoria, colQtd, colDisp);
        
        HBox acoesLivros = new HBox(10);
        acoesLivros.setAlignment(Pos.CENTER_LEFT);
        
        adicionarLivroBtn = new Button("Adicionar Livro");
        adicionarLivroBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        adicionarLivroBtn.setOnAction(e -> handleAdicionarLivro());
        
        editarLivroBtn = new Button("Editar Livro");
        editarLivroBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        editarLivroBtn.setOnAction(e -> handleEditarLivro());
        
        deletarLivroBtn = new Button("Deletar Livro");
        deletarLivroBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        deletarLivroBtn.setOnAction(e -> handleDeletarLivro());
        
        acoesLivros.getChildren().addAll(adicionarLivroBtn, editarLivroBtn, deletarLivroBtn);
        
        livrosBox.getChildren().addAll(livrosTable, acoesLivros);
        tabLivros.setContent(livrosBox);
        
        tabPane.getTabs().addAll(tabEmprestimos, tabLivros);
        setCenter(tabPane);
        
        // Inicialização
        atualizarEmprestimos();
        atualizarLivros();
    }

    private void atualizarEmprestimos() {
        // Carrega apenas empréstimos com status PENDENTE ou RESERVADO
        List<Emprestimo> emprestimos = emprestimoDAO.getTodosEmprestimos();
        ObservableList<Emprestimo> filtrados = FXCollections.observableArrayList();
        for (Emprestimo e : emprestimos) {
            if ("PENDENTE".equals(e.getStatus()) || "RESERVADO".equals(e.getStatus())) {
                filtrados.add(e);
            }
        }
        emprestimosTable.setItems(filtrados);
    }

    private void atualizarLivros() {
        List<Livro> livros = livroDAO.getTodosLivros();
        ObservableList<Livro> obs = FXCollections.observableArrayList(livros);
        livrosTable.setItems(obs);
    }

    private void handleAproviarEmprestimo() {
        Emprestimo selecionado = emprestimosTable.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um empréstimo para aprovar.");
            return;
        }
        
        // Atualizar status para ATIVO
        String sql = "UPDATE emprestimos SET status = 'ATIVO' WHERE id = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bibliot_uor_db", "root", "");
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selecionado.getId());
            if (ps.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Empréstimo aprovado com sucesso!");
                atualizarEmprestimos();
            }
        } catch (java.sql.SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao aprovar empréstimo: " + e.getMessage());
        }
    }

    private void handleNegarEmprestimo() {
        Emprestimo selecionado = emprestimosTable.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um empréstimo para negar.");
            return;
        }
        
        // Deletar empréstimo negado
        String sql = "DELETE FROM emprestimos WHERE id = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bibliot_uor_db", "root", "");
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selecionado.getId());
            if (ps.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Empréstimo negado!");
                atualizarEmprestimos();
            }
        } catch (java.sql.SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao negar empréstimo: " + e.getMessage());
        }
    }

    private void handleAdicionarLivro() {
        showFormularioLivro(null);
    }

    private void handleEditarLivro() {
        Livro selecionado = livrosTable.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um livro para editar.");
            return;
        }
        showFormularioLivro(selecionado);
    }

    private void handleDeletarLivro() {
        Livro selecionado = livrosTable.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um livro para deletar.");
            return;
        }
        
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Tem certeza que deseja deletar este livro?");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (livroDAO.deletarLivro(selecionado.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Livro deletado com sucesso!");
                atualizarLivros();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro ao deletar livro.");
            }
        }
    }

    private void showFormularioLivro(Livro livro) {
        Dialog<Livro> dialog = new Dialog<>();
        dialog.setTitle(livro == null ? "Adicionar Livro" : "Editar Livro");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        TextField tituloField = new TextField();
        tituloField.setPromptText("Título");
        TextField autorField = new TextField();
        autorField.setPromptText("Autor");
        ComboBox<String> categoriaCombo = new ComboBox<>();
        List<String> categorias = categoriaDAO.getTodasCategorias();
        categoriaCombo.setItems(FXCollections.observableArrayList(categorias));
        categoriaCombo.setPromptText("Selecione categoria");
        TextField quantidadeField = new TextField();
        quantidadeField.setPromptText("Quantidade");
        CheckBox disponivelCheck = new CheckBox("Disponível");
        
        if (livro != null) {
            isbnField.setText(livro.getIsbn());
            tituloField.setText(livro.getTitulo());
            autorField.setText(livro.getAutor());
            categoriaCombo.setValue(livro.getCategoria());
            quantidadeField.setText(String.valueOf(livro.getQuantidade()));
            disponivelCheck.setSelected(livro.isDisponivel());
        }
        
        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Título:"), 0, 1);
        grid.add(tituloField, 1, 1);
        grid.add(new Label("Autor:"), 0, 2);
        grid.add(autorField, 1, 2);
        grid.add(new Label("Categoria:"), 0, 3);
        grid.add(categoriaCombo, 1, 3);
        grid.add(new Label("Quantidade:"), 0, 4);
        grid.add(quantidadeField, 1, 4);
        grid.add(disponivelCheck, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(result -> {
            try {
                if (isbnField.getText().isEmpty() || tituloField.getText().isEmpty() || 
                    autorField.getText().isEmpty() || categoriaCombo.getValue() == null || 
                    quantidadeField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Todos os campos devem ser preenchidos!");
                    return;
                }
                
                Livro novoLivro = new Livro();
                novoLivro.setIsbn(isbnField.getText());
                novoLivro.setTitulo(tituloField.getText());
                novoLivro.setAutor(autorField.getText());
                novoLivro.setCategoria(categoriaCombo.getValue());
                novoLivro.setQuantidade(Integer.parseInt(quantidadeField.getText()));
                novoLivro.setDisponivel(disponivelCheck.isSelected());
                
                if (livro != null) {
                    novoLivro.setId(livro.getId());
                    if (livroDAO.editarLivro(novoLivro)) {
                        showAlert(Alert.AlertType.INFORMATION, "Livro editado com sucesso!");
                        atualizarLivros();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro ao editar livro!");
                    }
                } else {
                    if (livroDAO.adicionarLivro(novoLivro)) {
                        showAlert(Alert.AlertType.INFORMATION, "Livro adicionado com sucesso!");
                        atualizarLivros();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro ao adicionar livro! ISBN já existe?");
                    }
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro ao processar livro: " + e.getMessage());
            }
        });
    }

    private void handleTerminarSessao() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Saída");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja terminar a sessão?");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Criar nova Stage com LoginView
            Stage loginStage = new Stage();
            loginStage.setTitle("Biblioteca UOR - Login");
            loginStage.setScene(new Scene(new LoginView(loginStage), 400, 300));
            loginStage.show();
            // Fechar apenas a Stage atual
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Mensagem");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
