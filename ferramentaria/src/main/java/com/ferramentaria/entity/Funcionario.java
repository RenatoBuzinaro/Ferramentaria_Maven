package com.ferramentaria.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "funcionario")
public class Funcionario extends EntidadeBase {

    @Column(unique = true, nullable = false)
    private String matricula;
    private String setor;
    private String treinamentosHabilitados;

    public Funcionario() {}

    public Funcionario(String nome, String matricula, String setor) {
        super(nome);
        this.matricula = matricula;
        this.setor = setor;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
    public String getTreinamentosHabilitados() { return treinamentosHabilitados; }
    public void setTreinamentosHabilitados(String treinamentos) { this.treinamentosHabilitados = treinamentos; }
}