package com.bibliotecauor.view;

import com.bibliotecauor.dao.EmprestimoDAO;
import com.bibliotecauor.dao.LivroDAO;
import com.bibliotecauor.dao.CategoriaDAO;
import com.bibliotecauor.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class LeitorView extends BorderPane {
    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private LivroDAO livroDAO = new LivroDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Usuario usuario;
    private Stage stage;
    
    private TableView<Livro> catalogoTable;
    private TableView<Emprestimo> emprestimosTable;
    private ComboBox<String> categoriaCombo;
    private TextField pesquisaField;
    private DatePicker dataDevolucaoPicker;
    private Button pesquisarBtn, emprestarBtn, devolverBtn, reservarBtn, limparBtn, sairBtn;
    private Label usuarioLabel;

    public LeitorView(Stage stage, Usuario usuario) {
        this.stage = stage;
        this.usuario = usuario;
        setPadding(new Insets(10));
        
        // TOPO: Info do usuário e logout
        HBox topoInfo = new HBox(10);
        topoInfo.setAlignment(Pos.CENTER_LEFT);
        usuarioLabel = new Label("Bem-vindo, " + usuario.getNomeCompleto());
        usuarioLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        sairBtn = new Button("Sair");
        sairBtn.setStyle("-fx-font-size: 12;");
        sairBtn.setOnAction(e -> handleSair());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topoInfo.getChildren().addAll(usuarioLabel, spacer, sairBtn);
        
        // LINHA DE BUSCA E FILTROS
        HBox buscaBox = new HBox(10);
        buscaBox.setAlignment(Pos.CENTER_LEFT);
        buscaBox.setPadding(new Insets(10));
        buscaBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5;");
        
        Label categLabel = new Label("Categoria:");
        categoriaCombo = new ComboBox<>();
        carregarCategorias();
        categoriaCombo.setOnAction(e -> atualizarCatalogo());
        
        Label pesqLabel = new Label("Pesquisa:");
        pesquisaField = new TextField();
        pesquisaField.setPromptText("Título ou Autor");
        pesquisaField.setPrefWidth(250);
        pesquisarBtn = new Button("Pesquisar");
        pesquisarBtn.setOnAction(e -> handlePesquisa());
        
        Separator separador = new Separator();
        separador.setOrientation(javafx.geometry.Orientation.VERTICAL);
        buscaBox.getChildren().addAll(categLabel, categoriaCombo, separador, 
                                       pesqLabel, pesquisaField, pesquisarBtn);
        
        VBox topoCompleto = new VBox(10, topoInfo, buscaBox);
        setTop(topoCompleto);
        
        // CENTRO: Tabs com Catálogo e Empréstimos
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // TAB 1: CATÁLOGO
        Tab tabCatalogo = new Tab("Catálogo");
        VBox catalogoBox = new VBox(10);
        catalogoBox.setPadding(new Insets(10));
        
        catalogoTable = new TableView<>();
        catalogoTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Livro, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Livro, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitulo.setPrefWidth(250);
        
        TableColumn<Livro, String> colAutor = new TableColumn<>("Autor");
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAutor.setPrefWidth(150);
        
        TableColumn<Livro, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCategoria.setPrefWidth(120);
        
        TableColumn<Livro, String> colISBN = new TableColumn<>("ISBN");
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colISBN.setPrefWidth(120);
        
        TableColumn<Livro, Boolean> colDisp = new TableColumn<>("Disponível");
        colDisp.setCellValueFactory(new PropertyValueFactory<>("disponivel"));
        colDisp.setPrefWidth(100);
        
        catalogoTable.getColumns().addAll(colId, colTitulo, colAutor, colCategoria, colISBN, colDisp);
        
        HBox acoesCatalogo = new HBox(10);
        acoesCatalogo.setAlignment(Pos.CENTER_LEFT);
        Label dataLabel = new Label("Data de Devolução:");
        dataDevolucaoPicker = new DatePicker();
        dataDevolucaoPicker.setValue(LocalDate.now().plusDays(14)); // 14 dias por padrão
        
        emprestarBtn = new Button("Emprestar");
        emprestarBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        emprestarBtn.setOnAction(e -> handleEmprestar());
        
        reservarBtn = new Button("Reservar");
        reservarBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        reservarBtn.setOnAction(e -> handleReservar());
        
        limparBtn = new Button("Limpar Seleção");
        limparBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        limparBtn.setOnAction(e -> catalogoTable.getSelectionModel().clearSelection());
        
        acoesCatalogo.getChildren().addAll(dataLabel, dataDevolucaoPicker, emprestarBtn, reservarBtn, limparBtn);
        
        catalogoBox.getChildren().addAll(catalogoTable, acoesCatalogo);
        tabCatalogo.setContent(catalogoBox);
        
        // TAB 2: MEUS EMPRÉSTIMOS
        Tab tabEmprestimos = new Tab("Meus Empréstimos");
        VBox emprestimosBox = new VBox(10);
        emprestimosBox.setPadding(new Insets(10));
        
        emprestimosTable = new TableView<>();
        emprestimosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Emprestimo, Integer> colEId = new TableColumn<>("ID");
        colEId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Emprestimo, Integer> colLivroId = new TableColumn<>("LivroID");
        colLivroId.setCellValueFactory(new PropertyValueFactory<>("livroId"));
        
        TableColumn<Emprestimo, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Emprestimo, LocalDate> colDataEmp = new TableColumn<>("Data Empréstimo");
        colDataEmp.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        
        TableColumn<Emprestimo, LocalDate> colDataDev = new TableColumn<>("Data Devolução Prevista");
        colDataDev.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoPrevista"));
        
        TableColumn<Emprestimo, LocalDate> colDataDevReal = new TableColumn<>("Data Devolução Real");
        colDataDevReal.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoReal"));
        
        emprestimosTable.getColumns().addAll(colEId, colLivroId, colStatus, colDataEmp, colDataDev, colDataDevReal);
        
        HBox acoesEmprestimos = new HBox(10);
        acoesEmprestimos.setAlignment(Pos.CENTER_LEFT);
        
        devolverBtn = new Button("Devolver Livro");
        devolverBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        devolverBtn.setOnAction(e -> handleDevolver());
        
        acoesEmprestimos.getChildren().addAll(devolverBtn);
        
        emprestimosBox.getChildren().addAll(emprestimosTable, acoesEmprestimos);
        tabEmprestimos.setContent(emprestimosBox);
        
        tabPane.getTabs().addAll(tabCatalogo, tabEmprestimos);
        setCenter(tabPane);
        
        // Inicialização
        atualizarCatalogo();
        atualizarEmprestimos();
    }

    private void carregarCategorias() {
        List<String> categorias = categoriaDAO.getTodasCategorias();
        ObservableList<String> obs = FXCollections.observableArrayList(categorias);
        categoriaCombo.setItems(obs);
        if (!categorias.isEmpty()) {
            categoriaCombo.getSelectionModel().selectFirst();
        }
    }

    private void atualizarCatalogo() {
        String categoriaSelecionada = categoriaCombo.getValue();
        if (categoriaSelecionada == null) {
            catalogoTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        List<Livro> livros = livroDAO.getLivrosPorCategoria(categoriaSelecionada);
        ObservableList<Livro> obs = FXCollections.observableArrayList(livros);
        catalogoTable.setItems(obs);
    }

    private void handlePesquisa() {
        String termo = pesquisaField.getText().trim();
        if (termo.isEmpty()) {
            atualizarCatalogo();
            return;
        }
        
        List<Livro> livros = livroDAO.pesquisarLivro(termo);
        ObservableList<Livro> obs = FXCollections.observableArrayList(livros);
        catalogoTable.setItems(obs);
    }

    private void atualizarEmprestimos() {
        List<Emprestimo> emprestimos = emprestimoDAO.getHistoricoEmprestimos(usuario.getId());
        ObservableList<Emprestimo> obs = FXCollections.observableArrayList(emprestimos);
        emprestimosTable.setItems(obs);
    }

    private void handleEmprestar() {
        Livro livroSelecionado = catalogoTable.getSelectionModel().getSelectedItem();
        LocalDate dataDev = dataDevolucaoPicker.getValue();
        
        if (livroSelecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um livro para emprestar.");
            return;
        }
        
        if (!livroSelecionado.isDisponivel()) {
            showAlert(Alert.AlertType.WARNING, "Este livro não está disponível no momento. Deseja reservar?");
            return;
        }
        
        if (dataDev == null || dataDev.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Selecione uma data de devolução válida.");
            return;
        }
        
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuarioId(usuario.getId());
        emprestimo.setLivroId(livroSelecionado.getId());
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucaoPrevista(dataDev);
        emprestimo.setStatus("PENDENTE");
        emprestimo.setPrioridade(2);
        
        if (emprestimoDAO.adicionarEmprestimo(emprestimo)) {
            showAlert(Alert.AlertType.INFORMATION, "Empréstimo solicitado com sucesso! Aguarde aprovação do funcionário.");
            atualizarCatalogo();
            atualizarEmprestimos();
            dataDevolucaoPicker.setValue(LocalDate.now().plusDays(14));
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro ao solicitar empréstimo.");
        }
    }

    private void handleReservar() {
        Livro livroSelecionado = catalogoTable.getSelectionModel().getSelectedItem();
        
        if (livroSelecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um livro para reservar.");
            return;
        }
        
        if (emprestimoDAO.reservarLivro(livroSelecionado.getId(), usuario.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Livro reservado com sucesso! Você será notificado quando estiver disponível.");
            atualizarEmprestimos();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro ao reservar livro.");
        }
    }

    private void handleDevolver() {
        Emprestimo emprestimoSelecionado = emprestimosTable.getSelectionModel().getSelectedItem();
        
        if (emprestimoSelecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um empréstimo para devolver.");
            return;
        }
        
        if ("DEVOLVIDO".equals(emprestimoSelecionado.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Este livro já foi devolvido.");
            return;
        }
        
        if (emprestimoDAO.devolverLivro(emprestimoSelecionado.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Livro devolvido com sucesso!");
            atualizarCatalogo();
            atualizarEmprestimos();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro ao devolver livro.");
        }
    }

    private void handleSair() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Saída");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja sair?");
        if (confirmacao.showAndWait().orElse(Alert.AlertType.CANCEL) == Alert.AlertType.OK) {
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
