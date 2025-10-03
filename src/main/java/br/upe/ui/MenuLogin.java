package br.upe.ui;

import br.upe.business.IUsuarioService;
import br.upe.business.UsuarioService;
import br.upe.data.beans.Usuario;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuLogin {

    private static final Logger logger = LoggerFactory.getLogger(MenuLogin.class);

    private final IUsuarioService usuarioService;
    private final Scanner scanner;

    public MenuLogin(Scanner scanner) {
        this.usuarioService = new UsuarioService();
        this.scanner = scanner;
    }

    public Usuario exibirLogin() {
        System.out.println("\n================================");
        System.out.println("           LOGIN SYSFIT");
        System.out.println("================================");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            Usuario usuarioLogado = usuarioService.autenticarUsuario(email, senha);
            if (usuarioLogado != null) {
                logger.info("Login bem-sucedido! Bem-vindo(a), {}!", usuarioLogado.getNome());
                return usuarioLogado;
            } else {
                logger.warn("Credenciais inválidas para o e-mail '{}'.", email);
                return null;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Erro de login para o e-mail '{}': {}", email, e.getMessage(), e);
            return null;
        }
    }
}