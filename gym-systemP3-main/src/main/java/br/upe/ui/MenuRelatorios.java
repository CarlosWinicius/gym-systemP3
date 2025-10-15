package br.upe.ui;

import br.upe.controller.business.IndicadorBiomedicoService;
import br.upe.controller.business.RelatorioDiferencaIndicadores;

import java.time.LocalDate;
import java.util.Scanner;

public class MenuRelatorios {
    private Scanner sc;
    private int idUsuario;
    private IndicadorBiomedicoService indicadorService;

    public MenuRelatorios(Scanner sc, int idUsuario) {
        this.sc = sc;
        this.idUsuario = idUsuario;
        this.indicadorService = new IndicadorBiomedicoService();
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU DE RELATÓRIOS =====");
            System.out.println("1. Exportar Relatório por Data");
            System.out.println("2. Exportar Relatório de Diferença");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    exportarRelatorioPorData();
                    break;
                case 2:
                    exportarRelatorioDiferenca();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }

    private void exportarRelatorioPorData() {
        try {
            System.out.print("Data início (AAAA-MM-DD): ");
            LocalDate dataInicio = LocalDate.parse(sc.nextLine());

            System.out.print("Data fim (AAAA-MM-DD): ");
            LocalDate dataFim = LocalDate.parse(sc.nextLine());

            String caminho = "src/main/resources/relatorios/relatorio_por_data_" + idUsuario + ".csv";
            indicadorService.exportarRelatorioPorDataParaCsv(idUsuario, dataInicio, dataFim, caminho);

            System.out.println("Relatório por data exportado em: " + caminho);
        } catch (Exception e) {
            System.err.println("Erro ao exportar relatório por data: " + e.getMessage());
        }
    }

    private void exportarRelatorioDiferenca() {
        try {
            System.out.print("Data início (AAAA-MM-DD): ");
            LocalDate dataInicio = LocalDate.parse(sc.nextLine());

            System.out.print("Data fim (AAAA-MM-DD): ");
            LocalDate dataFim = LocalDate.parse(sc.nextLine());

            RelatorioDiferencaIndicadores relatorio = indicadorService.gerarRelatorioDiferenca(idUsuario, dataInicio, dataFim);

            String caminho = "src/main/resources/relatorios/relatorio_diferenca_" + idUsuario + ".csv";
            relatorio.exportarParaCsv(caminho);

            System.out.println("Relatório de diferença exportado em: " + caminho);
        } catch (Exception e) {
            System.err.println("Erro ao exportar relatório de diferença: " + e.getMessage());
        }
    }
}
