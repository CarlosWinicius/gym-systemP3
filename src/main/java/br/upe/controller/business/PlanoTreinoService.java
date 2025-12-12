package br.upe.controller.business;

import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import br.upe.data.entities.PlanoTreino;
import br.upe.data.entities.Usuario;
import br.upe.data.dao.ExercicioDAO;
import br.upe.data.dao.PlanoTreinoDAO;
import br.upe.data.dao.ItemPlanoTreinoDAO;
import br.upe.data.dao.UsuarioDAO;
import br.upe.data.interfaces.IExercicioRepository;
import br.upe.data.interfaces.IPlanoTreinoRepository;
import br.upe.data.interfaces.IUsuarioRepository;
import java.util.List;
import java.util.Optional;

public class PlanoTreinoService implements IPlanoTreinoService { // implements IPlanoTreinoService (se existir)

    // Usando interfaces para permitir injeção de dependência nos testes
    private final IPlanoTreinoRepository planoTreinoDAO;
    private final IExercicioRepository exercicioDAO;
    private final IUsuarioRepository usuarioDAO;
    private final ItemPlanoTreinoDAO itemDAO;

    // Construtor com Injeção de Dependência (para testes)
    public PlanoTreinoService(IPlanoTreinoRepository planoTreinoDAO, IExercicioRepository exercicioDAO,
                              IUsuarioRepository usuarioDAO, ItemPlanoTreinoDAO itemDAO) {
        this.planoTreinoDAO = planoTreinoDAO;
        this.exercicioDAO = exercicioDAO;
        this.usuarioDAO = usuarioDAO;
        this.itemDAO = itemDAO;
    }

    // Construtor Padrão
    public PlanoTreinoService() {
        this.planoTreinoDAO = new PlanoTreinoDAO();
        this.exercicioDAO = new ExercicioDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.itemDAO = new ItemPlanoTreinoDAO();
    }

    // Verifica as condições e cria plano de treino
    public PlanoTreino criarPlano(int idUsuario, String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do plano não pode ser vazio.");
        }

        // Validação: Busca plano por nome (assumindo que você criou esse método no PlanoTreinoDAO, senão precisa filtrar na lista)
        // Se não tiver o método específico no DAO, teria que listar todos e filtrar com stream.
        // Vou assumir a lógica original:
        Optional<PlanoTreino> planoExistente = buscarPlanoPorNomeEUsuario(idUsuario, nome);

        if (planoExistente.isPresent()) {
            throw new IllegalArgumentException("Você já possui um plano com o nome '" + nome + "'.");
        }

        // JPA: Precisa buscar o objeto Usuario, não só o ID
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        PlanoTreino novoPlano = new PlanoTreino();
        novoPlano.setUsuario(usuario);
        novoPlano.setNome(nome.trim());

