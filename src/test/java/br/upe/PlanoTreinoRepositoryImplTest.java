package br.upe;

import br.upe.data.TipoUsuario;
import br.upe.data.dao.ExercicioDAO;
import br.upe.data.dao.PlanoTreinoDAO;
import br.upe.data.dao.UsuarioDAO;
import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import br.upe.data.entities.PlanoTreino;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IExercicioRepository;
import br.upe.data.interfaces.IPlanoTreinoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlanoTreinoRepositoryImplTest {

    private IPlanoTreinoRepository repository;
    private Exercicio exercicioPersistente;
    private int userId1;
    private int userId2;

    @BeforeEach
    void setUp() {
        repository = new PlanoTreinoDAO();
        IExercicioRepository exercicioRepository = new ExercicioDAO();

        // Persistir usuários necessários
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        Usuario u1 = new Usuario();
        u1.setNome("Usuario 1");
        u1.setEmail("user1+" + System.nanoTime() + "@example.com");
        u1.setSenha("senha1");
        u1.setTipo(TipoUsuario.COMUM); // Definir o tipo do usuário
        userId1 = usuarioDAO.salvar(u1).getId();

        Usuario u2 = new Usuario();
        u2.setNome("Usuario 2");
        u2.setEmail("user2+" + System.nanoTime() + "@example.com");
        u2.setSenha("senha2");
        u2.setTipo(TipoUsuario.COMUM); // Definir o tipo do usuário
        userId2 = usuarioDAO.salvar(u2).getId();

        // Garantir que exista um Exercicio persistido para associar aos itens
        List<Exercicio> existentes = exercicioRepository.buscarTodosDoUsuario(userId1);
        if (existentes.isEmpty()) {
            Exercicio ex = new Exercicio();
            Usuario u = new Usuario();
            u.setId(userId1);
            ex.setUsuario(u);
            ex.setNome("Exercicio Base");
            ex.setDescricao("Base");
            ex.setCaminhoGif("gif");
            exercicioPersistente = exercicioRepository.salvar(ex);
        } else {
            exercicioPersistente = existentes.getFirst();
        }

        // Limpar planos existentes para garantir isolamento dos testes
        for (int userId : new int[]{userId1, userId2}) {
            List<PlanoTreino> lista = repository.buscarTodosDoUsuario(userId);
            for (PlanoTreino p : lista) {
                repository.deletar(p.getId());
            }
        }
    }


    @AfterEach
    void tearDown() {
        for (int userId : new int[]{userId1, userId2}) {
            List<PlanoTreino> lista = repository.buscarTodosDoUsuario(userId);
            for (PlanoTreino p : lista) {
                repository.deletar(p.getId());
            }
        }
    }

    private ItemPlanoTreino createItem() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(exercicioPersistente);
        item.setCargaKg(50);
        item.setRepeticoes(10);
        return item;
    }

    private PlanoTreino createPlano(int usuarioId, String nome) {
        Usuario u = new Usuario();
        u.setId(usuarioId);
        PlanoTreino p = new PlanoTreino();
        p.setUsuario(u);
        p.setNome(nome);
        return p;
    }

    @Test
    void testSalvarEBuscarPorId() {
        PlanoTreino plano = createPlano(userId1, "Plano 1");
        plano.adicionarItem(createItem());

        PlanoTreino salvo = repository.salvar(plano);

        assertNotNull(salvo.getId());
        assertTrue(salvo.getId() > 0);

        Optional<PlanoTreino> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals("Plano 1", buscado.get().getNome());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        PlanoTreino plano1 = createPlano(userId1, "Plano 1");
        PlanoTreino plano2 = createPlano(userId1, "Plano 2");
        PlanoTreino plano3 = createPlano(userId2, "Plano 3");

        repository.salvar(plano1);
        repository.salvar(plano2);
        repository.salvar(plano3);

        List<PlanoTreino> planosUsuario1 = repository.buscarTodosDoUsuario(userId1);
        assertEquals(2, planosUsuario1.size());
    }
}