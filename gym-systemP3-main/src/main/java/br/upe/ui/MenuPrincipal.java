package br.upe.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.beans.Usuario;
import br.upe.data.TipoUsuario;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner sc;
    private final IUsuarioService usuarioService;
    private Usuario usuarioLogado;

    public MenuPrincipal(Scanner scanner) {
        this.sc = scanner;
        this.usuarioService = new UsuarioService();
    }

    public void exibirMenuInicial() {
        int opcao;
        do {
            System.out.println("\n================================");
            System.out.println("      BEM-VINDO AO SYSFIT");
            System.out.println("================================");
            System.out.println("1. Entrar");
            System.out.println("2. Cadastrar-se");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    MenuLogin menuLogin = new MenuLogin(sc);
                    usuarioLogado = menuLogin.exibirLogin();
                    if (usuarioLogado != null) {
                        exibirMenuAposLogin();
                    }
                    break;
                case 2:
                    MenuCadastroUsuario menuCadastro = new MenuCadastroUsuario(sc);
                    menuCadastro.exibirCadastro();
                    break;
                case 3:
                    System.out.println("Saindo do SysFit. Até mais!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 3);
    }

    private void exibirMenuAposLogin() {
        if (usuarioLogado == null) return;

        if (usuarioLogado.getTipo() == TipoUsuario.ADMIN) {
            exibirMenuAdmin();
        } else {
            exibirMenuComum();
        }
    }

    private void exibirMenuAdmin() {
        MenuAdministrador menuAdmin = new MenuAdministrador(sc);
        menuAdmin.exibirMenu();
    }

    private void exibirMenuComum() {
        int opcao;
        do {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("Olá, " + usuarioLogado.getNome() + "!");
            System.out.println("1. Meus Treinos");
            System.out.println("2. Meus Indicadores");
            System.out.println("3. Gerenciar Planos de Treino");
            System.out.println("4. Gerenciar Exercícios");
            System.out.println("5. Exportar Relatórios");
            System.out.println("0. Sair da Conta");
            System.out.print("\nEscolha uma opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    MenuTreinos menuTreinos = new MenuTreinos(sc, usuarioLogado.getId());
                    menuTreinos.exibirMenu();
                    break;
                case 2:
                    MenuIndicadores menuIndicadores = new MenuIndicadores(sc, usuarioLogado.getId());
                    menuIndicadores.exibirMenu();
                    break;
                case 3:
                    MenuPlanoTreino menuPlanoTreino = new MenuPlanoTreino(usuarioLogado.getId());
                    menuPlanoTreino.exibirMenu();
                    break;
                case 4:
                    MenuExercicios menuExercicios = new MenuExercicios(sc, usuarioLogado.getId());
                    menuExercicios.exibirMenu();
                    break;
                case 5:
                    MenuRelatorios menuRelatorios = new MenuRelatorios(sc, usuarioLogado.getId());
                    menuRelatorios.exibirMenu();
                    break;
                case 0:
                    System.out.println("Deslogando...");
                    usuarioLogado = null;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }
}
