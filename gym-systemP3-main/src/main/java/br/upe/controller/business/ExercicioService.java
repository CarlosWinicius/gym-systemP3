package br.upe.controller.business;

import br.upe.data.beans.Exercicio;
import br.upe.data.repository.IExercicioRepository;
import br.upe.data.repository.impl.ExercicioRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class ExercicioService implements IExercicioService {

    private IExercicioRepository exercicioRepository;

    public ExercicioService(IExercicioRepository exercicioRepository) {
        this.exercicioRepository = exercicioRepository;
    }

    public ExercicioService() {
        this.exercicioRepository = new ExercicioRepositoryImpl();
    }

    // Verifica condições e cadastra exercicios no repositorio
    @Override
    public Exercicio cadastrarExercicio(int idUsuario, String nome, String descricao, String caminhoGif) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do exercício não pode ser vazio.");
        }

        List<Exercicio> exerciciosDoUsuario = exercicioRepository.buscarTodosDoUsuario(idUsuario);
        boolean nomeJaExiste = exerciciosDoUsuario.stream()
                .anyMatch(e -> e.getNome().equalsIgnoreCase(nome.trim()));
        if (nomeJaExiste) {
            throw new IllegalArgumentException("Você já possui um exercício com o nome '" + nome + "'.");
        }

        Exercicio novoExercicio = new Exercicio(idUsuario, nome.trim(), descricao, caminhoGif);
        return exercicioRepository.salvar(novoExercicio);
    }

    // Lista exercicios do usuario
    @Override
    public List<Exercicio> listarExerciciosDoUsuario(int idUsuario) {
        return exercicioRepository.buscarTodosDoUsuario(idUsuario);
    }


    // Busca o exercicio pelo nome
    @Override
    public Optional<Exercicio> buscarExercicioDoUsuarioPorNome(int idUsuario, String nomeExercicio) {
        if (nomeExercicio == null || nomeExercicio.trim().isEmpty()) {
            return Optional.empty();
        }

        List<Exercicio> exerciciosDoUsuario = exercicioRepository.buscarTodosDoUsuario(idUsuario);

        return exerciciosDoUsuario.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nomeExercicio.trim()))
                .findFirst();
    }

    // Busca o exercicio pelo id
    @Override
    public Optional<Exercicio> buscarExercicioPorIdGlobal(int idExercicio) {
        return exercicioRepository.buscarPorId(idExercicio);
    }

    // verifica condições e deleta o exercicio pelo nome
    @Override
    public boolean deletarExercicioPorNome(int idUsuario, String nomeExercicio) {
        if (nomeExercicio == null || nomeExercicio.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do exercício para deletar não pode ser vazio.");
        }

        Optional<Exercicio> exercicioParaDeletarOpt = buscarExercicioDoUsuarioPorNome(idUsuario, nomeExercicio);

        if (exercicioParaDeletarOpt.isPresent()) {
            Exercicio exercicioParaDeletar = exercicioParaDeletarOpt.get();
            exercicioRepository.deletar(exercicioParaDeletar.getIdExercicio());
            return true;
        } else {
            System.out.println("Exercício com nome '" + nomeExercicio + "' não encontrado entre os seus exercícios.");
            return false;
        }
    }

    // Alterar exercicios
    @Override
    public void atualizarExercicio(int idUsuario, String nomeAtualExercicio, String novoNome, String novaDescricao, String novoCaminhoGif) {
        validarNomeAtual(nomeAtualExercicio);

        Optional<Exercicio> exercicioOpt = buscarExercicioDoUsuarioPorNome(idUsuario, nomeAtualExercicio);

        if (exercicioOpt.isPresent()) {
            Exercicio exercicio = exercicioOpt.get();

            validarNovoNome(exercicio, novoNome, idUsuario);

            atualizarCampos(exercicio, novoNome, novaDescricao, novoCaminhoGif);

            exercicioRepository.editar(exercicio);
        } else {
            throw new IllegalArgumentException("Erro: Exercício '" + nomeAtualExercicio + "' não encontrado entre os seus exercícios para atualização.");
        }
    }

    private void validarNomeAtual(String nomeAtualExercicio) {
        if (nomeAtualExercicio == null || nomeAtualExercicio.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome atual do exercício não pode ser vazio.");
        }
    }

    private void validarNovoNome(Exercicio exercicio, String novoNome, int idUsuario) {
        if (novoNome != null && !novoNome.trim().isEmpty() && !novoNome.trim().equalsIgnoreCase(exercicio.getNome())) {
            List<Exercicio> exerciciosDoUsuario = exercicioRepository.buscarTodosDoUsuario(idUsuario);
            boolean nomeJaExiste = exerciciosDoUsuario.stream()
                    .anyMatch(e -> e.getNome().equalsIgnoreCase(novoNome.trim()));
            if (nomeJaExiste) {
                throw new IllegalArgumentException("Você já possui um exercício com o novo nome '" + novoNome + "'.");
            }
        }
    }

    private void atualizarCampos(Exercicio exercicio, String novoNome, String novaDescricao, String novoCaminhoGif) {
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            exercicio.setNome(novoNome.trim());
        }
        if (novaDescricao != null) {
            exercicio.setDescricao(novaDescricao);
        }
        if (novoCaminhoGif != null) {
            exercicio.setCaminhoGif(novoCaminhoGif);
        }
    }
}