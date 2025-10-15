package br.upe.controller.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import java.io.IOException;

/**
 * Controller para a tela de listagem de planos (PlansScreen.fxml).
 * Gerencia a adição de novos cards de plano à tela.
 */
public class PlansScreenController extends BaseController {

    @FXML
    private TilePane PlanoTilePane; // O container onde os cards serão adicionados

    @FXML
    private Button adicionarButton; // O botão "+ Adicionar Plano"

    // Um contador simples para dar nomes diferentes aos novos planos
    private int novoPlanoContador = 1;

    /**
     * Método chamado quando o botão "+ Adicionar Plano" é clicado.
     * @param event O evento de clique do botão.
     */
    @FXML
    private void handleAdicionarPlano(ActionEvent event) {
        System.out.println("Botão 'Adicionar Plano' foi pressionado.");

        // 1. Prepara os dados para o novo card
        String nomeDoNovoPlano = "Novo Plano de Treino " + novoPlanoContador;
        novoPlanoContador++; // Incrementa o contador para a próxima vez

        try {
            // 2. Cria o FXMLLoader para carregar nosso "molde" de card
            // ATENÇÃO: O ARQUIVO "PlanoList.fxml" DEVE ESTAR NA MESMA PASTA QUE ESTE CONTROLLER
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PlanoList.fxml"));

            // 3. Carrega o FXML e obtém o Node raiz (o HBox do card)
            HBox cardNode = loader.load();

            // 4. Pega a instância do controller que foi criada para este card específico
            PlanoListController cardController = loader.getController();

            // 5. Usa o método público do controller do card para passar os dados
            cardController.setData(nomeDoNovoPlano);

            // 6. Adiciona o card (já configurado) ao nosso TilePane na tela principal
            PlanoTilePane.getChildren().add(cardNode);

        } catch (IOException e) {
            System.err.println("!! ERRO AO CARREGAR PlanoList.fxml !!");
            System.err.println("Verifique se o arquivo FXML está na pasta correta e sem erros.");
            e.printStackTrace();
            // Usando o método do seu BaseController para mostrar um alerta de erro
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível criar o novo card de plano.");
        }
    }
}