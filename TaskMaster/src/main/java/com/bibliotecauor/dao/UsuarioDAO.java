package com.bibliotecauor.dao;

import com.bibliotecauor.model.Usuario;
import java.sql.*;
import java.util.*;

public class UsuarioDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/bibliot_uor_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Obter todos os usuários
     */
    public List<Usuario> getTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nomeCompleto";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("nomeCompleto"),
                    rs.getString("role")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Obter usuários por role (ADMIN, FUNCIONARIO, LEITOR)
     */
    public List<Usuario> getUsuariosPorRole(String role) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE role = ? ORDER BY nomeCompleto";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nomeCompleto"),
                        rs.getString("role")
                    );
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Obter usuário por ID
     */
    public Usuario getUsuarioById(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nomeCompleto"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adicionar usuário
     */
    public boolean adicionarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (username, password, nomeCompleto, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNomeCompleto());
            ps.setString(4, usuario.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Editar usuário
     */
    public boolean editarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET username = ?, password = ?, nomeCompleto = ?, role = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNomeCompleto());
            ps.setString(4, usuario.getRole());
            ps.setInt(5, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletar usuário
     */
    public boolean deletarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
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
     * Verificar se username já existe
     */
    public boolean usernameExiste(String username) {
        String sql = "SELECT COUNT(*) as count FROM usuarios WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
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
