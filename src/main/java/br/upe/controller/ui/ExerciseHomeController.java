package br.upe.controller.ui;

import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class ExerciseHomeController {

    @FXML private ImageView exerciseImage;
    @FXML private Label exerciseName;
    @FXML private Label exerciseName1;
    @FXML private TextField cargaField;
    @FXML private TextField repsField;

    @FXML private Button btnSalvar;  // Botão "Próximo" / "Finalizar"
    @FXML private Button btnRemover; // Botão "Anterior"

    private Stage dialogStage;

    // Serviço para salvar no banco
    private final IPlanoTreinoService planoService = new PlanoTreinoService();

    // Controle da lista e estado
    private List<ItemPlanoTreino> listaExercicios;
    private int indiceAtual = 0;

    // Variáveis para guardar os valores originais do item atual (para comparação)
    private int cargaOriginal;
    private int repsOriginal;

    @FXML
    public void initialize() {
        repsField.setEditable(false);
        // Filtro de números
        cargaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cargaField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setItensDoPlano(List<ItemPlanoTreino> itens) {
        this.listaExercicios = itens;
        this.indiceAtual = 0;

        if (listaExercicios != null && !listaExercicios.isEmpty()) {
            carregarExercicioAtual();
        }
    }

    private void carregarExercicioAtual() {
        ItemPlanoTreino itemAtual = listaExercicios.get(indiceAtual);
        Exercicio exercicio = itemAtual.getExercicio();

        // 1. Guarda os valores originais do banco/memória para comparar depois
        this.cargaOriginal = itemAtual.getCargaKg();
        this.repsOriginal = itemAtual.getRepeticoes();

        // 2. Preenche a tela
        exerciseName.setText(exercicio.getNome());
        cargaField.setText(String.valueOf(this.cargaOriginal));
        repsField.setText(String.valueOf(this.repsOriginal));
        exerciseName1.setText("Exercício " + (indiceAtual + 1) + " / " + listaExercicios.size());

        carregarImagem(exercicio);
        configurarBotoes();
    }

    private void configurarBotoes() {
        // Anterior
        btnRemover.setVisible(indiceAtual > 0);

        // Próximo / Finalizar
        if (indiceAtual == listaExercicios.size() - 1) {
            btnSalvar.setText("Finalizar");
            // MUDANÇA: Cor #8A806F conforme solicitado
            btnSalvar.setStyle("-fx-background-color: #8A806F; -fx-text-fill: white; -fx-cursor: hand; -fx-border-radius: 5; -fx-font-weight: bold;");
        } else {
            btnSalvar.setText("Próximo");
            btnSalvar.setStyle("-fx-background-color: #8A806F; -fx-text-fill: white; -fx-cursor: hand; -fx-border-radius: 5; -fx-font-weight: bold;");
        }
    }

    /**
     * Verifica se houve mudança na carga ou repetições.
     * Se houve, abre o Pop-up.
     * Retorna TRUE se pode prosseguir (navegar), FALSE se o usuário cancelou.
     */
    private boolean verificarSalvarAlteracoes() {
        try {
            int cargaAtual = Integer.parseInt(cargaField.getText());
            int repsAtual = Integer.parseInt(repsField.getText());

            // Se nada mudou, segue a vida
            if (cargaAtual == cargaOriginal && repsAtual == repsOriginal) {
                return true;
            }

            // Se mudou, mostra o Pop-up
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Alterações Detectadas");
            alert.setHeaderText("Você alterou a carga ou repetições.");
            alert.setContentText("Deseja salvar essas alterações no seu plano permanentemente?");

            ButtonType btnSim = new ButtonType("Sim");
            ButtonType btnNao = new ButtonType("Não");

            // Remove botões padrão e adiciona os nossos
            alert.getButtonTypes().setAll(btnSim, btnNao);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == btnSim) {
                // --- USUÁRIO CLICOU EM SIM ---
                // 1. Atualiza o objeto na memória
                ItemPlanoTreino item = listaExercicios.get(indiceAtual);
                item.setCargaKg(cargaAtual);
                item.setRepeticoes(repsAtual);

                // 2. Salva no Banco de Dados
                try {
                    // ATENÇÃO: Certifique-se de ter criado o método 'atualizarItemTreino' no Service conforme instrução anterior
                    // Se o método no service tiver outro nome, ajuste aqui.
                    // Exemplo de cast se o método não estiver na interface:
                    if (planoService instanceof PlanoTreinoService) {
                        ((PlanoTreinoService) planoService).atualizarItemTreino(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Log de erro
                }
                return true; // Pode navegar

            } else if (result.isPresent() && result.get() == btnNao) {
                // --- USUÁRIO CLICOU EM NÃO ---
                // Descartar é só não salvar. A tela vai recarregar o próximo item,
                // e se ele voltar, vai carregar do objeto original (que não mexemos)
                return true; // Pode navegar
            } else {
                // Fechou a janela ou cancelou
                return false; // Não navega
            }

        } catch (NumberFormatException e) {
            // Se tiver erro de número, restaura o original e segue
            return true;
        }
    }

    // --- AÇÕES DE NAVEGAÇÃO ---

    @FXML
    private void handleProximo() {
        // Só avança se o usuário confirmou (Sim ou Não) ou se não houve mudança
        if (verificarSalvarAlteracoes()) {
            if (indiceAtual < listaExercicios.size() - 1) {
                indiceAtual++;
                carregarExercicioAtual();
            } else {
                dialogStage.close();
            }
        }
    }

    @FXML
    private void handleAnterior() {
        // Só volta se verificar alterações
        if (verificarSalvarAlteracoes()) {
            if (indiceAtual > 0) {
                indiceAtual--;
                carregarExercicioAtual();
            }
        }
    }

    // --- MÉTODOS AUXILIARES E DE IMAGEM ---

    private void carregarImagem(Exercicio exercicio) {
        try {
            String caminho = exercicio.getCaminhoGif();
            if (caminho != null && !caminho.isBlank()) {
                String fullPath = "/gif/" + new File(caminho).getName();
                var url = getClass().getResource(fullPath);
                if (url != null) {
                    exerciseImage.setImage(new Image(url.toString()));
                } else {
                    imagemPadrao();
                }
            } else {
                imagemPadrao();
            }
        } catch (Exception e) {
            imagemPadrao();
        }
    }

    private void imagemPadrao() {
        try {
            exerciseImage.setImage(new Image(getClass().getResourceAsStream("/images/no image.png")));
        } catch (Exception ignored) {}
    }

    @FXML
    private void incrementReps() {
        try {
            int val = Integer.parseInt(repsField.getText());
            repsField.setText(String.valueOf(val + 1));
        } catch (NumberFormatException e) {
            repsField.setText("1");
        }
    }

    @FXML
    private void decrementReps() {
        try {
            int val = Integer.parseInt(repsField.getText());
            if (val > 0) repsField.setText(String.valueOf(val - 1));
        } catch (NumberFormatException e) {
            repsField.setText("0");
        }
    }
}