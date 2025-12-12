package br.upe;

import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import br.upe.data.entities.PlanoTreino;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IPlanoTreinoRepository;
import br.upe.data.dao.PlanoTreinoDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlanoTreinoRepositoryImplTest {

    private IPlanoTreinoRepository repository;

    @BeforeEach
    void setUp() {
        // Usando DAO JPA
        repository = new PlanoTreinoDAO();
    }

    @AfterEach
    void tearDown() {
        // Nenhuma limpeza de arquivo necessária para DAO JPA
    }

    // Helper para criar item (entidade)
    private ItemPlanoTreino createItem(int idExercicio, int cargaKg, int repeticoes) {
        ItemPlanoTreino item = new ItemPlanoTreino();
        Exercicio ex = new Exercicio();
        ex.setId(idExercicio);
        item.setExercicio(ex);
        item.setCargaKg(cargaKg);
        item.setRepeticoes(repeticoes);
        return item;
    }

    // Helper para criar plano (entidade)
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
        PlanoTreino plano = createPlano(1, "Plano 1");
        plano.adicionarItem(createItem(1, 50, 10));

        PlanoTreino salvo = repository.salvar(plano);

        assertNotNull(salvo.getId());
        assertTrue(salvo.getId() > 0);

        Optional<PlanoTreino> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals("Plano 1", buscado.get().getNome());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        PlanoTreino plano1 = createPlano(1, "Plano 1");
        PlanoTreino plano2 = createPlano(1, "Plano 2");
        PlanoTreino plano3 = createPlano(2, "Plano 3");

        repository.salvar(plano1);
        repository.salvar(plano2);
        repository.salvar(plano3);

        List<PlanoTreino> planosUsuario1 = repository.buscarTodosDoUsuario(1);
        assertEquals(2, planosUsuario1.size());
    }

    @Test
    void testEditar() {
        PlanoTreino plano = createPlano(1, "Plano 1");
        PlanoTreino salvo = repository.salvar(plano);
        Integer idSalvo = salvo.getId();

        salvo.setNome("Plano 1 Editado");
        repository.editar(salvo);

        Optional<PlanoTreino> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals("Plano 1 Editado", editado.get().getNome());
    }

    @Test
    void testDeletar() {
        PlanoTreino plano = createPlano(1, "Plano 1");
        PlanoTreino salvo = repository.salvar(plano);
        Integer idSalvo = salvo.getId();

        repository.deletar(idSalvo);

        Optional<PlanoTreino> depoisDeDeletar = repository.buscarPorId(idSalvo);
        assertFalse(depoisDeDeletar.isPresent());
    }

    @Test
    void testBuscarPorNomeEUsuario() {
        PlanoTreino plano = createPlano(1, "Plano 1");
        repository.salvar(plano);

        Optional<PlanoTreino> buscado = repository.buscarPorNomeEUsuario(1, "Plano 1");
        assertTrue(buscado.isPresent());
        assertEquals("Plano 1", buscado.get().getNome());
    }

    @Test
    void testBuscarPorNomeEUsuarioInexistente() {
        Optional<PlanoTreino> buscado = repository.buscarPorNomeEUsuario(1, "Inexistente");
        assertFalse(buscado.isPresent());
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<PlanoTreino> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testEditarPlanoInexistente() {
        PlanoTreino plano = createPlano(1, "Plano");
        plano.setId(999);
        repository.editar(plano);
        Optional<PlanoTreino> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testDeletarPlanoInexistente() {
        repository.deletar(999);
        assertTrue(repository.buscarTodosDoUsuario(1).isEmpty());
    }

    @Test
    void testAtualizar() {
        PlanoTreino plano = createPlano(1, "Plano 1");
        PlanoTreino salvo = repository.salvar(plano);

        salvo.setNome("Plano Atualizado");
        repository.atualizar(salvo);

        Optional<PlanoTreino> atualizado = repository.buscarPorId(salvo.getId());
        assertTrue(atualizado.isPresent());
        assertEquals("Plano Atualizado", atualizado.get().getNome());
    }

    @Test
    void testProximoId() {
        PlanoTreino plano1 = createPlano(1, "Plano 1");
        PlanoTreino plano2 = createPlano(1, "Plano 2");

        PlanoTreino salvo1 = repository.salvar(plano1);
        PlanoTreino salvo2 = repository.salvar(plano2);

        assertEquals(salvo1.getId() + 1, salvo2.getId());
    }

    @Test
    void testPersistenciaEmArquivo() {
        PlanoTreino plano = createPlano(1, "Plano Persistente");
        // Recriar o repositório (DAO) e salvar para verificar persistência via JPA
        repository = new PlanoTreinoDAO();
        repository.salvar(plano);

        List<PlanoTreino> planos = repository.buscarTodosDoUsuario(1);
        assertFalse(planos.isEmpty());
        assertEquals("Plano Persistente", planos.get(0).getNome());
    }
}