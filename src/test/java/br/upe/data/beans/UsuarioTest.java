package br.upe.data.beans;

import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioTest {

    @InjectMocks
    private Usuario usuarioCompleto;
    private Usuario usuarioSemId;
    private Usuario usuarioNull;

    @BeforeEach
    void setUp() {
        usuarioCompleto = new Usuario(1, "João", "joao@email.com", "senha123", TipoUsuario.ADMIN);
        usuarioSemId = new Usuario("Maria", "maria@email.com", "senha456", TipoUsuario.COMUM);
        usuarioNull = null;
    }
    //testar construtores
    @Test
    @DisplayName("Testando o construtor completo")
    void testConstrutorCompleto() {
        assertEquals(1, usuarioCompleto.getId());
        assertEquals("João", usuarioCompleto.getNome());
        assertEquals("joao@email.com", usuarioCompleto.getEmail());
        assertEquals("senha123", usuarioCompleto.getSenha());
        assertEquals(TipoUsuario.ADMIN, usuarioCompleto.getTipo());
    }
    @Test
    @DisplayName("Testando o construtor sem ID")
    void testConstrutorSemId() {
        assertEquals(0, usuarioSemId.getId()); // ID padrão é 0
        assertEquals("Maria", usuarioSemId.getNome());
        assertEquals("maria@email.com", usuarioSemId.getEmail());
        assertEquals("senha456", usuarioSemId.getSenha());
        assertEquals(TipoUsuario.COMUM, usuarioSemId.getTipo());
    }

    //testar getters e setters
    @Test
    @DisplayName("Testando os getters e setters")
    void testGettersAndSetters() {
        //ID
        usuarioCompleto.setId(2);
        assertEquals(2, usuarioCompleto.getId());
        //Nome
        usuarioCompleto.setNome("Carlos");
        assertEquals("Carlos", usuarioCompleto.getNome());
        //Email
        usuarioCompleto.setEmail("Carlos@email.com");
        assertEquals("Carlos@email.com", usuarioCompleto.getEmail());
        //Senha
        usuarioCompleto.setSenha("senha789");
        assertEquals("senha789", usuarioCompleto.getSenha());
        //Tipo
        usuarioCompleto.setTipo(TipoUsuario.COMUM);
        assertEquals(TipoUsuario.COMUM, usuarioCompleto.getTipo());
    }

    //testar toString
    @Test
    @DisplayName("Testando o método toString")
    void testToString() {
        String expected = "ID: 1, Nome: 'João', Email: 'joao@email.com', Tipo: ADMIN";
        assertEquals(expected, usuarioCompleto.toString());
    }

    //testar valores nulos
    @Test
    @DisplayName("Testando valores nulos")
    void testValoresNulos() {
        assertNull(usuarioNull);
    }

    //testar caracteres especiais nos campos String
    @Test
    @DisplayName("Testando caracteres especiais nos campos String")
    void testCaracteresEspeciais() {
        usuarioCompleto.setNome("François Müller");
        usuarioCompleto.setEmail("FrançoisMüller@email.com");
        usuarioCompleto.setSenha("s#e@n$a*1(2)3");
        assertEquals("François Müller", usuarioCompleto.getNome());
        assertEquals("FrançoisMüller@email.com", usuarioCompleto.getEmail());
        assertEquals("s#e@n$a*1(2)3", usuarioCompleto.getSenha());
    }


}