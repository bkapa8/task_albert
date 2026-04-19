package com.bibliotecauor.dao;

import com.bibliotecauor.model.*;
import java.sql.*;
import java.util.*;

public class LivroDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/bibliot_uor_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public List<Livro> getAllLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id ORDER BY l.titulo";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                livros.add(mapResultSetToLivro(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    public List<Livro> getLivrosPorCategoria(int categoriaId) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE l.categoria = ? ORDER BY l.titulo";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    livros.add(mapResultSetToLivro(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    public List<Livro> getLivrosDisponiveis() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE l.disponivel = TRUE AND l.quantidade > 0 ORDER BY l.titulo";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                livros.add(mapResultSetToLivro(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    public Livro getLivroById(int id) {
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE l.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLivro(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Livro> pesquisarLivros(String termo) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE l.titulo LIKE ? OR l.autor LIKE ? ORDER BY l.titulo";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchTerm = "%" + termo + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    livros.add(mapResultSetToLivro(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    public boolean adicionarLivro(Livro livro) {
        String sql = "INSERT INTO livros (isbn, titulo, autor, categoria, quantidade, disponivel) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, livro.getIsbn());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getAutor());
            ps.setInt(4, obterIdCategoria(livro.getCategoria()));
            ps.setInt(5, livro.getQuantidade());
            ps.setBoolean(6, livro.isDisponivel());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        livro.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean atualizarLivro(Livro livro) {
        String sql = "UPDATE livros SET isbn = ?, titulo = ?, autor = ?, categoria = ?, quantidade = ?, disponivel = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, livro.getIsbn());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getAutor());
            ps.setInt(4, obterIdCategoria(livro.getCategoria()));
            ps.setInt(5, livro.getQuantidade());
            ps.setBoolean(6, livro.isDisponivel());
            ps.setInt(7, livro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletarLivro(int livroId) {
        String sql = "DELETE FROM livros WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, livroId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int obterIdCategoria(String categoriaNome) {
        String sql = "SELECT id FROM categorias WHERE nome = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoriaNome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Livro mapResultSetToLivro(ResultSet rs) throws SQLException {
        return new Livro(
            rs.getInt("id"),
            rs.getString("isbn"),
            rs.getString("titulo"),
            rs.getString("autor"),
            rs.getString("categoria_nome"),
            rs.getInt("quantidade"),
            rs.getBoolean("disponivel")
        );
    }
}
