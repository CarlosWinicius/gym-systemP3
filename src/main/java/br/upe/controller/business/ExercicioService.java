package br.upe.controller.business;

import br.upe.data.dao.ExercicioDAO;
import br.upe.data.dao.UsuarioDAO;
import br.upe.data.entities.Exercicio;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IExercicioRepository;
import br.upe.data.interfaces.IUsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExercicioService implements IExercicioService {

    private final IExercicioRepository exercicioRepository;
    private final IUsuarioRepository usuarioRepository; // Necessário para buscar o dono do exercício
    private static final Logger logger = Logger.getLogger(ExercicioService.class.getName());

    // Construtor com Injeção (Ideal para testes)
    public ExercicioService(IExercicioRepository exercicioRepository, IUsuarioRepository usuarioRepository) {
        this.exercicioRepository = exercicioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Construtor Padrão (Instancia seus DAOs concretos)
    public ExercicioService() {
        this.exercicioRepository = new ExercicioDAO();
        this.usuarioRepository = new UsuarioDAO(); // Precisa existir para pegar o objeto Usuario
    }

    @Override
    public Exercicio cadastrarExercicio(int idUsuario, String nome, String descricao, String caminhoGif) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do exercício não pode ser vazio.");
        }

        // 1. Validar nome duplicado
        List<Exercicio> exerciciosDoUsuario = exercicioRepository.buscarTodosDoUsuario(idUsuario);
        boolean nomeJaExiste = exerciciosDoUsuario.stream()
                .anyMatch(e -> e.getNome().equalsIgnoreCase(nome.trim()));

        if (nomeJaExiste) {
            throw new IllegalArgumentException("Você já possui um exercício com o nome '" + nome + "'.");
        }

        // 2. BUSCAR O OBJETO USUÁRIO (A mudança principal)
        // O JPA exige o objeto Usuario real para salvar a relação, não apenas o ID inteiro.
        Usuario usuario = usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // 3. Montar a Entity
        Exercicio novoExercicio = new Exercicio();
        novoExercicio.setUsuario(usuario); // Vincula o objeto Usuario
        novoExercicio.setNome(nome.trim());
        novoExercicio.setDescricao(descricao);
        novoExercicio.setCaminhoGif(caminhoGif);

        // 4. Salvar usando o DAO
        return exercicioRepository.salvar(novoExercicio);
    }

    @Override
    public List<Exercicio> listarExerciciosDoUsuario(int idUsuario) {
        // O seu ExercicioDAO já implementa isso com JPQL
        return exercicioRepository.buscarTodosDoUsuario(idUsuario);
    }

    @Override
    public Optional<Exercicio> buscarExercicioDoUsuarioPorNome(int idUsuario, String nomeExercicio) {
        if (nomeExercicio == null || nomeExercicio.trim().isEmpty()) {
            return Optional.empty();
        }

        // Otimização: Se quiser, pode criar um método específico no DAO para isso (buscarPorNomeEUsuario)
        // Mas filtrar a lista aqui funciona bem para volumes pequenos de dados.
        List<Exercicio> exerciciosDoUsuario = exercicioRepository.buscarTodosDoUsuario(idUsuario);

        return exerciciosDoUsuario.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nomeExercicio.trim()))
                .findFirst();
    }

    @Override
    public Optional<Exercicio> buscarExercicioPorIdGlobal(int idExercicio) {
        // O GenericDAO já fornece o buscarPorId
        return exercicioRepository.buscarPorId(idExercicio);
    }

    @Override
    public boolean deletarExercicioPorNome(int idUsuario, String nomeExercicio) {
        if (nomeExercicio == null || nomeExercicio.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome inválido.");
        }

        Optional<Exercicio> exercicioOpt = buscarExercicioDoUsuarioPorNome(idUsuario, nomeExercicio);

        if (exercicioOpt.isPresent()) {
            // O GenericDAO fornece o deletar pelo ID
            exercicioRepository.deletar(exercicioOpt.get().getId());
            return true;
        } else {
            logger.log(Level.WARNING, "Exercício ''{0}'' não encontrado.", nomeExercicio);
            return false;
        }
    }

    @Override
    public void atualizarExercicio(int idUsuario, String nomeAtualExercicio, String novoNome, String novaDescricao, String novoCaminhoGif) {
        if (nomeAtualExercicio == null || nomeAtualExercicio.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome atual inválido.");
        }

        Optional<Exercicio> exercicioOpt = buscarExercicioDoUsuarioPorNome(idUsuario, nomeAtualExercicio);

        if (exercicioOpt.isPresent()) {
            Exercicio exercicio = exercicioOpt.get();

            // Valida se o novo nome já existe para OUTRO exercício
            if (novoNome != null && !novoNome.trim().isEmpty() && !novoNome.trim().equalsIgnoreCase(exercicio.getNome())) {
                boolean nomeJaExiste = listarExerciciosDoUsuario(idUsuario).stream()
                        .anyMatch(e -> e.getNome().equalsIgnoreCase(novoNome.trim()));
                if (nomeJaExiste) {
                    throw new IllegalArgumentException("Já existe um exercício com o nome '" + novoNome + "'.");
                }
                exercicio.setNome(novoNome.trim());
            }

            if (novaDescricao != null) exercicio.setDescricao(novaDescricao);
            if (novoCaminhoGif != null) exercicio.setCaminhoGif(novoCaminhoGif);

            // O GenericDAO fornece o editar
            exercicioRepository.editar(exercicio);
        } else {
            throw new IllegalArgumentException("Exercício '" + nomeAtualExercicio + "' não encontrado.");
        }
    }
}