package com.bibliotecauor.dao;

import com.bibliotecauor.model.*;
import java.sql.*;
// Usar java.sql.Date explicitamente para evitar ambiguidade
import java.time.LocalDate;
import java.util.*;

public class EmprestimoDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/bibliot_uor_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private Stack<Emprestimo> undoStack = new Stack<>();

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Retorna Usuario se login válido. role pode ser "ADMIN", "FUNCIONARIO" ou "LEITOR".
     */
    public Usuario login(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                // role pode ser ADMIN, FUNCIONARIO ou LEITOR
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("nomeCompleto"),
                    role
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Livro> getLivrosDisponiveis() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE l.disponivel = TRUE AND l.quantidade > 0";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Livro livro = new Livro(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("categoria_nome"),
                    rs.getInt("quantidade"),
                    rs.getBoolean("disponivel")
                );
                livros.add(livro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    public boolean adicionarEmprestimo(Emprestimo e) {
        String sql = "INSERT INTO emprestimos (usuario_id, livro_id, data_emprestimo, data_devolucao_prevista, status, prioridade) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getUsuarioId());
            ps.setInt(2, e.getLivroId());
            ps.setDate(3, java.sql.Date.valueOf(e.getDataEmprestimo()));
            ps.setDate(4, java.sql.Date.valueOf(e.getDataDevolucaoPrevista()));
            ps.setString(5, e.getStatus());
            ps.setInt(6, e.getPrioridade());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        e.setId(keys.getInt(1));
                        undoStack.push(e);
                    }
                }
                
                
                
                
                String updateLivro = "UPDATE livros SET quantidade = quantidade - 1, disponivel = IF(quantidade-1>0, TRUE, FALSE) WHERE id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateLivro)) {
                    ps2.setInt(1, e.getLivroId());
                    ps2.executeUpdate();
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean devolverLivro(int emprestimoId) {
        String sql = "UPDATE emprestimos SET data_devolucao_real = CURDATE(), status = 'DEVOLVIDO' WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, emprestimoId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                // Atualiza quantidade e disponibilidade do livro
                String updateLivro = "UPDATE livros l JOIN emprestimos e ON l.id = e.livro_id SET l.quantidade = l.quantidade + 1, l.disponivel = TRUE WHERE e.id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateLivro)) {
                    ps2.setInt(1, emprestimoId);
                    ps2.executeUpdate();
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean reservarLivro(int livroId, int usuarioId) {
        // Reserva: status = 'RESERVADO', prioridade = 1 (alta)
        String sql = "INSERT INTO emprestimos (usuario_id, livro_id, data_emprestimo, data_devolucao_prevista, status, prioridade) VALUES (?, ?, CURDATE(), CURDATE(), 'RESERVADO', 1)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, livroId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        Emprestimo e = new Emprestimo();
                        e.setId(keys.getInt(1));
                        e.setUsuarioId(usuarioId);
                        e.setLivroId(livroId);
                        e.setDataEmprestimo(LocalDate.now());
                        e.setDataDevolucaoPrevista(LocalDate.now());
                        e.setStatus("RESERVADO");
                        e.setPrioridade(1);
                        undoStack.push(e);
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public PriorityQueue<Emprestimo> getFilaEspera() {
        PriorityQueue<Emprestimo> fila = new PriorityQueue<>();
        String sql = "SELECT * FROM emprestimos WHERE status = 'RESERVADO' ORDER BY prioridade, data_devolucao_prevista";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Emprestimo e = new Emprestimo(
                    rs.getInt("id"),
                    rs.getInt("usuario_id"),
                    rs.getInt("livro_id"),
                    rs.getDate("data_emprestimo").toLocalDate(),
                    rs.getDate("data_devolucao_prevista").toLocalDate(),
                    rs.getDate("data_devolucao_real") != null ? rs.getDate("data_devolucao_real").toLocalDate() : null,
                    rs.getString("status"),
                    rs.getInt("prioridade")
                );
                fila.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return fila;
    }

    public LinkedList<Emprestimo> getHistoricoEmprestimos(int usuarioId) {
        LinkedList<Emprestimo> historico = new LinkedList<>();
        String sql = "SELECT * FROM emprestimos WHERE usuario_id = ? ORDER BY data_emprestimo DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Emprestimo e = new Emprestimo(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getInt("livro_id"),
                        rs.getDate("data_emprestimo").toLocalDate(),
                        rs.getDate("data_devolucao_prevista").toLocalDate(),
                        rs.getDate("data_devolucao_real") != null ? rs.getDate("data_devolucao_real").toLocalDate() : null,
                        rs.getString("status"),
                        rs.getInt("prioridade")
                    );
                    historico.add(e);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return historico;
    }

    public HashMap<String, Livro> getLivrosPorISBN() {
        HashMap<String, Livro> map = new HashMap<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Livro livro = new Livro(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("categoria_nome"),
                    rs.getInt("quantidade"),
                    rs.getBoolean("disponivel")
                );
                map.put(livro.getIsbn(), livro);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public boolean undoUltimaAcao() {
        if (undoStack.isEmpty()) return false;
        Emprestimo e = undoStack.pop();
        
        
        // Exemplo: desfaz apenas empréstimo inserido
        String sql = "DELETE FROM emprestimos WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                
                
                // Reverte quantidade do livro
                String updateLivro = "UPDATE livros SET quantidade = quantidade + 1, disponivel = TRUE WHERE id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateLivro)) {
                    ps2.setInt(1, e.getLivroId());
                    ps2.executeUpdate();
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
