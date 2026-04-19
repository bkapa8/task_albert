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

public class LeitorView extends BorderPane {
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

    // Nova implementação será criada conforme requisitos do usuário.
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
