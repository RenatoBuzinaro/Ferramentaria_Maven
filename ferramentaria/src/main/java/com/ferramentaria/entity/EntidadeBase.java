package com.ferramentaria.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class EntidadeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String titulo; 

    public EntidadeBase() {}

    public EntidadeBase(String titulo) {
        validaTitulo(titulo);
        this.titulo = titulo;
    }

    protected final void validaTitulo(String titulo) {
        if (titulo == null || titulo.trim().length() < 2) {
            throw new IllegalArgumentException("O título/descrição é obrigatório (min 2 chars).");
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) {
        validaTitulo(titulo);
        this.titulo = titulo;
    }
}