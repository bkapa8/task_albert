package com.bibliotecauor.view;

import com.bibliotecauor.dao.EmprestimoDAO;
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
import java.util.PriorityQueue;

public class FuncionarioView extends BorderPane {
    private EmprestimoDAO dao = new EmprestimoDAO();
    private Usuario usuario;
    private TableView<Livro> livrosTable;
    private TableView<Emprestimo> emprestimosTable;
    private TableView<Emprestimo> filaTable;
    private ComboBox<Livro> livrosCombo;
    private DatePicker dataDevolucaoPicker;
    private TextField motivoField;
    private Button emprestarBtn, devolverBtn, reservarBtn, undoBtn, limparBtn;
    private ComboBox<String> relatorioCombo;
    private Button gerarRelatorioBtn;
    private TextArea relatorioArea;

    public FuncionarioView(Stage stage, Usuario usuario) {
        this.usuario = usuario;
        setPadding(new Insets(10));
        // Topo
        HBox topo = new HBox(10);
        topo.setAlignment(Pos.CENTER_LEFT);
        livrosCombo = new ComboBox<>();
        livrosCombo.setPromptText("Selecione um livro");
        livrosCombo.setMinWidth(200);
        dataDevolucaoPicker = new DatePicker();
        dataDevolucaoPicker.setPromptText("Data Devolução");
        motivoField = new TextField();
        motivoField.setPromptText("Motivo");
        motivoField.setMinWidth(120);
        emprestarBtn = new Button("Emprestar");
        devolverBtn = new Button("Devolver");
        reservarBtn = new Button("Reservar");
        undoBtn = new Button("Undo Última Ação");
        limparBtn = new Button("Limpar");
        topo.getChildren().addAll(livrosCombo, dataDevolucaoPicker, motivoField, emprestarBtn, devolverBtn, reservarBtn, undoBtn, limparBtn);
        setTop(topo);
        // Tabs
        TabPane tabPane = new TabPane();
        // Tab 1: Livros Disponíveis
        Tab tab1 = new Tab("Livros Disponíveis");
        livrosTable = new TableView<>();
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
        tab1.setContent(livrosTable);
        // Tab 2: Meus Empréstimos
        Tab tab2 = new Tab("Meus Empréstimos");
        emprestimosTable = new TableView<>();
        emprestimosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Emprestimo, Integer> colEId = new TableColumn<>("ID");
        colEId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Emprestimo, Integer> colLivroId = new TableColumn<>("LivroID");
        colLivroId.setCellValueFactory(new PropertyValueFactory<>("livroId"));
        TableColumn<Emprestimo, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Emprestimo, LocalDate> colDevPrev = new TableColumn<>("Devolução Prevista");
        colDevPrev.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoPrevista"));
        TableColumn<Emprestimo, LocalDate> colDevReal = new TableColumn<>("Devolução Real");
        colDevReal.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoReal"));
        emprestimosTable.getColumns().addAll(colEId, colLivroId, colStatus, colDevPrev, colDevReal);
        tab2.setContent(emprestimosTable);
        // Tab 3: Fila de Espera
        Tab tab3 = new Tab("Fila de Espera");
        filaTable = new TableView<>();
        filaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Emprestimo, Integer> colFId = new TableColumn<>("ID");
        colFId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Emprestimo, Integer> colFUser = new TableColumn<>("UsuárioID");
        colFUser.setCellValueFactory(new PropertyValueFactory<>("usuarioId"));
        TableColumn<Emprestimo, Integer> colFLivro = new TableColumn<>("LivroID");
        colFLivro.setCellValueFactory(new PropertyValueFactory<>("livroId"));
        TableColumn<Emprestimo, Integer> colFPrioridade = new TableColumn<>("Prioridade");
        colFPrioridade.setCellValueFactory(new PropertyValueFactory<>("prioridade"));
        TableColumn<Emprestimo, LocalDate> colFDevPrev = new TableColumn<>("Devolução Prevista");
        colFDevPrev.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoPrevista"));
        filaTable.getColumns().addAll(colFId, colFUser, colFLivro, colFPrioridade, colFDevPrev);
        tab3.setContent(filaTable);
        // Tab 4: Relatórios
        Tab tab4 = new Tab("Relatórios");
        VBox relatorioBox = new VBox(10);
        relatorioBox.setPadding(new Insets(10));
        relatorioCombo = new ComboBox<>();
        relatorioCombo.getItems().addAll("Livros", "Empréstimos", "Fila de Espera");
        relatorioCombo.setPromptText("Categoria");
        gerarRelatorioBtn = new Button("Gerar");
        relatorioArea = new TextArea();
        relatorioArea.setEditable(false);
        relatorioArea.setPrefHeight(200);
        relatorioBox.getChildren().addAll(relatorioCombo, gerarRelatorioBtn, relatorioArea);
        tab4.setContent(relatorioBox);
        tabPane.getTabs().addAll(tab1, tab2, tab3, tab4);
        setCenter(tabPane);
        // Handlers
        emprestarBtn.setOnAction(e -> handleEmprestar());
        devolverBtn.setOnAction(e -> handleDevolver());
        reservarBtn.setOnAction(e -> handleReservar());
        undoBtn.setOnAction(e -> handleUndo());
        limparBtn.setOnAction(e -> limparCampos());
        gerarRelatorioBtn.setOnAction(e -> gerarRelatorio());
        // Inicialização
        atualizarLivros();
        atualizarEmprestimos();
        atualizarFila();
    }

