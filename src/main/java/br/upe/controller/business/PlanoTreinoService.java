package br.upe.controller.business;

import br.upe.data.beans.Exercicio;
import br.upe.data.beans.ItemPlanoTreino;
import br.upe.data.beans.PlanoTreino;
import br.upe.data.repository.IExercicioRepository;
import br.upe.data.repository.IPlanoTreinoRepository;
import br.upe.data.repository.impl.ExercicioRepositoryImpl;
import br.upe.data.repository.impl.PlanoTreinoRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class PlanoTreinoService implements IPlanoTreinoService {

    private IPlanoTreinoRepository planoTreinoRepository;
    private IExercicioService exercicioService;
    private IExercicioRepository exercicioRepository;

    public PlanoTreinoService(IPlanoTreinoRepository planoTreinoRepository, IExercicioService exercicioService, IExercicioRepository exercicioRepository) {
        this.planoTreinoRepository = planoTreinoRepository;
        this.exercicioService = exercicioService;
        this.exercicioRepository = exercicioRepository;
    }

    public PlanoTreinoService() {
        this.planoTreinoRepository = new PlanoTreinoRepositoryImpl();
        this.exercicioService = new ExercicioService();
        this.exercicioRepository = new ExercicioRepositoryImpl();
    }

    // Verifica as condições e cria plano de treino
    @Override
    public PlanoTreino criarPlano(int idUsuario, String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do plano não pode ser vazio.");
        }
        Optional<PlanoTreino> planoExistente = planoTreinoRepository.buscarPorNomeEUsuario(idUsuario, nome);
        if (planoExistente.isPresent()) {
            throw new IllegalArgumentException("Você já possui um plano com o nome '" + nome + "'.");
        }
        PlanoTreino novoPlano = new PlanoTreino(idUsuario, nome.trim());
        return planoTreinoRepository.salvar(novoPlano);
    }

    // Verifica as condições e adiciona exercicios ao plano
    @Override
    public void adicionarExercicioAoPlano(int idUsuario, String nomePlano, int idExercicio, int cargaKg, int repeticoes) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (!planoOpt.isPresent()) {
            throw new IllegalArgumentException("Plano '" + nomePlano + "' não encontrado ou não pertence a você.");
        }
        PlanoTreino plano = planoOpt.get();

        Optional<Exercicio> exercicioOpt = exercicioRepository.buscarPorId(idExercicio);
        if (!exercicioOpt.isPresent() || exercicioOpt.get().getIdUsuario() != idUsuario) {
            throw new IllegalArgumentException("Exercício com ID " + idExercicio + " não encontrado ou não pertence a você.");
        }

        boolean exercicioJaNoPlano = plano.getItensTreino().stream()
                .anyMatch(item -> item.getIdExercicio() == idExercicio);
        if (exercicioJaNoPlano) {
            throw new IllegalArgumentException("Exercício já adicionado a este plano. Considere editá-lo.");
        }

        ItemPlanoTreino newItem = new ItemPlanoTreino(idExercicio, cargaKg, repeticoes);
        plano.adicionarItem(newItem);
        planoTreinoRepository.editar(plano);
    }

    // Remove exercicios do plano pelo id
    @Override
    public void removerExercicioDoPlano(int idUsuario, String nomePlano, int idExercicio) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (!planoOpt.isPresent()) {
            throw new IllegalArgumentException("Plano '" + nomePlano + "' não encontrado ou não pertence a você.");
        }
        PlanoTreino plano = planoOpt.get();

        boolean removido = plano.getItensTreino().removeIf(item -> item.getIdExercicio() == idExercicio);
        if (!removido) {
            throw new IllegalArgumentException("Exercício com ID " + idExercicio + " não encontrado neste plano.");
        }
        planoTreinoRepository.editar(plano);
    }

    // Lista o plano pelo usuario
    @Override
    public List<PlanoTreino> listarMeusPlanos(int idUsuario) {
        return planoTreinoRepository.buscarTodosDoUsuario(idUsuario);
    }

    // Lista o plano do usuario pelo nome
    @Override
    public Optional<PlanoTreino> buscarPlanoPorNomeEUsuario(int idUsuario, String nomePlano) {
        if (nomePlano == null || nomePlano.trim().isEmpty()) {
            return Optional.empty();
        }
        return planoTreinoRepository.buscarPorNomeEUsuario(idUsuario, nomePlano.trim());
    }

    // Altera o plano do usuario pelo nome
    @Override
    public void editarPlano(int idUsuario, String nomeAtualPlano, String novoNome) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomeAtualPlano);
        if (!planoOpt.isPresent()) {
            throw new IllegalArgumentException("Plano '" + nomeAtualPlano + "' não encontrado ou não pertence a você.");
        }
        PlanoTreino plano = planoOpt.get();

        if (novoNome != null && !novoNome.trim().isEmpty() && !novoNome.trim().equalsIgnoreCase(plano.getNome())) {
            Optional<PlanoTreino> nomeExistente = planoTreinoRepository.buscarPorNomeEUsuario(idUsuario, novoNome);
            if (nomeExistente.isPresent()) {
                throw new IllegalArgumentException("Você já possui outro plano com o nome '" + novoNome + "'.");
            }
            plano.setNome(novoNome.trim());
        }

        planoTreinoRepository.editar(plano);
    }

    // Deleta o plano
    @Override
    public boolean deletarPlano(int idUsuario, String nomePlano) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (!planoOpt.isPresent()) {
            return false;
        }
        planoTreinoRepository.deletar(planoOpt.get().getIdPlano());
        return true;
    }

    // Lista o plano pelo id
    @Override
    public Optional<PlanoTreino> buscarPlanoPorId(int idPlanoEscolhido) {
        return Optional.empty();
    }
}