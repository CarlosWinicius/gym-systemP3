package br.upe.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.beans.Usuario;

import java.util.List;
import java.util.Scanner;

public class MenuAdministrador {
    private static final String INVALID_ID_MESSAGE = "ID inválido. Por favor, digite um número.";
    private static final String ERROR_PREFIX = "Erro: ";
    private final IUsuarioService usuarioService;
    private final Scanner scanner;

    public MenuAdministrador(Scanner scanner) {
        this.usuarioService = new UsuarioService();
        this.scanner = scanner;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n================================");
            System.out.println("      MENU ADMINISTRADOR");
            System.out.println("================================");
            System.out.println("1. Listar Usuários");
            System.out.println("2. Promover Usuário a Admin");
            System.out.println("3. Rebaixar Usuário a Comum");
            System.out.println("4. Remover Usuário");
            System.out.println("0. Voltar");
            System.out.print("\nEscolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    listarUsuarios();
                    break;
                case 2:
                    promoverUsuario();
                    break;
                case 3:
                    rebaixarUsuario();
                    break;
                case 4:
                    removerUsuario();
                    break;
                case 0:
                    System.out.println("Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }

    private void listarUsuarios() {
        System.out.println("\n===== LISTA DE TODOS OS USUÁRIOS =====");
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
        } else {
            usuarios.forEach(System.out::println);
        }
    }

    private void promoverUsuario() {
        System.out.println("\n===== PROMOVER USUÁRIO A ADMIN =====");
        System.out.print("Digite o ID do usuário para promover: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
            usuarioService.promoverUsuarioAAdmin(id);
        } catch (NumberFormatException e) {
            System.err.println(INVALID_ID_MESSAGE);
        } catch (IllegalArgumentException e) {
            System.err.println(ERROR_PREFIX + e.getMessage());
        }
    }

    private void rebaixarUsuario() {
        System.out.println("\n===== REBAIXAR USUÁRIO A COMUM =====");
        System.out.print("Digite o ID do usuário para rebaixar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
            usuarioService.rebaixarUsuarioAComum(id);
        } catch (NumberFormatException e) {
            System.err.println(INVALID_ID_MESSAGE);
        } catch (IllegalArgumentException e) {
            System.err.println(ERROR_PREFIX + e.getMessage());
        }
    }

    private void removerUsuario() {
        System.out.println("\n===== REMOVER USUÁRIO =====");
        System.out.print("Digite o ID do usuário para remover: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
            usuarioService.removerUsuario(id);
            System.out.println("Usuário removido com sucesso!");
        } catch (NumberFormatException e) {
            System.err.println(INVALID_ID_MESSAGE);
        } catch (IllegalArgumentException e) {
            System.err.println(ERROR_PREFIX + e.getMessage());
        }
    }
}
