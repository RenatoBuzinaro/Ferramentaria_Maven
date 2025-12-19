package com.ferramentaria.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.ferramentaria.dao.EmprestimoDAO;
import com.ferramentaria.dao.FerramentaDAO;
import com.ferramentaria.dao.FuncionarioDAO;
import com.ferramentaria.entity.Emprestimo;
import com.ferramentaria.entity.Ferramenta;
import com.ferramentaria.entity.Funcionario;
import com.ferramentaria.util.enums.StatusFerramenta;
import com.ferramentaria.util.enums.TipoFerramenta;

public class FerramentariaService {
    
    private final FerramentaDAO ferramentaDao = new FerramentaDAO();
    private final FuncionarioDAO funcionarioDao = new FuncionarioDAO();
    private final EmprestimoDAO emprestimoDao = new EmprestimoDAO();


    public void cadastrarFerramenta(Ferramenta f) { 
        ferramentaDao.salvar(f); 
    }

    public void cadastrarFuncionario(Funcionario f) { 
        funcionarioDao.salvar(f); 
    }

    public List<Ferramenta> listarFerramentas() { 
        return ferramentaDao.listarTodos(); 
    }


    public void registrarEmprestimo(String codigoFerramenta, String matriculaFuncionario, String documentoExterno) {
        Ferramenta ferramenta = ferramentaDao.buscarPorCodigo(codigoFerramenta);
        Funcionario funcionario = funcionarioDao.buscarPorMatricula(matriculaFuncionario);

        if (ferramenta == null) throw new IllegalArgumentException("Ferramenta não encontrada.");
        if (funcionario == null) throw new IllegalArgumentException("Funcionário não encontrado.");

        if (ferramenta.getStatus() == StatusFerramenta.EM_USO) 
            throw new RuntimeException("Ferramenta já está em uso.");
        
        if (ferramenta.getStatus() == StatusFerramenta.EM_MANUTENCAO)
            throw new RuntimeException("BLOQUEIO: Ferramenta em manutenção.");
            
        if (ferramenta.getStatus() == StatusFerramenta.CALIBRACAO_PENDENTE)
            throw new RuntimeException("BLOQUEIO: Ferramenta com calibração vencida!");

        if (ferramenta.getTipo() == TipoFerramenta.ESPECIALIZADA) {
            String treinoNecessario = ferramenta.getTreinamentoNecessario();
            if (treinoNecessario != null && 
               (funcionario.getTreinamentosHabilitados() == null || 
               !funcionario.getTreinamentosHabilitados().contains(treinoNecessario))) {
                throw new RuntimeException("BLOQUEIO: Funcionário não possui treinamento: " + treinoNecessario);
            }
        }

        if (ferramenta.getTipo() == TipoFerramenta.GABARITO && (documentoExterno == null || documentoExterno.trim().isEmpty())) {
            throw new RuntimeException("BLOQUEIO: Gabarito exige documento externo (Ordem de Fabricação).");
        }

        if (ferramenta.getTipo() == TipoFerramenta.CONSUMIVEL) {
            if (ferramenta.getEstoqueAtual() <= 0) {
                throw new RuntimeException("Estoque esgotado!");
            }
            ferramenta.setEstoqueAtual(ferramenta.getEstoqueAtual() - 1);
            
            if (ferramenta.getEstoqueAtual() <= ferramenta.getEstoqueMinimo()) {
                System.out.println("!!! ALERTA: Estoque Baixo para " + ferramenta.getTitulo() + " !!!");
            }
        } else {
            ferramenta.setStatus(StatusFerramenta.EM_USO);
        }

        Emprestimo emprestimo = new Emprestimo(ferramenta, funcionario);
        emprestimo.setDocumentoAssociado(documentoExterno);
        
        emprestimoDao.salvar(emprestimo);    
        ferramentaDao.atualizar(ferramenta); 
    }


