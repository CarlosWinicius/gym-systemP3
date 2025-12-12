package br.upe;

import br.upe.data.entities.IndicadorBiomedico;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IIndicadorBiomedicoRepository;
import br.upe.data.dao.IndicadorBiomedicoDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IndicadorBiomedicoRepositoryImplTest {

    private IIndicadorBiomedicoRepository repository;

    @BeforeEach
    void setUp() {
        // Usando DAO JPA
        repository = new IndicadorBiomedicoDAO();
    }

    @AfterEach
    void tearDown() {
        // ambiente de teste (banco) cuida da limpeza
    }

    // Helper para criar IndicadorBiomedico (entidade)
    private IndicadorBiomedico createIndicador(int usuarioId, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra, double imc) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        IndicadorBiomedico ind = new IndicadorBiomedico();
        ind.setUsuario(usuario);
        ind.setDataRegistro(data);
        ind.setPesoKg(pesoKg);
        ind.setAlturaCm(alturaCm);
        ind.setPercentualGordura(percentualGordura);
        ind.setPercentualMassaMagra(percentualMassaMagra);
        ind.setImc(imc);
        return ind;
    }

    @Test
    void testSalvarEBuscarPorId() {
        IndicadorBiomedico indicador = createIndicador(1, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1);

        IndicadorBiomedico salvo = repository.salvar(indicador);

        assertNotEquals(0, salvo.getId());

        Optional<IndicadorBiomedico> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals(1, buscado.get().getUsuario().getId());
    }

    @Test
    void testListarPorUsuario() {
        repository.salvar(createIndicador(1, LocalDate.now().minusDays(1), 80.0, 175.0, 20.0, 70.0, 26.1));
        repository.salvar(createIndicador(1, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1));
        repository.salvar(createIndicador(2, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1));

        List<IndicadorBiomedico> indicadoresUsuario1 = repository.listarPorUsuario(1);
        assertEquals(2, indicadoresUsuario1.size());
    }

    @Test
    void testBuscarPorPeriodo() {
        LocalDate hoje = LocalDate.now();
        repository.salvar(createIndicador(1, hoje.minusDays(10), 80.0, 175.0, 20.0, 70.0, 26.1));
        repository.salvar(createIndicador(1, hoje.minusDays(5), 80.0, 175.0, 20.0, 70.0, 26.1));
        repository.salvar(createIndicador(1, hoje, 80.0, 175.0, 20.0, 70.0, 26.1));

        List<IndicadorBiomedico> resultado = repository.buscarPorPeriodo(1, hoje.minusDays(6), hoje.plusDays(1));
        assertEquals(2, resultado.size());
    }

    @Test
    void testEditar() {
        IndicadorBiomedico salvo = repository.salvar(createIndicador(1, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1));
        Integer idSalvo = salvo.getId();

        salvo.setPesoKg(85.0);
        repository.editar(salvo);

        Optional<IndicadorBiomedico> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals(85.0, editado.get().getPesoKg());
    }

    @Test
    void testDeletar() {
        IndicadorBiomedico salvo = repository.salvar(createIndicador(1, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1));
        Integer idSalvo = salvo.getId();

        repository.deletar(idSalvo);

        Optional<IndicadorBiomedico> depoisDeDeletar = repository.buscarPorId(idSalvo);
        assertFalse(depoisDeDeletar.isPresent());
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<IndicadorBiomedico> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testEditarIndicadorInexistente() {
        IndicadorBiomedico indicador = createIndicador(1, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1);
        indicador.setId(999);
        repository.editar(indicador);
        Optional<IndicadorBiomedico> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testDeletarIndicadorInexistente() {
        repository.deletar(999);
        assertTrue(repository.listarPorUsuario(1).isEmpty());
    }

    @Test
    void testGerarProximoId() {
        IndicadorBiomedico ind1 = repository.salvar(createIndicador(1, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1));
        IndicadorBiomedico ind2 = repository.salvar(createIndicador(1, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1));

        assertEquals(ind1.getId() + 1, ind2.getId());
    }

    @Test
    void testListarTodos() {
        repository.salvar(createIndicador(1, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1));
        repository.salvar(createIndicador(2, LocalDate.now(), 80.0, 175.0, 20.0, 70.0, 26.1));

        List<IndicadorBiomedico> todos = repository.listarTodos();
        assertEquals(2, todos.size());
    }

    @Test
    void testPersistenciaEmArquivo() {
        IndicadorBiomedico indicador = createIndicador(1, LocalDate.of(2025, 1, 1), 75.5, 180.0, 18.0, 72.0, 23.3);
        repository.salvar(indicador);

        IIndicadorBiomedicoRepository novoRepository = new IndicadorBiomedicoDAO();
        List<IndicadorBiomedico> recuperados = novoRepository.listarPorUsuario(1);

        assertFalse(recuperados.isEmpty());
        assertEquals(75.5, recuperados.getFirst().getPesoKg());
    }
}
