package com.bibliotecauor.model;

import java.time.LocalDate;

public class Emprestimo implements Comparable<Emprestimo> {
    private int id;
    private int usuarioId;
    private int livroId;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal;
    private String status;
    private int prioridade;

    public Emprestimo() {}

    public Emprestimo(int id, int usuarioId, int livroId, LocalDate dataEmprestimo, LocalDate dataDevolucaoPrevista, LocalDate dataDevolucaoReal, String status, int prioridade) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoReal = dataDevolucaoReal;
        this.status = status;
        this.prioridade = prioridade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public int getLivroId() { return livroId; }
    public void setLivroId(int livroId) { this.livroId = livroId; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
    public LocalDate getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) { this.dataDevolucaoPrevista = dataDevolucaoPrevista; }
    public LocalDate getDataDevolucaoReal() { return dataDevolucaoReal; }
    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) { this.dataDevolucaoReal = dataDevolucaoReal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

    @Override
    public int compareTo(Emprestimo o) {
        int cmp = Integer.compare(this.prioridade, o.prioridade);
        if (cmp == 0) {
            if (this.dataDevolucaoPrevista != null && o.dataDevolucaoPrevista != null) {
                return this.dataDevolucaoPrevista.compareTo(o.dataDevolucaoPrevista);
            }
        }
        return cmp;
    }
}
