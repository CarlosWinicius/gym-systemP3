package br.upe.data.beans;

import br.upe.data.dao.UsuarioDAO;
import br.upe.data.entity.Usuario;
import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTeste {

    @Test
    public void deveCriarUsuarioComSucesso() {
        // 1. Prepara
        UsuarioDAO dao = new UsuarioDAO();
        Usuario novoUser = new Usuario("Teste JUnit", "teste.junit@email.com", "123", TipoUsuario.COMUM);

        // 2. Executa
        Usuario salvo = dao.salvar(novoUser);

        // 3. Valida (Sem System.out.println, usamos Assert)
        assertNotNull(salvo.getId(), "O ID não deveria ser nulo após salvar");
        assertEquals("Teste JUnit", salvo.getNome());
    }
}