package com.bibliotecauor.dao;

import java.sql.*;
import java.util.*;

public class CategoriaDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/bibliot_uor_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Obter todas as categorias
     */
    public List<String> getTodasCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT nome FROM categorias ORDER BY nome";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    /**
     * Adicionar categoria
     */
    public boolean adicionarCategoria(String nome) {
        String sql = "INSERT INTO categorias (nome) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletar categoria
     */
    public boolean deletarCategoria(String nome) {
        String sql = "DELETE FROM categorias WHERE nome = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verificar se categoria existe
     */
    public boolean categoriaExiste(String nome) {
        String sql = "SELECT COUNT(*) as count FROM categorias WHERE nome = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
