package com.ferramentaria.entity;



import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "emprestimo")
public class Emprestimo extends EntidadeBase {

    @ManyToOne
    @JoinColumn(name = "id_ferramenta", nullable = false)
    private Ferramenta ferramenta;

    @ManyToOne
    @JoinColumn(name = "id_funcionario", nullable = false)
    private Funcionario funcionario;

    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucao;
    private String documentoAssociado; 
    public Emprestimo() {}

    public Emprestimo(Ferramenta f, Funcionario func) {
        super("Emprestimo: " + f.getTitulo());
        this.ferramenta = f;
        this.funcionario = func;
        this.dataEmprestimo = LocalDateTime.now();
    }

    public Ferramenta getFerramenta() { return ferramenta; }
    public void setFerramenta(Ferramenta ferramenta) { this.ferramenta = ferramenta; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public LocalDateTime getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDateTime data) { this.dataEmprestimo = data; }
    public LocalDateTime getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDateTime data) { this.dataDevolucao = data; }
    public String getDocumentoAssociado() { return documentoAssociado; }
    public void setDocumentoAssociado(String doc) { this.documentoAssociado = doc; }
}