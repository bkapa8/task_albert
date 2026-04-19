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
import java.util.List;

/**
 * FuncionarioView: tela para FUNCIONARIO (privilégios intermediários)
 * Pode visualizar livros, emprestar, devolver, reservar, mas não gerenciar usuários/livros.
 */
public class FuncionarioView extends BorderPane {
    private EmprestimoDAO dao = new EmprestimoDAO();
    private Usuario usuario;
    private TableView<Livro> livrosTable;
    private TableView<Emprestimo> emprestimosTable;
    private Button emprestarBtn, devolverBtn, reservarBtn, undoBtn, sairBtn;

    public FuncionarioView(Stage stage, Usuario usuario) {
        this.usuario = usuario;
        setPadding(new Insets(10));
        // Topo
        HBox topo = new HBox(10);
        topo.setAlignment(Pos.CENTER_LEFT);
        emprestarBtn = new Button("Emprestar");
        devolverBtn = new Button("Devolver");
        reservarBtn = new Button("Reservar");
        undoBtn = new Button("Undo Última Ação");
        sairBtn = new Button("Sair");
        sairBtn.setOnAction(e -> stage.close());
        topo.getChildren().addAll(emprestarBtn, devolverBtn, reservarBtn, undoBtn, sairBtn);
        setTop(topo);
        // Tabela de livros
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
        setCenter(livrosTable);
        // Tabela de empréstimos
        emprestimosTable = new TableView<>();
        emprestimosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Emprestimo, Integer> colEId = new TableColumn<>("ID");
        colEId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Emprestimo, Integer> colLivroId = new TableColumn<>("LivroID");
        colLivroId.setCellValueFactory(new PropertyValueFactory<>("livroId"));
        TableColumn<Emprestimo, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        emprestimosTable.getColumns().addAll(colEId, colLivroId, colStatus);
        // Layout
        VBox vbox = new VBox(10, livrosTable, emprestimosTable);
        vbox.setPadding(new Insets(10));
        setCenter(vbox);
        // Inicialização
        atualizarLivros();
        atualizarEmprestimos();
        // Handlers (exemplo)
        emprestarBtn.setOnAction(e -> {/* lógica de empréstimo */});
        devolverBtn.setOnAction(e -> {/* lógica de devolução */});
        reservarBtn.setOnAction(e -> {/* lógica de reserva */});
        undoBtn.setOnAction(e -> {/* lógica de undo */});
    }
    private void atualizarLivros() {
        List<Livro> livros = dao.getLivrosDisponiveis();
        ObservableList<Livro> obs = FXCollections.observableArrayList(livros);
        livrosTable.setItems(obs);
    }
    private void atualizarEmprestimos() {
        List<Emprestimo> hist = dao.getHistoricoEmprestimos(usuario.getId());
        ObservableList<Emprestimo> obs = FXCollections.observableArrayList(hist);
        emprestimosTable.setItems(obs);
    }
}
