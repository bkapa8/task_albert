package com.bibliotecauor.model;

public class Livro {
    private int id;
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private int quantidade;
    private boolean disponivel;

    public Livro() {}

    public Livro(int id, String isbn, String titulo, String autor, String categoria, int quantidade, boolean disponivel) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.disponivel = disponivel;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}
