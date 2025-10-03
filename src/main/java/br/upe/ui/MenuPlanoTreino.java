package br.upe.ui;

import br.upe.business.IExercicioService;
import br.upe.business.ExercicioService;
import br.upe.business.IPlanoTreinoService;
import br.upe.business.PlanoTreinoService;
import br.upe.data.beans.Exercicio;
import br.upe.data.beans.ItemPlanoTreino;
import br.upe.data.beans.PlanoTreino;
import br.upe.data.repository.IExercicioRepository;
import br.upe.data.repository.impl.ExercicioRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuPlanoTreino {

    private IPlanoTreinoService planoTreinoService;
    private IExercicioService exercicioService;
    private IExercicioRepository exercicioRepository;
    private Scanner sc;
    private int idUsuarioLogado;

    private static final Logger logger = LoggerFactory.getLogger(MenuPlanoTreino.class);

    public MenuPlanoTreino(int idUsuarioLogado) {
        this.planoTreinoService = new PlanoTreinoService();
        this.exercicioService = new ExercicioService();
        this.exercicioRepository = new ExercicioRepositoryImpl();
        this.sc = new Scanner(System.in);
        this.idUsuarioLogado = idUsuarioLogado;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== GERENCIAR PLANOS DE TREINO =====");
            System.out.println("1. Criar Novo Plano de Treino");
            System.out.println("2. Listar Meus Planos de Treino");
            System.out.println("3. Editar Plano de Treino");
            System.out.println("4. Deletar Plano de Treino");
            System.out.println("5. Adicionar Exercício ao Plano");
            System.out.println("6. Remover Exercício do Plano");
            System.out.println("7. Ver Detalhes do Plano");
            System.out.println("8. Voltar");
            System.out.print("\nEscolha uma opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    criarNovoPlano();
                    break;
                case 2:
                    listarMeusPlanos();
                    break;
                case 3:
                    editarPlano();
                    break;
                case 4:
                    deletarPlano();
                    break;
                case 5:
                    adicionarExercicioAoPlano();
                    break;
                case 6:
                    removerExercicioDoPlano();
                    break;
                case 7:
                    verDetalhesDoPlano();
                    break;
                case 8:
                    logger.info("Usuário {} voltando ao menu principal.", idUsuarioLogado);
                    break;
                default:
                    logger.warn("Opção inválida selecionada: {}", opcao);
            }
        } while (opcao != 8);
    }

    private void criarNovoPlano() {
        System.out.println("\n===== CRIAR NOVO PLANO DE TREINO =====");
        System.out.print("Nome do Plano: ");
        String nome = sc.nextLine();

        try {
            PlanoTreino novoPlano = planoTreinoService.criarPlano(idUsuarioLogado, nome);
            logger.info("Plano de Treino '{}' criado com sucesso! ID: {}", novoPlano.getNome(), novoPlano.getIdPlano());
        } catch (IllegalArgumentException e) {
            logger.error("Erro ao criar plano para usuário {}: {}", idUsuarioLogado, e.getMessage(), e);
        }
    }

    private void listarMeusPlanos() {
        System.out.println("\n===== MEUS PLANOS DE TREINO =====");
        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);
        if (planos.isEmpty()) {
            logger.info("Nenhum plano de treino cadastrado para usuário {}.", idUsuarioLogado);
        } else {
            planos.forEach(System.out::println);
        }
    }

    private void editarPlano() {
        System.out.println("\n===== EDITAR PLANO DE TREINO =====");
        System.out.print("Digite o NOME do plano que você deseja editar: ");
        String nomeAtualPlano = sc.nextLine();

        try {
            Optional<PlanoTreino> planoOpt = planoTreinoService.buscarPlanoPorNomeEUsuario(idUsuarioLogado, nomeAtualPlano);
            if (!planoOpt.isPresent()) {
                logger.warn("Plano '{}' não encontrado ou não pertence ao usuário {}.", nomeAtualPlano, idUsuarioLogado);
                return;
            }
            PlanoTreino plano = planoOpt.get();

            System.out.println("Plano atual: " + plano.getNome());
            System.out.println("Deixe o campo em branco se não quiser alterar o valor.");

            System.out.print("Novo Nome do Plano (" + plano.getNome() + "): ");
            String novoNome = sc.nextLine();

            planoTreinoService.editarPlano(idUsuarioLogado, nomeAtualPlano, novoNome.isEmpty() ? null : novoNome);
            logger.info("Plano '{}' atualizado com sucesso pelo usuário {}.", nomeAtualPlano, idUsuarioLogado);

        } catch (IllegalArgumentException e) {
            logger.error("Erro ao editar plano '{}' para usuário {}: {}", nomeAtualPlano, idUsuarioLogado, e.getMessage(), e);
        }
    }

    private void deletarPlano() {
        System.out.println("\n===== DELETAR PLANO DE TREINO =====");
        System.out.print("Digite o NOME do plano que você deseja deletar: ");
        String nomePlano = sc.nextLine();

        try {
            boolean deletado = planoTreinoService.deletarPlano(idUsuarioLogado, nomePlano);
            if (deletado) {
                logger.info("Plano '{}' deletado com sucesso pelo usuário {}.", nomePlano, idUsuarioLogado);
            } else {
                logger.warn("Plano '{}' não encontrado ou não pertence ao usuário {}.", nomePlano, idUsuarioLogado);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Erro ao deletar plano '{}' para usuário {}: {}", nomePlano, idUsuarioLogado, e.getMessage(), e);
        }
    }

    private void adicionarExercicioAoPlano() {
        System.out.println("\n===== ADICIONAR EXERCÍCIO AO PLANO =====");
        System.out.print("Digite o NOME do plano ao qual deseja adicionar o exercício: ");
        String nomePlano = sc.nextLine();

        List<Exercicio> meusExercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        if (meusExercicios.isEmpty()) {
            logger.info("Usuário {} tentou adicionar exercício mas não possui exercícios cadastrados.", idUsuarioLogado);
            return;
        }

        System.out.println("\n--- Seus Exercícios Disponíveis ---");
        meusExercicios.forEach(e -> System.out.println("ID: " + e.getIdExercicio() + ", Nome: " + e.getNome()));
        System.out.print("Digite o ID do exercício que deseja adicionar: ");
        int idExercicio;
        try {
            idExercicio = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("ID de exercício inválido informado pelo usuário {}.", idUsuarioLogado);
            return;
        }

        System.out.print("Digite a Carga (kg) para este exercício neste plano: ");
        int carga;
        try {
            carga = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("Carga inválida informada pelo usuário {}.", idUsuarioLogado);
            return;
        }

        System.out.print("Digite o número de Repetições para este exercício neste plano: ");
        int repeticoes;
        try {
            repeticoes = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("Repetições inválidas informadas pelo usuário {}.", idUsuarioLogado);
            return;
        }

        try {
            planoTreinoService.adicionarExercicioAoPlano(idUsuarioLogado, nomePlano, idExercicio, carga, repeticoes);
            logger.info("Exercício {} adicionado ao plano '{}' pelo usuário {}.", idExercicio, nomePlano, idUsuarioLogado);
        } catch (IllegalArgumentException e) {
            logger.error("Erro ao adicionar exercício {} ao plano '{}' para usuário {}: {}", idExercicio, nomePlano, idUsuarioLogado, e.getMessage(), e);
        }
    }

    private void removerExercicioDoPlano() {
        System.out.println("\n===== REMOVER EXERCÍCIO DO PLANO =====");
        System.out.print("Digite o NOME do plano do qual deseja remover o exercício: ");
        String nomePlano = sc.nextLine();

        Optional<PlanoTreino> planoOpt = planoTreinoService.buscarPlanoPorNomeEUsuario(idUsuarioLogado, nomePlano);
        if (!planoOpt.isPresent()) {
            logger.warn("Plano '{}' não encontrado para usuário {}.", nomePlano, idUsuarioLogado);
            return;
        }
        PlanoTreino plano = planoOpt.get();

        if (plano.getItensTreino().isEmpty()) {
            logger.info("Plano '{}' não possui exercícios para remover.", nomePlano);
            return;
        }

        System.out.println("\n--- Exercícios neste Plano ---");
        for (ItemPlanoTreino item : plano.getItensTreino()) {
            Optional<Exercicio> exercicioDoItemOpt = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
            String nomeExercicio = "Desconhecido";
            if (exercicioDoItemOpt.isPresent() && exercicioDoItemOpt.get().getIdUsuario() == idUsuarioLogado) {
                nomeExercicio = exercicioDoItemOpt.get().getNome();
            }
            System.out.println("ID: " + item.getIdExercicio() + ", Nome: " + nomeExercicio + ", Carga: " + item.getCargaKg() + ", Repetições: " + item.getRepeticoes());
        }

        System.out.print("Digite o ID do exercício que deseja remover do plano: ");
        int idExercicio;
        try {
            idExercicio = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("ID de exercício inválido informado para remoção pelo usuário {}.", idUsuarioLogado);
            return;
        }

        try {
            planoTreinoService.removerExercicioDoPlano(idUsuarioLogado, nomePlano, idExercicio);
            System.out.println("Exercício ID " + idExercicio + " removido do plano '" + nomePlano + "' com sucesso!");
        } catch (IllegalArgumentException e) {
            logger.error("Erro ao remover exercício {} do plano '{}' para usuário {}: {}", idExercicio, nomePlano, idUsuarioLogado, e.getMessage(), e);
        }
    }

    private void verDetalhesDoPlano() {
        System.out.println("\n===== VER DETALHES DO PLANO =====");
        System.out.print("Digite o NOME do plano para ver os detalhes: ");
        String nomePlano = sc.nextLine();

        Optional<PlanoTreino> planoOpt = planoTreinoService.buscarPlanoPorNomeEUsuario(idUsuarioLogado, nomePlano);
        if (!planoOpt.isPresent()) {
            logger.warn("Plano '{}' não encontrado para usuário {}.", nomePlano, idUsuarioLogado);
            return;
        }
        PlanoTreino plano = planoOpt.get();

        System.out.println(plano);

        if (!plano.getItensTreino().isEmpty()) {
            System.out.println("  Detalhes dos Exercícios no Plano:");
            for (ItemPlanoTreino item : plano.getItensTreino()) {
                Optional<Exercicio> exercicioDoItemOpt = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
                String nomeExercicio = "Desconhecido";
                if (exercicioDoItemOpt.isPresent() && exercicioDoItemOpt.get().getIdUsuario() == idUsuarioLogado) {
                    nomeExercicio = exercicioDoItemOpt.get().getNome();
                }
                logger.info("    - {} (ID: {}): Carga {}kg, {} repetições",
                        nomeExercicio, item.getIdExercicio(),
                        item.getCargaKg(), item.getRepeticoes());
            }
        }
    }
}