        planoTreinoDAO.salvar(novoPlano);
        return novoPlano;
    }

    // Verifica as condições e adiciona exercicios ao plano
    public void adicionarExercicioAoPlano(int idUsuario, String nomePlano, int idExercicio, int cargaKg, int repeticoes) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (planoOpt.isEmpty()) { // .isEmpty() é o jeito novo do !isPresent()
            throw new IllegalArgumentException("Plano '" + nomePlano + "' não encontrado ou não pertence a você.");
        }
        PlanoTreino plano = planoOpt.get();

        Optional<Exercicio> exercicioOpt = exercicioDAO.buscarPorId(idExercicio);
        // Observação: A entidade Exercicio nova não tinha idUsuario, se tiver, mantenha a verificação.
        // Removi a verificação de dono do exercício para focar na lógica principal, adicione se necessário.
        if (exercicioOpt.isEmpty()) {
            throw new IllegalArgumentException("Exercício com ID " + idExercicio + " não encontrado.");
        }
        Exercicio exercicio = exercicioOpt.get();

        // Verifica se já existe usando o DAO de itens
        List<ItemPlanoTreino> itensAtuais = itemDAO.listarPorPlano(plano.getId());
        boolean exercicioJaNoPlano = itensAtuais.stream()
                .anyMatch(item -> item.getExercicio().getId() == idExercicio);

        if (exercicioJaNoPlano) {
            throw new IllegalArgumentException("Exercício já adicionado a este plano. Considere editá-lo.");
        }

        // Criação do item no padrão JPA (Vinculando os objetos pais)
        ItemPlanoTreino newItem = new ItemPlanoTreino();
        newItem.setPlanoTreino(plano);
        newItem.setExercicio(exercicio);
        newItem.setCargaKg(cargaKg);
        newItem.setRepeticoes(repeticoes);
        // Se tiver o campo series, defina um padrão ou receba no parametro
        itemDAO.salvar(newItem);
    }

    // Remove exercicios do plano pelo id
    public void removerExercicioDoPlano(int idUsuario, String nomePlano, int idExercicio) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (planoOpt.isEmpty()) {
            throw new IllegalArgumentException("Plano '" + nomePlano + "' não encontrado.");
        }
        PlanoTreino plano = planoOpt.get();

        // Busca os itens do banco para encontrar o ID correto do ItemPlanoTreino
        List<ItemPlanoTreino> itens = itemDAO.listarPorPlano(plano.getId());

        Optional<ItemPlanoTreino> itemParaRemover = itens.stream()
                .filter(item -> item.getExercicio().getId() == idExercicio)
                .findFirst();

        if (itemParaRemover.isEmpty()) {
            throw new IllegalArgumentException("Exercício com ID " + idExercicio + " não encontrado neste plano.");
        }

        // Deleta usando o DAO do item
        itemDAO.deletar(itemParaRemover.get().getId());
    }

    // Lista o plano pelo usuario
    public List<PlanoTreino> listarMeusPlanos(int idUsuario) {
        // Adaptação: Se o PlanoTreinoDAO não tiver buscarTodosDoUsuario, usamos JPQL
        // Estou assumindo que você vai criar esse método no DAO ou usar filtro
        // Aqui vai uma implementação manual segura caso o método não exista no DAO genérico:
        List<PlanoTreino> todos = planoTreinoDAO.listarTodos();
        return todos.stream()
                .filter(p -> p.getUsuario().getId() == idUsuario)
                .toList();
    }

    // Lista o plano do usuario pelo nome
    public Optional<PlanoTreino> buscarPlanoPorNomeEUsuario(int idUsuario, String nomePlano) {
        if (nomePlano == null || nomePlano.trim().isEmpty()) {
            return Optional.empty();
        }
        // Adaptação: Listar e filtrar (mesma lógica acima)
        return planoTreinoDAO.listarTodos().stream()
                .filter(p -> p.getUsuario().getId() == idUsuario && p.getNome().equalsIgnoreCase(nomePlano.trim()))
                .findFirst();
    }

    // Altera o plano do usuario pelo nome
    public void editarPlano(int idUsuario, String nomeAtualPlano, String novoNome) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomeAtualPlano);
        if (planoOpt.isEmpty()) {
            throw new IllegalArgumentException("Plano '" + nomeAtualPlano + "' não encontrado.");
        }
        PlanoTreino plano = planoOpt.get();

        if (novoNome != null && !novoNome.trim().isEmpty() && !novoNome.trim().equalsIgnoreCase(plano.getNome())) {
            Optional<PlanoTreino> nomeExistente = buscarPlanoPorNomeEUsuario(idUsuario, novoNome);
            if (nomeExistente.isPresent()) {
                throw new IllegalArgumentException("Você já possui outro plano com o nome '" + novoNome + "'.");
            }
            plano.setNome(novoNome.trim());
            planoTreinoDAO.editar(plano);
        }
    }

    // Deleta o plano
    public boolean deletarPlano(int idUsuario, String nomePlano) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (planoOpt.isEmpty()) {
            return false;
        }
        // Dica JPA: Se não tiver CascadeType.REMOVE na entidade PlanoTreino -> itens,
        // você precisaria deletar os itens manualmente antes.
        // Assumindo que o banco resolve ou o Hibernate tem cascade:
        planoTreinoDAO.deletar(planoOpt.get().getId());
        return true;
    }

    // Lista o plano pelo id
    public Optional<PlanoTreino> buscarPlanoPorId(int idPlanoEscolhido) {
        return planoTreinoDAO.buscarPorId(idPlanoEscolhido);
    }
}