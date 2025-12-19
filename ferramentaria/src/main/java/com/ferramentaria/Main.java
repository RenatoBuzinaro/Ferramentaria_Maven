package com.ferramentaria;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.ferramentaria.entity.Ferramenta;
import com.ferramentaria.entity.Funcionario;
import com.ferramentaria.service.FerramentariaService;
import com.ferramentaria.util.enums.TipoFerramenta;

public class Main {
    private static final FerramentariaService service = new FerramentariaService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        inicializarDados(); 

        boolean rodando = true;
        while(rodando) {
            System.out.println("\n=== SISTEMA FERRAMENTARIA ===");
            System.out.println("1. Listar Ferramentas");
            System.out.println("2. Registrar Empréstimo");
            System.out.println("3. Registrar Devolução");
            System.out.println("4. Rastrear Ferramenta");
            System.out.println("5. Histórico do Funcionário");
            System.out.println("6. Histórico da Ferramenta");
            System.out.println("7. Histórico GERAL DA EMPRESA (Novo)");
            System.out.println("9. Sair");
            System.out.print("Opção: ");
            String op = scanner.nextLine();

            try {
                switch(op) {
                    case "1": 
                        for(Ferramenta f : service.listarFerramentas()) System.out.println(f);
                        break;
                    case "2": realizarEmprestimo(); break;
                    case "3": realizarDevolucao(); break;
                    case "4": rastrearFerramenta(); break;
                    case "5": historicoFuncionario(); break;
                    case "6": historicoFerramenta(); break;
                    case "7": historicoGeralMenu(); break; // Nova chamada
                    case "9": rodando = false; System.out.println("Encerrando..."); break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("ERRO: " + e.getMessage());
            }
        }
    }


    private static void realizarEmprestimo() {
        System.out.print("Código da Ferramenta: ");
        String cod = scanner.nextLine();
        System.out.print("Matrícula do Funcionário: ");
        String mat = scanner.nextLine();
        System.out.print("Documento Externo (Enter se vazio): ");
        String doc = scanner.nextLine();
        service.registrarEmprestimo(cod, mat, doc);
        System.out.println(">>> Sucesso!");
    }

    private static void realizarDevolucao() {
        System.out.print("ID do Empréstimo: ");
        try {
            Long id = Long.valueOf(scanner.nextLine());
            System.out.print("Houve avaria? (S/N): ");
            boolean avaria = scanner.nextLine().equalsIgnoreCase("S");
            service.registrarDevolucao(id, avaria);
            System.out.println(">>> Devolução registrada!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    private static void rastrearFerramenta() {
        System.out.print("ID da Ferramenta: ");
        try {
            Long id = Long.valueOf(scanner.nextLine());
            System.out.println(service.rastrearFerramenta(id));
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    private static void historicoFuncionario() {
        System.out.print("ID do Funcionário: ");
        try {
            Long id = Long.valueOf(scanner.nextLine());
            service.imprimirHistoricoFuncionario(id);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }
    
    private static void historicoFerramenta() {
        System.out.print("ID da Ferramenta: ");
        try {
            Long id = Long.valueOf(scanner.nextLine());
            service.imprimirHistoricoFerramenta(id);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    private static void historicoGeralMenu() {
        System.out.println("--- Histórico Geral ---");
        System.out.println("1. Últimos 500 registros");
        System.out.println("2. Filtrar por Datas");
        System.out.print("Escolha: ");
        String subOp = scanner.nextLine();

        if (subOp.equals("1")) {
            service.imprimirHistoricoGeralUltimos(500);
        } else if (subOp.equals("2")) {
            try {
                System.out.print("Data Início (dd/MM/yyyy): ");
                LocalDate inicio = LocalDate.parse(scanner.nextLine(), DATE_FMT);
                
                System.out.print("Data Fim (dd/MM/yyyy): ");
                LocalDate fim = LocalDate.parse(scanner.nextLine(), DATE_FMT);
                
                service.imprimirHistoricoGeralPorData(inicio, fim);
            } catch (Exception e) {
                System.out.println("Data inválida. Use o formato dd/MM/yyyy");
            }
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private static void inicializarDados() {
        Funcionario f1 = new Funcionario("João Silva", "M01", "Usinagem");
        f1.setTreinamentosHabilitados("NR10");
        service.cadastrarFuncionario(f1);

        service.cadastrarFerramenta(new Ferramenta("Chave Inglesa", "F01", TipoFerramenta.BASICA));
        
        Ferramenta f2 = new Ferramenta("Multímetro AT", "F02", TipoFerramenta.ESPECIALIZADA);
        f2.setTreinamentoNecessario("NR10");
        f2.setFrequenciaCalibracaoDias(180);
        f2.setDataUltimaCalibracao(LocalDate.now());
        service.cadastrarFerramenta(f2);

        service.cadastrarFerramenta(new Ferramenta("Gabarito Furação", "G05", TipoFerramenta.GABARITO));

        Ferramenta f4 = new Ferramenta("Luva", "C01", TipoFerramenta.CONSUMIVEL);
        f4.setEstoqueAtual(10);
        f4.setEstoqueMinimo(5);
        service.cadastrarFerramenta(f4);
        
        System.out.println("Dados iniciais carregados.");
    }
}