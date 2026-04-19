package com.bibliotecauor.model;

/**
 * role pode ser "ADMIN", "FUNCIONARIO" ou "LEITOR".
 */
public class Usuario {
    private int id;
    private String username;
    private String password;
    private String nomeCompleto;
    /**
     * "ADMIN", "FUNCIONARIO" ou "LEITOR"
     */
    private String role; 

    public Usuario() {}

    public Usuario(int id, String username, String password, String nomeCompleto, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nomeCompleto = nomeCompleto;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
