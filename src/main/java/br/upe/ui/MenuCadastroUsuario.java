package br.upe.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.TipoUsuario;

import java.util.Scanner;

public class MenuCadastroUsuario {
    private final IUsuarioService usuarioService;
    private final Scanner scanner;

    public MenuCadastroUsuario(Scanner scanner) {
        this.usuarioService = new UsuarioService();
        this.scanner = scanner;
    }

    public void exibirCadastro() {
        System.out.println("\n================================");
        System.out.println("      CADASTRO DE USUÁRIO");
        System.out.println("================================");
        System.out.print("Nome Completo: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            usuarioService.cadastrarUsuario(nome, email, senha, TipoUsuario.COMUM);
            System.out.println("Usuário cadastrado com sucesso! Faça login para acessar.");
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao cadastrar: " + e.getMessage());
        }
    }
}