    private void atualizarLivros() {
        List<Livro> livros = dao.getLivrosDisponiveis();
        ObservableList<Livro> obs = FXCollections.observableArrayList(livros);
        livrosTable.setItems(obs);
        livrosCombo.setItems(obs);
    }
    private void atualizarEmprestimos() {
        List<Emprestimo> hist = dao.getHistoricoEmprestimos(usuario.getId());
        ObservableList<Emprestimo> obs = FXCollections.observableArrayList(hist);
        emprestimosTable.setItems(obs);
    }
    private void atualizarFila() {
        PriorityQueue<Emprestimo> fila = dao.getFilaEspera();
        ObservableList<Emprestimo> obs = FXCollections.observableArrayList(fila);
        filaTable.setItems(obs);
    }
    private void limparCampos() {
        livrosCombo.getSelectionModel().clearSelection();
        dataDevolucaoPicker.setValue(null);
        motivoField.clear();
    }
    private void handleEmprestar() {
        Livro livro = livrosCombo.getValue();
        LocalDate dataDev = dataDevolucaoPicker.getValue();
        if (livro == null || dataDev == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione livro e data de devolução.");
            return;
        }
        Emprestimo e = new Emprestimo();
        e.setUsuarioId(usuario.getId());
        e.setLivroId(livro.getId());
        e.setDataEmprestimo(LocalDate.now());
        e.setDataDevolucaoPrevista(dataDev);
        e.setStatus("EMPRESTADO");
        e.setPrioridade(2);
        if (dao.adicionarEmprestimo(e)) {
            showAlert(Alert.AlertType.INFORMATION, "Empréstimo realizado!");
            atualizarLivros(); atualizarEmprestimos();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro ao emprestar livro.");
        }
    }
    private void handleDevolver() {
        Emprestimo e = emprestimosTable.getSelectionModel().getSelectedItem();
        if (e == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um empréstimo para devolver.");
            return;
        }
        if (dao.devolverLivro(e.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Livro devolvido!");
            atualizarLivros(); atualizarEmprestimos();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro ao devolver livro.");
        }
    }
    private void handleReservar() {
        Livro livro = livrosCombo.getValue();
        if (livro == null) {
            showAlert(Alert.AlertType.WARNING, "Selecione um livro para reservar.");
            return;
        }
        if (dao.reservarLivro(livro.getId(), usuario.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Livro reservado!");
            atualizarFila();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro ao reservar livro.");
        }
    }
    private void handleUndo() {
        if (dao.undoUltimaAcao()) {
            showAlert(Alert.AlertType.INFORMATION, "Ação desfeita!");
            atualizarLivros(); atualizarEmprestimos(); atualizarFila();
        } else {
            showAlert(Alert.AlertType.WARNING, "Nada para desfazer.");
        }
    }
    private void gerarRelatorio() {
        String cat = relatorioCombo.getValue();
        if (cat == null) return;
        StringBuilder sb = new StringBuilder();
        if (cat.equals("Livros")) {
            for (Livro l : dao.getLivrosDisponiveis()) {
                sb.append(l.getTitulo()).append(" - ").append(l.getAutor()).append("\n");
            }
        } else if (cat.equals("Empréstimos")) {
            for (Emprestimo e : dao.getHistoricoEmprestimos(usuario.getId())) {
                sb.append("LivroID: ").append(e.getLivroId()).append(", Status: ").append(e.getStatus()).append("\n");
            }
        } else if (cat.equals("Fila de Espera")) {
            for (Emprestimo e : dao.getFilaEspera()) {
                sb.append("UsuárioID: ").append(e.getUsuarioId()).append(", LivroID: ").append(e.getLivroId()).append(", Prioridade: ").append(e.getPrioridade()).append("\n");
            }
        }
        relatorioArea.setText(sb.toString());
    }
    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Mensagem");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
