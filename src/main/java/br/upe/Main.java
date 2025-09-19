package br.upe;

import br.upe.ui.MenuPrincipal;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scGlobal = new Scanner(System.in);

        // Exibir menu e iniciar
        MenuPrincipal menuPrincipal = new MenuPrincipal(scGlobal);
        menuPrincipal.exibirMenuInicial();

        scGlobal.close();
        System.out.println("Programa encerrado.");
    }
}