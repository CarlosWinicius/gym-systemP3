package br.upe.controller.business;

import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import br.upe.data.entities.PlanoTreino;
import br.upe.data.entities.Usuario;
import br.upe.data.dao.ExercicioDAO;
import br.upe.data.dao.PlanoTreinoDAO;
import br.upe.data.dao.ItemPlanoTreinoDAO;
import br.upe.data.dao.UsuarioDAO;
import java.util.List;
import java.util.Optional;

public class PlanoTreinoService implements IPlanoTreinoService {

    private final PlanoTreinoDAO planoTreinoDAO;
    private final ExercicioDAO exercicioDAO;
    private final UsuarioDAO usuarioDAO;
    private final ItemPlanoTreinoDAO itemDAO;

    public PlanoTreinoService() {
        this.planoTreinoDAO = new PlanoTreinoDAO();
        this.exercicioDAO = new ExercicioDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.itemDAO = new ItemPlanoTreinoDAO();
    }

    @Override
    public PlanoTreino criarPlano(int idUsuario, String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do plano não pode ser vazio.");
        }

        Optional<PlanoTreino> planoExistente = buscarPlanoPorNomeEUsuario(idUsuario, nome);

        if (planoExistente.isPresent()) {
            throw new IllegalArgumentException("Você já possui um plano com o nome '" + nome + "'.");
        }

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        PlanoTreino novoPlano = new PlanoTreino();
        novoPlano.setUsuario(usuario);
        novoPlano.setNome(nome.trim());

        // CORREÇÃO: Deve retornar a entidade gerenciada/atualizada pelo DAO.
        return planoTreinoDAO.salvar(novoPlano);
    }

    @Override
    public void adicionarExercicioAoPlano(int idUsuario, String nomePlano, int idExercicio, int cargaKg, int repeticoes) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (planoOpt.isEmpty()) {
            throw new IllegalArgumentException("Plano '" + nomePlano + "' não encontrado ou não pertence a você.");
        }
        PlanoTreino plano = planoOpt.get();

        Optional<Exercicio> exercicioOpt = exercicioDAO.buscarPorId(idExercicio);
        if (exercicioOpt.isEmpty()) {
            throw new IllegalArgumentException("Exercício com ID " + idExercicio + " não encontrado.");
        }
        Exercicio exercicio = exercicioOpt.get();

        List<ItemPlanoTreino> itensAtuais = itemDAO.listarPorPlano(plano.getId());
        boolean exercicioJaNoPlano = itensAtuais.stream()
                .anyMatch(item -> item.getExercicio().getId() == idExercicio);

        if (exercicioJaNoPlano) {
            throw new IllegalArgumentException("Exercício já adicionado a este plano. Considere editá-lo.");
        }

        ItemPlanoTreino newItem = new ItemPlanoTreino();
        newItem.setPlanoTreino(plano);
        newItem.setExercicio(exercicio);
        newItem.setCargaKg(cargaKg);
        newItem.setRepeticoes(repeticoes);
        itemDAO.salvar(newItem);
    }

    @Override
    public void removerExercicioDoPlano(int idUsuario, String nomePlano, int idExercicio) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (planoOpt.isEmpty()) {
            throw new IllegalArgumentException("Plano '" + nomePlano + "' não encontrado.");
        }
        PlanoTreino plano = planoOpt.get();

        List<ItemPlanoTreino> itens = itemDAO.listarPorPlano(plano.getId());

        Optional<ItemPlanoTreino> itemParaRemover = itens.stream()
                .filter(item -> item.getExercicio().getId() == idExercicio)
                .findFirst();

        if (itemParaRemover.isEmpty()) {
            throw new IllegalArgumentException("Exercício com ID " + idExercicio + " não encontrado neste plano.");
        }

        itemDAO.deletar(itemParaRemover.get().getId());
    }

    @Override
    public List<PlanoTreino> listarMeusPlanos(int idUsuario) {
        List<PlanoTreino> todos = planoTreinoDAO.listarTodos();
        return todos.stream()
                .filter(p -> p.getUsuario().getId() == idUsuario)
                .toList();
    }

    @Override
    public Optional<PlanoTreino> buscarPlanoPorNomeEUsuario(int idUsuario, String nomePlano) {
        if (nomePlano == null || nomePlano.trim().isEmpty()) {
            return Optional.empty();
        }
        return planoTreinoDAO.listarTodos().stream()
                .filter(p -> p.getUsuario().getId() == idUsuario && p.getNome().equalsIgnoreCase(nomePlano.trim()))
                .findFirst();
    }

    @Override
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

    @Override
    public boolean deletarPlano(int idUsuario, String nomePlano) {
        Optional<PlanoTreino> planoOpt = buscarPlanoPorNomeEUsuario(idUsuario, nomePlano);
        if (planoOpt.isEmpty()) {
            return false;
        }

        planoTreinoDAO.deletar(planoOpt.get().getId());
        return true;
    }

    @Override
    public Optional<PlanoTreino> buscarPlanoPorId(int idPlanoEscolhido) {
        return planoTreinoDAO.buscarPorId(idPlanoEscolhido);
    }

    @Override
    public void atualizarItemTreino(ItemPlanoTreino item) {
        if (item == null || item.getId() == null) {
            throw new IllegalArgumentException("Item inválido para atualização.");
        }
        itemDAO.editar(item);
    }
}