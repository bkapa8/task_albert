package com.bibliotecauor.dao;

import com.bibliotecauor.model.Livro;
import java.sql.*;
import java.util.*;

public class LivroDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/bibliot_uor_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Obter todos os livros
     */
    public List<Livro> getTodosLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id ORDER BY l.titulo";
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

    /**
     * Obter livros disponíveis
     */
    public List<Livro> getLivrosDisponiveis() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE l.disponivel = TRUE AND l.quantidade > 0 ORDER BY l.titulo";
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

    /**
     * Obter livros por categoria (sem mostrar quantidade)
     */
    public List<Livro> getLivrosPorCategoria(String categoria) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE c.nome = ? ORDER BY l.titulo";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    /**
     * Pesquisar livro por título ou autor (sem mostrar quantidade)
     */
    public List<Livro> pesquisarLivro(String termo) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE l.titulo LIKE ? OR l.autor LIKE ? ORDER BY l.titulo";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + termo + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    /**
     * Obter livro por ID
     */
    public Livro getLivroById(int id) {
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l JOIN categorias c ON l.categoria = c.id WHERE l.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adicionar livro
     */
    public boolean adicionarLivro(Livro livro) {
        String sql = "INSERT INTO livros (isbn, titulo, autor, categoria, quantidade, disponivel) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, livro.getIsbn());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getAutor());
            
            // Obter ID da categoria pelo nome
            int categoriaId = getCategoriaIdByName(livro.getCategoria());
            if (categoriaId == -1) {
                return false; // Categoria não existe
            }
            
            ps.setInt(4, categoriaId);
            ps.setInt(5, livro.getQuantidade());
            ps.setBoolean(6, livro.isDisponivel());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Editar livro
     */
    public boolean editarLivro(Livro livro) {
        String sql = "UPDATE livros SET isbn = ?, titulo = ?, autor = ?, categoria = ?, quantidade = ?, disponivel = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, livro.getIsbn());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getAutor());
            
            int categoriaId = getCategoriaIdByName(livro.getCategoria());
            if (categoriaId == -1) {
                return false;
            }
            
            ps.setInt(4, categoriaId);
            ps.setInt(5, livro.getQuantidade());
            ps.setBoolean(6, livro.isDisponivel());
            ps.setInt(7, livro.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletar livro
     */
    public boolean deletarLivro(int id) {
        String sql = "DELETE FROM livros WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obter ID da categoria pelo nome
     */
    private int getCategoriaIdByName(String nomeProcurado) {
        String sql = "SELECT id FROM categorias WHERE nome = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomeProcurado);
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

    /**
     * Atualizar quantidade de livro
     */
    public boolean atualizarQuantidade(int livroId, int novaQuantidade) {
        String sql = "UPDATE livros SET quantidade = ?, disponivel = IF(? > 0, TRUE, FALSE) WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, novaQuantidade);
            ps.setInt(2, novaQuantidade);
            ps.setInt(3, livroId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
