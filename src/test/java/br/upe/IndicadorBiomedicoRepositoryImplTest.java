package br.upe;

import br.upe.data.TipoUsuario;
import br.upe.data.dao.IndicadorBiomedicoDAO;
import br.upe.data.dao.UsuarioDAO;
import br.upe.data.entities.IndicadorBiomedico;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IIndicadorBiomedicoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IndicadorBiomedicoRepositoryImplTest {

    private IIndicadorBiomedicoRepository repository;
    private int userId1;
    private int userId2;

    @BeforeEach
    void setUp() {
        repository = new IndicadorBiomedicoDAO();

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

        // Limpar indicadores existentes
        for (int userId : new int[]{userId1, userId2}) {
            List<IndicadorBiomedico> lista = repository.listarPorUsuario(userId);
            for (IndicadorBiomedico ind : lista) {
                repository.deletar(ind.getId());
            }
        }
    }


    @AfterEach
    void tearDown() {
        for (int userId : new int[]{userId1, userId2}) {
            List<IndicadorBiomedico> lista = repository.listarPorUsuario(userId);
            for (IndicadorBiomedico ind : lista) {
                repository.deletar(ind.getId());
            }
        }
    }

    private IndicadorBiomedico createIndicador(int usuarioId, LocalDate data) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        IndicadorBiomedico ind = new IndicadorBiomedico();
        ind.setUsuario(usuario);
        ind.setDataRegistro(data);
        ind.setPesoKg(80.0);
        ind.setAlturaCm(175.0);
        ind.setPercentualGordura(20.0);
        ind.setPercentualMassaMagra(70.0);
        ind.setImc(26.1);
        return ind;

    }

    @Test
    void testSalvarEBuscarPorId() {
        IndicadorBiomedico indicador = createIndicador(userId1, LocalDate.now());

        IndicadorBiomedico salvo = repository.salvar(indicador);

        assertNotEquals(0, salvo.getId());

        Optional<IndicadorBiomedico> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals(userId1, buscado.get().getUsuario().getId());
    }

    @Test
    void testListarPorUsuario() {
        repository.salvar(createIndicador(userId1, LocalDate.now().minusDays(1)));
        repository.salvar(createIndicador(userId1, LocalDate.now()));
        repository.salvar(createIndicador(userId2, LocalDate.now()));

        List<IndicadorBiomedico> indicadoresUsuario1 = repository.listarPorUsuario(userId1);
        assertEquals(2, indicadoresUsuario1.size());
    }
}
