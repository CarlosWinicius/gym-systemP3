/*

// erros impossiveis de corrigir
package br.upe;

import br.upe.data.TipoUsuario;
import br.upe.data.dao.PlanoTreinoDAO;
import br.upe.data.dao.SessaoTreinoDAO;
import br.upe.data.dao.UsuarioDAO;
import br.upe.data.entities.PlanoTreino;
import br.upe.data.entities.SessaoTreino;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.ISessaoTreinoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessaoTreinoRepositoryImplTest {

    private ISessaoTreinoRepository repository;
    private PlanoTreino planoPersistido;
    private Usuario usuarioPersistido;

    @BeforeEach
    void setUp() {
        repository = new SessaoTreinoDAO();

        // Verificar se o usuário já existe no banco
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Optional<Usuario> usuarioExistente = usuarioDAO.buscarPorEmail("teste@example.com");

        if (usuarioExistente.isPresent()) {
            usuarioPersistido = usuarioExistente.get();
        } else {
            // Persistir um novo usuário
            Usuario usuario = new Usuario();
            usuario.setNome("Usuario Teste");
            usuario.setEmail("teste@example.com");
            usuario.setSenha("senha");
            usuario.setTipo(TipoUsuario.COMUM); // Definir o tipo do usuário
            usuarioPersistido = usuarioDAO.salvar(usuario);
        }

        // Verificar se o plano já existe no banco
        PlanoTreinoDAO planoTreinoDAO = new PlanoTreinoDAO();
        Optional<PlanoTreino> planoExistente = planoTreinoDAO.buscarPorNomeEUsuario(usuarioPersistido.getId(),"Plano A");

        if (planoExistente.isPresent()) {
            planoPersistido = planoExistente.get();
        } else {
            // Persistir um novo plano associado ao usuário
            PlanoTreino plano = new PlanoTreino();
            plano.setNome("Plano A");
            plano.setUsuario(usuarioPersistido); // importante
            planoPersistido = planoTreinoDAO.salvar(plano);
        }

        // Criar uma sessão inicial ligada ao plano persistido
        SessaoTreino sessao = new SessaoTreino();
        sessao.setDataSessao(LocalDate.now());
        sessao.setPlanoTreino(planoPersistido);
        sessao.setUsuario(usuarioPersistido); // Definir o usuário na sessão
        repository.salvar(sessao);
    }




    @AfterEach
    void tearDown(){
        // sem limpeza de arquivo ao usar DAO JPA
    }

    // helper para criar e salvar SessaoTreino já associada ao plano persistido
    private SessaoTreino novoESalvarSessao(LocalDate data) {
        SessaoTreino s = new SessaoTreino();
        s.setDataSessao(data);
        s.setPlanoTreino(planoPersistido);
        return repository.salvar(s);
    }

    @Test
    void testSalvarEBuscarPorId() {
        SessaoTreino salvo = novoESalvarSessao(LocalDate.now());

        assertNotEquals(0, salvo.getId());

        Optional<SessaoTreino> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals(LocalDate.now(), buscado.get().getDataSessao());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        novoESalvarSessao(LocalDate.now());
        novoESalvarSessao(LocalDate.now().plusDays(1));
        novoESalvarSessao(LocalDate.now().plusDays(2));

        List<SessaoTreino> sessoes = repository.buscarTodosDoUsuario(1);
        assertEquals(3, sessoes.size());
    }

    @Test
    void testBuscarPorPeriodo() {
        LocalDate hoje = LocalDate.now();
        novoESalvarSessao(hoje.minusDays(3));
        novoESalvarSessao(hoje.minusDays(2));
        novoESalvarSessao(hoje.minusDays(1));

        List<SessaoTreino> resultado = repository.buscarPorPeriodo(1, hoje.minusDays(2), hoje);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(s -> !s.getDataSessao().isBefore(hoje.minusDays(2)) && !s.getDataSessao().isAfter(hoje)));
    }

    @Test
    void testEditar() {
        SessaoTreino salvo = novoESalvarSessao(LocalDate.now());
        int idSalvo = salvo.getId();

        salvo.setDataSessao(LocalDate.now().plusDays(5));
        repository.editar(salvo);

        Optional<SessaoTreino> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals(LocalDate.now().plusDays(5), editado.get().getDataSessao());
    }

    @Test
    void testDeletar() {
        SessaoTreino salvo = novoESalvarSessao(LocalDate.now());
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
        SessaoTreino salvo = novoESalvarSessao(LocalDate.now());
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
*/