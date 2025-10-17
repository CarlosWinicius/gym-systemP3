package br.upe.controller.ui;

import br.upe.controller.business.IIndicadorBiomedicoService;
import br.upe.controller.business.IndicadorBiomedicoService;
import br.upe.controller.business.RelatorioDiferencaIndicadores;
import br.upe.data.beans.IndicadorBiomedico;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetricasScreenController extends BaseController {

    private static final Logger logger = Logger.getLogger(MetricasScreenController.class.getName());

    private static final String MSG_SESSAO = "Sessão";
    private static final String MSG_PERIODO_REQUERIDO = "Período requerido";
    private static final String CSV_PATTERN = "*.csv";

    // Form de cadastro
    @FXML private DatePicker dataPicker;
    @FXML private TextField pesoField;
    @FXML private TextField alturaField;
    @FXML private TextField gorduraField;
    @FXML private TextField massaMagraField;

    // Filtros
    @FXML private DatePicker inicioPicker;
    @FXML private DatePicker fimPicker;

    // Tabela
    @FXML private TableView<IndicadorBiomedico> indicadoresTable;
    @FXML private TableColumn<IndicadorBiomedico, String> colData;
    @FXML private TableColumn<IndicadorBiomedico, String> colPeso;
    @FXML private TableColumn<IndicadorBiomedico, String> colAltura;
    @FXML private TableColumn<IndicadorBiomedico, String> colGordura;
    @FXML private TableColumn<IndicadorBiomedico, String> colMassaMagra;
    @FXML private TableColumn<IndicadorBiomedico, String> colImc;

    @FXML private SideMenuController sideMenuController;

    private final IIndicadorBiomedicoService indicadorService = new IndicadorBiomedicoService();
    private final ObservableList<IndicadorBiomedico> indicadoresObservable = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Defaults
        if (dataPicker != null) dataPicker.setValue(LocalDate.now());

        configurarTabela();
        carregarTodosIndicadores();
    }

    private void configurarTabela() {
        // Formatters para display
        colData.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getData() != null ? cell.getValue().getData().format(DATE_FORMATTER) : ""));
        colPeso.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.1f", cell.getValue().getPesoKg())));
        colAltura.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.0f", cell.getValue().getAlturaCm())));
        colGordura.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.1f", cell.getValue().getPercentualGordura())));
        colMassaMagra.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.1f", cell.getValue().getPercentualMassaMagra())));
        colImc.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getImc())));

        indicadoresTable.setItems(indicadoresObservable);
    }

    private void carregarTodosIndicadores() {
        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.WARNING, MSG_SESSAO, "Faça login para visualizar suas métricas.");
            return;
        }

        List<IndicadorBiomedico> lista = indicadorService.listarTodosDoUsuario(usuarioLogado.getId())
                .stream()
                .sorted(Comparator.comparing(IndicadorBiomedico::getData))
                .toList(); // cria uma lista nova já ordenada

        indicadoresObservable.setAll(lista);
    }

    @FXML
    private void handleAdicionarIndicador() {
        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.WARNING, MSG_SESSAO, "Faça login para cadastrar um indicador.");
            return;
        }
        try {
            LocalDate data = Optional.ofNullable(dataPicker.getValue()).orElse(LocalDate.now());
            double peso = parseDouble(pesoField.getText(), "Peso");
            double altura = parseDouble(alturaField.getText(), "Altura");
            double gordura = parseDouble(gorduraField.getText(), "Gordura");
            double massa = parseDouble(massaMagraField.getText(), "Massa Magra");

            IndicadorBiomedico novo = indicadorService.cadastrarIndicador(
                    usuarioLogado.getId(), data, peso, altura, gordura, massa);

            showAlert(Alert.AlertType.INFORMATION, "Indicador salvo",
                    String.format("IMC calculado: %.2f", novo.getImc()));

            limparFormularioCadastro();
            carregarTodosIndicadores();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Entrada inválida", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Erro ao salvar", ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Erro inesperado ao cadastrar indicador", ex);
            showAlert(Alert.AlertType.ERROR, "Erro inesperado", ex.getMessage());
        }
    }

    @FXML
    private void handleFiltrar() {
        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.WARNING, MSG_SESSAO, "Faça login para visualizar as métricas.");
            return;
        }
        LocalDate inicio = inicioPicker.getValue();
        LocalDate fim = fimPicker.getValue();
        if (inicio == null || fim == null) {
            showAlert(Alert.AlertType.WARNING, "Filtro incompleto", "Informe as datas de início e fim.");
            return;
        }
        try {
            List<IndicadorBiomedico> lista = indicadorService.gerarRelatorioPorData(usuarioLogado.getId(), inicio, fim);
            lista.sort(Comparator.comparing(IndicadorBiomedico::getData));
            indicadoresObservable.setAll(lista);
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Filtro inválido", ex.getMessage());
        }
    }

    @FXML
    private void handleMostrarTodos() {
        // Limpar filtros e recarregar todos os indicadores
        if (inicioPicker != null) inicioPicker.setValue(null);
        if (fimPicker != null) fimPicker.setValue(null);
        carregarTodosIndicadores();
    }

    @FXML
    private void handleRelatorioDiferenca() {
        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.WARNING, MSG_SESSAO, "Faça login para gerar relatório.");
            return;
        }
        LocalDate inicio = inicioPicker.getValue();
        LocalDate fim = fimPicker.getValue();
        if (inicio == null || fim == null) {
            showAlert(Alert.AlertType.WARNING, MSG_PERIODO_REQUERIDO, "Informe as datas de início e fim para o relatório.");
            return;
        }
        try {
            RelatorioDiferencaIndicadores rel = indicadorService.gerarRelatorioDiferenca(usuarioLogado.getId(), inicio, fim);
            TextArea area = new TextArea(rel.toString());
            area.setEditable(false);
            area.setWrapText(true);
            area.setPrefRowCount(12);
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Relatório de Diferenças");
            dialog.getDialogPane().setContent(area);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Erro no relatório", ex.getMessage());
        }
    }

    @FXML
    private void handleImportarCsv() {
        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.WARNING, MSG_SESSAO, "Faça login para importar.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar arquivo CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos CSV", CSV_PATTERN));
        File file = chooser.showOpenDialog(obterStageAtual());
        if (file != null) {
            try {
                indicadorService.importarIndicadoresCsv(usuarioLogado.getId(), file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Importação concluída", "Arquivo importado com sucesso.");
                carregarTodosIndicadores();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erro na importação", ex.getMessage());
            }
        }
    }

    @FXML
    private void handleExportarLista() {
        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.WARNING, MSG_SESSAO, "Faça login para exportar.");
            return;
        }
        LocalDate inicio = inicioPicker.getValue();
        LocalDate fim = fimPicker.getValue();
        if (inicio == null || fim == null) {
            showAlert(Alert.AlertType.WARNING, MSG_PERIODO_REQUERIDO, "Informe as datas de início e fim para exportar a lista.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salvar Relatório (Lista)");
        chooser.setInitialFileName(String.format("metricas_%s_a_%s.csv", inicio, fim));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", CSV_PATTERN));
        File file = chooser.showSaveDialog(obterStageAtual());
        if (file != null) {
            try {
                // Usa o helper de exportação já presente no service
                if (indicadorService instanceof IndicadorBiomedicoService impl) {
                    impl.exportarRelatorioPorDataParaCsv(usuarioLogado.getId(), inicio, fim, file.getAbsolutePath());
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Exportação", "Recurso de exportação indisponível neste momento.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Falha na exportação", ex.getMessage());
            }
        }
    }

    @FXML
    private void handleExportarDiferenca() {
        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.WARNING, MSG_SESSAO, "Faça login para exportar.");
            return;
        }
        LocalDate inicio = inicioPicker.getValue();
        LocalDate fim = fimPicker.getValue();
        if (inicio == null || fim == null) {
            showAlert(Alert.AlertType.WARNING, MSG_PERIODO_REQUERIDO, "Informe as datas de início e fim para exportar a diferença.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salvar Relatório (Diferença)");
        chooser.setInitialFileName(String.format("diferencas_%s_a_%s.csv", inicio, fim));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", CSV_PATTERN));
        File file = chooser.showSaveDialog(obterStageAtual());
        if (file != null) {
            try {
                RelatorioDiferencaIndicadores rel = indicadorService.gerarRelatorioDiferenca(usuarioLogado.getId(), inicio, fim);
                rel.calcularDiferencas();
                rel.exportarParaCsv(file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Exportação concluída", "Relatório exportado com sucesso.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Falha na exportação", ex.getMessage());
            }
        }
    }

    private void limparFormularioCadastro() {
        if (dataPicker != null) dataPicker.setValue(LocalDate.now());
        if (pesoField != null) pesoField.clear();
        if (alturaField != null) alturaField.clear();
        if (gorduraField != null) gorduraField.clear();
        if (massaMagraField != null) massaMagraField.clear();
    }

    private double parseDouble(String value, String campo) throws NumberFormatException {
        try {
            return Double.parseDouble(Optional.ofNullable(value).orElse("").trim());
        } catch (Exception ex) {
            throw new NumberFormatException("Valor inválido para '" + campo + "'.");
        }
    }

    private javafx.stage.Window obterStageAtual() {
        return indicadoresTable != null && indicadoresTable.getScene() != null ? indicadoresTable.getScene().getWindow() : null;
    }
}
