package com.ferramentaria.entity;


import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PostLoad;
import javax.persistence.Table;

import com.ferramentaria.util.enums.StatusFerramenta;
import com.ferramentaria.util.enums.TipoFerramenta;

@Entity
@Table(name = "ferramenta")
public class Ferramenta extends EntidadeBase {

    @Column(unique = true, nullable = false)
    private String codigoUnico;

    @Enumerated(EnumType.STRING)
    private TipoFerramenta tipo;

    @Enumerated(EnumType.STRING)
    private StatusFerramenta status;

    private LocalDate dataUltimaCalibracao;
    private Integer frequenciaCalibracaoDias;

    private Integer estoqueAtual;
    private Integer estoqueMinimo;

    private String treinamentoNecessario;

    public Ferramenta() {}

    public Ferramenta(String descricao, String codigo, TipoFerramenta tipo) {
        super(descricao);
        this.codigoUnico = codigo;
        this.tipo = tipo;
        this.status = StatusFerramenta.DISPONIVEL;
    }

    @PostLoad
    public void verificarCalibracao() {
        if (this.dataUltimaCalibracao != null && this.frequenciaCalibracaoDias != null) {
            LocalDate vencimento = this.dataUltimaCalibracao.plusDays(this.frequenciaCalibracaoDias);
            if (LocalDate.now().isAfter(vencimento) && this.status == StatusFerramenta.DISPONIVEL) {
                this.status = StatusFerramenta.CALIBRACAO_PENDENTE;
            }
        }
    }

    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }
    
    public TipoFerramenta getTipo() { return tipo; }
    public void setTipo(TipoFerramenta tipo) { this.tipo = tipo; }
    
    public StatusFerramenta getStatus() { return status; }
    public void setStatus(StatusFerramenta status) { this.status = status; }
    
    public LocalDate getDataUltimaCalibracao() { return dataUltimaCalibracao; }
    public void setDataUltimaCalibracao(LocalDate data) { this.dataUltimaCalibracao = data; }
    
    public Integer getFrequenciaCalibracaoDias() { return frequenciaCalibracaoDias; }
    public void setFrequenciaCalibracaoDias(Integer dias) { this.frequenciaCalibracaoDias = dias; }
    
    public Integer getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(Integer estoque) { this.estoqueAtual = estoque; }
    
    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer min) { this.estoqueMinimo = min; }
    
    public String getTreinamentoNecessario() { return treinamentoNecessario; }
    public void setTreinamentoNecessario(String treino) { this.treinamentoNecessario = treino; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Tipo: %s | Status: %s", codigoUnico, getTitulo(), tipo, status);
    }
}