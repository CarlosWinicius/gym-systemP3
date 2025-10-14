package br.upe.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.beans.Usuario;

import java.util.Scanner;

public class MenuLogin {
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
                System.out.println("Login bem-sucedido! Bem-vindo(a), " + usuarioLogado.getNome() + "!");
                return usuarioLogado;
            } else {
                System.out.println("Credenciais inválidas. Tente novamente.");
                return null;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de login: " + e.getMessage());
            return null;
        }
    }
}