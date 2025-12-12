package br.upe.controller.ui;

import br.upe.controller.business.CalculadoraIMC;
import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.IndicadorBiomedicoService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.entities.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class PerfilEditController extends BaseController {

    @FXML private TextField nome;
    @FXML private TextField altura;
    @FXML private TextField peso;
    @FXML private TextField percentualGordura;
    @FXML private TextField percentualMM;
    @FXML private TextField imc;
    @FXML private Label categoriaIMC;
    @FXML private ImageView foto;

    private byte[] fotoBytesSelecionada;

    private final IUsuarioService usuarioService = new UsuarioService();
    private final IndicadorBiomedicoService indicadorService = new IndicadorBiomedicoService();

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        carregarDadosNaTela();
    }

    @FXML
    protected void initialize() {
        imc.setEditable(false);
    }

    private void carregarDadosNaTela() {
        if (usuarioLogado == null) return;

        nome.setEditable(true);
        altura.setEditable(true);
        peso.setEditable(true);
        percentualGordura.setEditable(true);
        percentualMM.setEditable(true);
        imc.setEditable(false);

        nome.setText(usuarioLogado.getNome());

        if (usuarioLogado.getFotoPerfil() != null) {
            foto.setImage(new Image(new ByteArrayInputStream(usuarioLogado.getFotoPerfil())));
        } else {
            foto.setImage(null);
        }

        try {
            var lista = indicadorService.listarTodosDoUsuario(usuarioLogado.getId());
            if (!lista.isEmpty()) {
                var ultimo = lista.get(lista.size() - 1);
                altura.setText(String.valueOf(ultimo.getAlturaCm()));
                peso.setText(String.valueOf(ultimo.getPesoKg()));
                percentualGordura.setText(String.valueOf(ultimo.getPercentualGordura()));
                percentualMM.setText(String.valueOf(ultimo.getPercentualMassaMagra()));
                imc.setText(String.format("%.2f", ultimo.getImc()));
                categoriaIMC.setText(CalculadoraIMC.classificarImc(ultimo.getImc()));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar dados na tela de edição", e);
        }
        if (usuarioLogado != null && usuarioLogado.getFotoPerfil() != null) {
            foto.setImage(new Image(new ByteArrayInputStream(usuarioLogado.getFotoPerfil())));
        }



    }

    @FXML
    private void escolherImagem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;

        try {
            long fileSize = file.length();
            if (fileSize > 5L * 1024 * 1024) {
                throw new IllegalArgumentException("Imagem muito grande. Máximo permitido: 5MB.");
            }

            fotoBytesSelecionada = java.nio.file.Files.readAllBytes(file.toPath());
            foto.setImage(new Image(file.toURI().toString()));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao escolher imagem", e);
            new Alert(Alert.AlertType.ERROR, "Erro ao escolher imagem: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void salvarDados() {
        try {
            String novoNome = nome.getText().trim();
            if (novoNome.isEmpty()) throw new IllegalArgumentException("O nome não pode estar vazio.");

            double alturaValor = Double.parseDouble(altura.getText());
            double pesoValor = Double.parseDouble(peso.getText());
            double percentualGorduraValor = Double.parseDouble(percentualGordura.getText());
            double percentualMMValor = Double.parseDouble(percentualMM.getText());

            double resultadoIMC = CalculadoraIMC.calcular(pesoValor, alturaValor);
            imc.setText(String.format("%.2f", resultadoIMC));
            categoriaIMC.setText(CalculadoraIMC.classificarImc(resultadoIMC));

            // 1) Atualiza usuário
            usuarioService.atualizarUsuario(
                    usuarioLogado.getId(),
                    novoNome,
                    usuarioLogado.getEmail(),
                    usuarioLogado.getSenha(),
                    usuarioLogado.getTipo()
            );

            // 2) Salva indicadores
            indicadorService.cadastrarIndicador(
                    usuarioLogado.getId(),
                    java.time.LocalDate.now(),
                    pesoValor,
                    alturaValor,
                    percentualGorduraValor,
                    percentualMMValor
            );

            // 3) Se escolheu foto, salva foto
            if (fotoBytesSelecionada != null) {
                usuarioService.atualizarFoto(usuarioLogado.getId(), fotoBytesSelecionada);
                usuarioLogado.setFotoPerfil(fotoBytesSelecionada);
            }

            // Atualiza objeto em memória para refletir na volta
            usuarioLogado.setNome(novoNome);


            new Alert(Alert.AlertType.INFORMATION, "Dados salvos com sucesso!").showAndWait();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Verifique os campos numéricos (altura, peso, percentuais).").showAndWait();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar dados", e);
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar: " + e.getMessage()).showAndWait();
        }

        BaseController.usuarioLogado = usuarioService.buscarUsuarioPorId(usuarioLogado.getId())
                .orElse(usuarioLogado);



    }


    @FXML
    private void voltarTela(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PerfilScreen.fxml"));
            Parent root = loader.load();

            PerfilController controller = loader.getController();

            // ✅ recarrega do banco para garantir que vem com foto e dados atualizados
            Usuario usuarioAtualizado = usuarioService.buscarUsuarioPorId(usuarioLogado.getId())
                    .orElse(usuarioLogado);

            controller.setUsuarioLogado(usuarioAtualizado);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao voltar para tela de perfil", e);
        }
    }

}