    public void registrarDevolucao(Long idEmprestimo, boolean comAvaria) {
        Emprestimo emp = emprestimoDao.buscarPorId(idEmprestimo);
        
        if (emp == null) throw new IllegalArgumentException("Empréstimo não encontrado.");
        if (emp.getDataDevolucao() != null) throw new RuntimeException("Este empréstimo já foi devolvido.");

        Ferramenta f = emp.getFerramenta();

        if (comAvaria) {
            f.setStatus(StatusFerramenta.EM_MANUTENCAO);
            System.out.println(">>> ALERTA: Ferramenta marcada para MANUTENÇÃO.");
        } else if (f.getTipo() != TipoFerramenta.CONSUMIVEL) {
            f.setStatus(StatusFerramenta.DISPONIVEL);
        }

        emp.setDataDevolucao(LocalDateTime.now());
        
        emprestimoDao.atualizar(emp);
        ferramentaDao.atualizar(f);
    }


    public String rastrearFerramenta(Long idFerramenta) {
        Emprestimo emp = emprestimoDao.buscarAbertoPorFerramenta(idFerramenta);
        Ferramenta f = ferramentaDao.buscarPorId(idFerramenta);

        if (f == null) return "Ferramenta não encontrada no sistema.";

        if (emp != null) {
            return String.format(
                "=== EM USO ===\n" +
                "Ferramenta: %s\n" +
                "Responsável: %s (Matr: %s)\n" +
                "Data Retirada: %s\n" +
                "Doc. Associado: %s",
                f.getTitulo(),
                emp.getFuncionario().getTitulo(),
                emp.getFuncionario().getMatricula(),
                emp.getDataEmprestimo(),
                emp.getDocumentoAssociado() != null ? emp.getDocumentoAssociado() : "N/A"
            );
        } else {
            return "Ferramenta '" + f.getTitulo() + "' está no estoque. Status: " + f.getStatus();
        }
    }

  
    public void imprimirHistoricoFuncionario(Long idFuncionario) {
        Funcionario func = funcionarioDao.buscarPorId(idFuncionario);
        if (func == null) throw new IllegalArgumentException("Funcionário não encontrado.");

        List<Emprestimo> historico = emprestimoDao.listarHistoricoPorFuncionario(idFuncionario);
        imprimirListaEmprestimos(historico, "FUNCIONÁRIO: " + func.getTitulo());
    }

  
    public void imprimirHistoricoFerramenta(Long idFerramenta) {
        Ferramenta fer = ferramentaDao.buscarPorId(idFerramenta);
        if (fer == null) throw new IllegalArgumentException("Ferramenta não encontrada.");

        List<Emprestimo> historico = emprestimoDao.listarHistoricoPorFerramenta(idFerramenta);
        imprimirListaEmprestimos(historico, "FERRAMENTA: " + fer.getTitulo());
    }

   
    public void imprimirHistoricoGeralUltimos(int limite) {
        List<Emprestimo> lista = emprestimoDao.listarUltimos(limite);
        imprimirListaEmprestimos(lista, "GERAL (Últimos " + limite + ")");
    }

    
    public void imprimirHistoricoGeralPorData(LocalDate inicio, LocalDate fim) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.atTime(LocalTime.MAX);
        
        List<Emprestimo> lista = emprestimoDao.listarPorPeriodo(dataInicio, dataFim);
        imprimirListaEmprestimos(lista, "PERÍODO: " + inicio + " até " + fim);
    }


    private void imprimirListaEmprestimos(List<Emprestimo> lista, String tituloRelatorio) {
        System.out.println("\n=== RELATÓRIO DE EMPRÉSTIMOS: " + tituloRelatorio + " ===");
        if (lista.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }
        
        System.out.printf("%-5s | %-20s | %-20s | %-12s | %s\n", "ID", "Ferramenta", "Funcionário", "Retirada", "Devolução");
        System.out.println("------------------------------------------------------------------------------------------");
        
        for (Emprestimo e : lista) {
            String dataDevolucao = (e.getDataDevolucao() == null) ? "EM ABERTO" : e.getDataDevolucao().toLocalDate().toString();
            
            System.out.printf("%-5d | %-20s | %-20s | %-12s | %s\n",
                e.getId(), 
                truncate(e.getFerramenta().getTitulo(), 20),
                truncate(e.getFuncionario().getTitulo(), 20),
                e.getDataEmprestimo().toLocalDate(), 
                dataDevolucao
            );
        }
        System.out.println("Total de registros: " + lista.size());
    }

    private String truncate(String str, int width) {
        if (str.length() > width) {
            return str.substring(0, width - 3) + "...";
        }
        return str;
    }
}