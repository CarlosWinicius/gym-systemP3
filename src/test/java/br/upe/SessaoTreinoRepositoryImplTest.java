package br.upe;

import br.upe.data.entities.SessaoTreino;
import br.upe.data.interfaces.ISessaoTreinoRepository;
import br.upe.data.dao.SessaoTreinoDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessaoTreinoRepositoryImplTest {

    private ISessaoTreinoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SessaoTreinoDAO();
    }

    @AfterEach
    void tearDown(){
        // sem limpeza de arquivo ao usar DAO JPA
    }

    @Test
    void testSalvarEBuscarPorId() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setDataSessao(LocalDate.now());
        SessaoTreino salvo = repository.salvar(sessao);

        assertNotEquals(0, salvo.getId());

        Optional<SessaoTreino> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals(LocalDate.now(), buscado.get().getDataSessao());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        SessaoTreino s1 = new SessaoTreino();
        s1.setDataSessao(LocalDate.now());
        SessaoTreino s2 = new SessaoTreino();
        s2.setDataSessao(LocalDate.now().plusDays(1));
        SessaoTreino s3 = new SessaoTreino();
        s3.setDataSessao(LocalDate.now().plusDays(2));

        repository.salvar(s1);
        repository.salvar(s2);
        repository.salvar(s3);

        List<SessaoTreino> sessoes = repository.buscarTodosDoUsuario(1);
        assertEquals(3, sessoes.size());
    }

    @Test
    void testBuscarPorPeriodo() {
        LocalDate hoje = LocalDate.now();
        SessaoTreino s1 = new SessaoTreino();
        s1.setDataSessao(hoje.minusDays(3));
        SessaoTreino s2 = new SessaoTreino();
        s2.setDataSessao(hoje.minusDays(2));
        SessaoTreino s3 = new SessaoTreino();
        s3.setDataSessao(hoje.minusDays(1));

        repository.salvar(s1);
        repository.salvar(s2);
        repository.salvar(s3);

        List<SessaoTreino> resultado = repository.buscarPorPeriodo(1, hoje.minusDays(2), hoje);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(s -> !s.getDataSessao().isBefore(hoje.minusDays(2)) && !s.getDataSessao().isAfter(hoje)));
    }

    @Test
    void testEditar() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setDataSessao(LocalDate.now());
        SessaoTreino salvo = repository.salvar(sessao);
        int idSalvo = salvo.getId();

        salvo.setDataSessao(LocalDate.now().plusDays(5));
        repository.editar(salvo);

        Optional<SessaoTreino> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals(LocalDate.now().plusDays(5), editado.get().getDataSessao());
    }

    @Test
    void testDeletar() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setDataSessao(LocalDate.now());
        SessaoTreino salvo = repository.salvar(sessao);
        int idSalvo = salvo.getId();

        repository.deletar(idSalvo);

        Optional<SessaoTreino> depoisDeDeletar = repository.buscarPorId(idSalvo);
        assertFalse(depoisDeDeletar.isPresent());
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<SessaoTreino> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testDeletarSessaoInexistente() {
        repository.deletar(999);
        assertTrue(repository.buscarTodosDoUsuario(1).isEmpty());
    }

    @Test
    void testPersistenciaEmArquivo() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setDataSessao(LocalDate.now());
        SessaoTreino salvo = repository.salvar(sessao);
        int idSalvo = salvo.getId();

        // Simular reinicialização do repositório
        repository = new SessaoTreinoDAO();

        Optional<SessaoTreino> buscado = repository.buscarPorId(idSalvo);
        assertTrue(buscado.isPresent());
        assertEquals(LocalDate.now(), buscado.get().getDataSessao());
    }

    @Test
    void testBuscarPorPeriodoVazio() {
        LocalDate hoje = LocalDate.now();
        List<SessaoTreino> resultado = repository.buscarPorPeriodo(1, hoje.minusDays(7), hoje.minusDays(1));
        assertTrue(resultado.isEmpty());
    }
}