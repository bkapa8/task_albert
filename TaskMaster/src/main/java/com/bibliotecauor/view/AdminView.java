package com.bibliotecauor.view;

import com.bibliotecauor.dao.*;
import com.bibliotecauor.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdminView extends BorderPane {
    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private LivroDAO livroDAO = new LivroDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    
    private Stage stage;
    
    private TableView<Livro> livrosTable;
    private TableView<Usuario> usuariosTable;
    private TableView<Emprestimo> filaEsperaTable;
    private TableView<Emprestimo> emprestimosTable;
    private Label usuarioLabel;
    private Button sairBtn;

    public AdminView(Stage stage, Usuario usuario) {
        this.stage = stage;
        setPadding(new Insets(10));
        
        // TOPO: Info do usuário e logout
        HBox topoInfo = new HBox(10);
        topoInfo.setAlignment(Pos.CENTER_LEFT);
        usuarioLabel = new Label("Admin: " + usuario.getNomeCompleto());
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
        
        // TAB 1: GERENCIAR LIVROS
        Tab tabLivros = new Tab("Gerenciar Livros");
        tabLivros.setContent(criarTabLivros());
        
        // TAB 2: GERENCIAR USUÁRIOS
        Tab tabUsuarios = new Tab("Gerenciar Usuários");
        tabUsuarios.setContent(criarTabUsuarios());
        
        // TAB 3: FILA DE ESPERA
        Tab tabFila = new Tab("Fila de Espera");
        tabFila.setContent(criarTabFila());
        
        // TAB 4: GERENCIAR EMPRÉSTIMOS
        Tab tabEmprestimos = new Tab("Gerenciar Empréstimos");
        tabEmprestimos.setContent(criarTabEmprestimos());
        
        // TAB 5: RELATÓRIOS
        Tab tabRelatorios = new Tab("Relatórios");
        tabRelatorios.setContent(criarTabRelatorios());
        
        tabPane.getTabs().addAll(tabLivros, tabUsuarios, tabFila, tabEmprestimos, tabRelatorios);
        setCenter(tabPane);
    }

    private VBox criarTabLivros() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        livrosTable = new TableView<>();
        livrosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Livro, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
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
        
        livrosTable.getColumns().addAll(colId, colISBN, colTitulo, colAutor, colCategoria, colQtd, colDisp);
        
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_LEFT);
        Button adicionarBtn = new Button("Adicionar");
        adicionarBtn.setOnAction(e -> handleAdicionarLivro());
        Button editarBtn = new Button("Editar");
        editarBtn.setOnAction(e -> handleEditarLivro());
        Button deletarBtn = new Button("Deletar");
        deletarBtn.setOnAction(e -> handleDeletarLivro());
        Button atualizarBtn = new Button("Atualizar");
        atualizarBtn.setOnAction(e -> atualizarLivros());
        acoes.getChildren().addAll(adicionarBtn, editarBtn, deletarBtn, atualizarBtn);
        
        box.getChildren().addAll(livrosTable, acoes);
        atualizarLivros();
        return box;
    }

    private VBox criarTabUsuarios() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        usuariosTable = new TableView<>();
        usuariosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Usuario, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Usuario, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome Completo");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        
        TableColumn<Usuario, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        usuariosTable.getColumns().addAll(colId, colUsername, colNome, colRole);
        
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_LEFT);
        Button adicionarBtn = new Button("Adicionar");
        adicionarBtn.setOnAction(e -> handleAdicionarUsuario());
        Button editarBtn = new Button("Editar");
        editarBtn.setOnAction(e -> handleEditarUsuario());
        Button deletarBtn = new Button("Deletar");
        deletarBtn.setOnAction(e -> handleDeletarUsuario());
        Button atualizarBtn = new Button("Atualizar");
        atualizarBtn.setOnAction(e -> atualizarUsuarios());
        acoes.getChildren().addAll(adicionarBtn, editarBtn, deletarBtn, atualizarBtn);
        
        box.getChildren().addAll(usuariosTable, acoes);
        atualizarUsuarios();
        return box;
    }

    private VBox criarTabFila() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        filaEsperaTable = new TableView<>();
        filaEsperaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Emprestimo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Emprestimo, Integer> colUsuarioId = new TableColumn<>("UsuárioID");
        colUsuarioId.setCellValueFactory(new PropertyValueFactory<>("usuarioId"));
        
        TableColumn<Emprestimo, Integer> colLivroId = new TableColumn<>("LivroID");
        colLivroId.setCellValueFactory(new PropertyValueFactory<>("livroId"));
        
        TableColumn<Emprestimo, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Emprestimo, Integer> colPrioridade = new TableColumn<>("Prioridade");
        colPrioridade.setCellValueFactory(new PropertyValueFactory<>("prioridade"));
        
        filaEsperaTable.getColumns().addAll(colId, colUsuarioId, colLivroId, colStatus, colPrioridade);
        
        HBox acoes = new HBox(10);
        Button atualizarBtn = new Button("Atualizar");
        atualizarBtn.setOnAction(e -> atualizarFila());
        acoes.getChildren().addAll(atualizarBtn);
        
        box.getChildren().addAll(filaEsperaTable, acoes);
        atualizarFila();
        return box;
    }

    private VBox criarTabEmprestimos() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
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
        
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_LEFT);
        Button atualizarBtn = new Button("Atualizar");
        atualizarBtn.setOnAction(e -> atualizarEmprestimos());
        acoes.getChildren().addAll(atualizarBtn);
        
        box.getChildren().addAll(emprestimosTable, acoes);
        atualizarEmprestimos();
        return box;
    }

    private VBox criarTabRelatorios() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        HBox filtroBox = new HBox(10);
        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll("Todos os Empréstimos", "Empréstimos Pendentes", "Empréstimos Ativos", "Total de Livros");
        tipoCombo.setPromptText("Selecione um tipo");
        
        Button gerarBtn = new Button("Gerar Relatório");
        TextArea relatorioArea = new TextArea();
        relatorioArea.setEditable(false);
        relatorioArea.setWrapText(true);
        relatorioArea.setPrefHeight(300);
        
        gerarBtn.setOnAction(e -> {
            String tipo = tipoCombo.getValue();
            if (tipo == null) {
                showAlert(Alert.AlertType.WARNING, "Selecione um tipo de relatório.");
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            if ("Todos os Empréstimos".equals(tipo)) {
                // Implementar
                sb.append("Relatório de Todos os Empréstimos\n");
            } else if ("Empréstimos Pendentes".equals(tipo)) {
                sb.append("Relatório de Empréstimos Pendentes\n");
            } else if ("Empréstimos Ativos".equals(tipo)) {
                sb.append("Relatório de Empréstimos Ativos\n");
            } else if ("Total de Livros".equals(tipo)) {
                List<Livro> livros = livroDAO.getTodosLivros();
                sb.append("TOTAL DE LIVROS: ").append(livros.size()).append("\n\n");
                for (Livro l : livros) {
                    sb.append(l.getId()).append(" - ").append(l.getTitulo())
                      .append(" (").append(l.getAutor()).append(") [")
                      .append(l.getQuantidade()).append(" cópias]\n");
                }
            }
            relatorioArea.setText(sb.toString());
        });
        
        filtroBox.getChildren().addAll(tipoCombo, gerarBtn);
        box.getChildren().addAll(filtroBox, relatorioArea);
        return box;
    }

    private void atualizarLivros() {
        List<Livro> livros = livroDAO.getTodosLivros();
        ObservableList<Livro> obs = FXCollections.observableArrayList(livros);
        livrosTable.setItems(obs);
    }

    private void atualizarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.getTodosUsuarios();
        ObservableList<Usuario> obs = FXCollections.observableArrayList(usuarios);
        usuariosTable.setItems(obs);
    }

    private void atualizarFila() {
        java.util.PriorityQueue<Emprestimo> fila = emprestimoDAO.getFilaEspera();
        ObservableList<Emprestimo> obs = FXCollections.observableArrayList(fila);
        filaEsperaTable.setItems(obs);
    }

    private void atualizarEmprestimos() {
        List<Emprestimo> emprestimos = emprestimoDAO.getTodosEmprestimos();
        ObservableList<Emprestimo> obs = FXCollections.observableArrayList(emprestimos);
        emprestimosTable.setItems(obs);
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
        confirmacao.setContentText("Deseja deletar esta reserva?");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (livroDAO.deletarLivro(selecionado.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Livro deletado!");
                atualizarLivros();
            }
        }
    }

    private void showFormularioLivro(Livro livro) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(livro == null ? "Adicionar Livro" : "Editar Livro");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField isbnField = new TextField();
        TextField tituloField = new TextField();
        TextField autorField = new TextField();
        ComboBox<String> categoriaCombo = new ComboBox<>();
        List<String> categorias = categoriaDAO.getTodasCategorias();
        categoriaCombo.setItems(FXCollections.observableArrayList(categorias));
        TextField quantidadeField = new TextField();
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
        
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
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
                        showAlert(Alert.AlertType.INFORMATION, "Livro editado!");
                        atualizarLivros();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro ao editar livro!");
                    }
                } else {
                    if (livroDAO.adicionarLivro(novoLivro)) {
                        showAlert(Alert.AlertType.INFORMATION, "Livro adicionado!");
                        atualizarLivros();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro ao adicionar livro! ISBN já existe?");
                    }
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro: " + e.getMessage());
            }
        }
    }

    private void handleAdicionarUsuario() {
        showFormularioUsuario(null);
    }

    private void handleEditarUsuario() {
        Usuario selecionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um usuário para editar.");
            return;
        }
        showFormularioUsuario(selecionado);
    }

    private void handleDeletarUsuario() {
        Usuario selecionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um usuário para deletar.");
            return;
        }
        
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setContentText("Deseja deletar este usuário?");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (usuarioDAO.deletarUsuario(selecionado.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Usuário deletado!");
                atualizarUsuarios();
            }
        }
    }

    private void showFormularioUsuario(Usuario usuario) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(usuario == null ? "Adicionar Usuário" : "Editar Usuário");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Digite o username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Digite a senha");
        TextField nomeField = new TextField();
        nomeField.setPromptText("Digite o nome completo");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("ADMIN", "FUNCIONARIO", "LEITOR");
        roleCombo.setPromptText("Selecione o role");
        
        if (usuario != null) {
            usernameField.setText(usuario.getUsername());
            passwordField.setText(usuario.getPassword());
            nomeField.setText(usuario.getNomeCompleto());
            roleCombo.setValue(usuario.getRole());
        }
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Senha:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Nome Completo:"), 0, 2);
        grid.add(nomeField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || 
                    nomeField.getText().isEmpty() || roleCombo.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Todos os campos devem ser preenchidos!");
                    return;
                }
                
                Usuario novoUsuario = new Usuario();
                novoUsuario.setUsername(usernameField.getText());
                novoUsuario.setPassword(passwordField.getText());
                novoUsuario.setNomeCompleto(nomeField.getText());
                novoUsuario.setRole(roleCombo.getValue());
                
                if (usuario != null) {
                    novoUsuario.setId(usuario.getId());
                    if (usuarioDAO.editarUsuario(novoUsuario)) {
                        showAlert(Alert.AlertType.INFORMATION, "Usuário editado!");
                        atualizarUsuarios();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro ao editar usuário!");
                    }
                } else {
                    if (usuarioDAO.adicionarUsuario(novoUsuario)) {
                        showAlert(Alert.AlertType.INFORMATION, "Usuário adicionado!");
                        atualizarUsuarios();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro ao adicionar usuário! Username já existe?");
                    }
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro: " + e.getMessage());
            }
        }
    }

    private void handleTerminarSessao() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Saída");
        confirmacao.setContentText("Deseja terminar a sessão?");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
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
