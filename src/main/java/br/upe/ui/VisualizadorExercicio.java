package br.upe.ui;

import br.upe.data.beans.Exercicio;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class VisualizadorExercicio {

    // Metodo para visualização de exercicios
    public static void mostrarExercicio(String gifPath, String descricao) {

        // Cria uma janela com o gif
        JFrame frame = new JFrame("Detalhes do Exercício");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);

        // Carrega o gif
        ImageIcon gifIcon = null;

        try {
            gifIcon = new ImageIcon(Objects.requireNonNull(VisualizadorExercicio.class.getResource("/" + gifPath)));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar GIF: " + gifPath + "\n" + e.getMessage(), "Erro de Imagem", JOptionPane.ERROR_MESSAGE);
            gifIcon = new ImageIcon(new byte[0]);
        }

        // Cria os componentes gráficos
        JLabel gifLabel = new JLabel(gifIcon);
        JTextArea descricaoArea = new JTextArea(descricao);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        descricaoArea.setEditable(false);

        frame.setLayout(new BorderLayout());
        frame.add(gifLabel, BorderLayout.CENTER);
        frame.add(descricaoArea, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Metodo para exibir o exercicio
    public void exibirDetalhes(Exercicio exercicio) {
        mostrarExercicio(exercicio.getCaminhoGif(), exercicio.getDescricao());
    }
}