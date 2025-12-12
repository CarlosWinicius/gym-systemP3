package br.upe.controller.ui;

import br.upe.controller.business.RelatorioDiferencaIndicadores;
import br.upe.data.beans.IndicadorBiomedico;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class RelatorioDiferencasDialogController {

    @FXML private TableView<DiffRow> diffTable;
    @FXML private TableColumn<DiffRow, String> colIndicador;
    @FXML private TableColumn<DiffRow, String> colInicial;
    @FXML private TableColumn<DiffRow, String> colFinal;
    @FXML private TableColumn<DiffRow, String> colDiferenca;

    @FXML private LineChart<String, Number> evolutionChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String FP_1 = "%.1f";
    private static final String FP_PM1 = "%+.1f";
    private static final String FP_2 = "%.2f";
    private static final String FP_PM2 = "%+.2f";

    @FXML
    private void initialize() {
        // Configura colunas da tabela
        colIndicador.setCellValueFactory(new PropertyValueFactory<>("indicador"));
        colInicial.setCellValueFactory(new PropertyValueFactory<>("inicial"));
        colFinal.setCellValueFactory(new PropertyValueFactory<>("fim"));
        colDiferenca.setCellValueFactory(new PropertyValueFactory<>("diferenca"));

        evolutionChart.setAnimated(false);
        evolutionChart.setCreateSymbols(true);
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
    }

    public void setDados(RelatorioDiferencaIndicadores rel, List<IndicadorBiomedico> evolucao) {
        preencherTabela(rel);
        preencherGrafico(evolucao);
        habilitarAlternanciaLegenda();
    }

    private void preencherTabela(RelatorioDiferencaIndicadores rel) {
        ObservableList<DiffRow> rows = FXCollections.observableArrayList();
        Optional<IndicadorBiomedico> iniOpt = rel.getIndicadorInicial();
        Optional<IndicadorBiomedico> fimOpt = rel.getIndicadorFinal();

        if (iniOpt.isEmpty() || fimOpt.isEmpty()) {
            diffTable.setItems(rows);
            return;
        }

        IndicadorBiomedico ini = iniOpt.get();
        IndicadorBiomedico fim = fimOpt.get();

        rows.add(new DiffRow(
                "Peso (kg)",
                String.format(Locale.getDefault(), FP_1, ini.getPesoKg()),
                String.format(Locale.getDefault(), FP_1, fim.getPesoKg()),
                String.format(Locale.getDefault(), FP_PM1, fim.getPesoKg() - ini.getPesoKg())
        ));
        rows.add(new DiffRow(
                "Gordura (%)",
                String.format(Locale.getDefault(), FP_1, ini.getPercentualGordura()),
                String.format(Locale.getDefault(), FP_1, fim.getPercentualGordura()),
                String.format(Locale.getDefault(), FP_PM1, fim.getPercentualGordura() - ini.getPercentualGordura())
        ));
        rows.add(new DiffRow(
                "Massa Magra (%)",
                String.format(Locale.getDefault(), FP_1, ini.getPercentualMassaMagra()),
                String.format(Locale.getDefault(), FP_1, fim.getPercentualMassaMagra()),
                String.format(Locale.getDefault(), FP_PM1, fim.getPercentualMassaMagra() - ini.getPercentualMassaMagra())
        ));
        rows.add(new DiffRow(
                "IMC",
                String.format(Locale.getDefault(), FP_2, ini.getImc()),
                String.format(Locale.getDefault(), FP_2, fim.getImc()),
                String.format(Locale.getDefault(), FP_PM2, fim.getImc() - ini.getImc())
        ));

        diffTable.setItems(rows);
    }

    private void preencherGrafico(List<IndicadorBiomedico> evolucao) {
        evolutionChart.getData().clear();

        XYChart.Series<String, Number> seriePeso = new XYChart.Series<>();
        seriePeso.setName("Peso (kg)");
        XYChart.Series<String, Number> serieGordura = new XYChart.Series<>();
        serieGordura.setName("Gordura (%)");
        XYChart.Series<String, Number> serieMassa = new XYChart.Series<>();
        serieMassa.setName("Massa Magra (%)");
        XYChart.Series<String, Number> serieImc = new XYChart.Series<>();
        serieImc.setName("IMC");

        for (IndicadorBiomedico ind : evolucao) {
            String data = ind.getData() != null ? ind.getData().format(DATE_FMT) : "";
            seriePeso.getData().add(new XYChart.Data<>(data, ind.getPesoKg()));
            serieGordura.getData().add(new XYChart.Data<>(data, ind.getPercentualGordura()));
            serieMassa.getData().add(new XYChart.Data<>(data, ind.getPercentualMassaMagra()));
            serieImc.getData().add(new XYChart.Data<>(data, ind.getImc()));
        }

        // Adiciona as séries individualmente para evitar varargs não verificados
        evolutionChart.getData().add(seriePeso);
        evolutionChart.getData().add(serieGordura);
        evolutionChart.getData().add(serieMassa);
        evolutionChart.getData().add(serieImc);
    }

    private void habilitarAlternanciaLegenda() {
        // Aguarda a criação dos nós das séries e da legenda
        Platform.runLater(() -> {
            Node legendNode = evolutionChart.lookup(".chart-legend");
            if (!(legendNode instanceof Pane legendPane)) {
                return;
            }
            // Cada item da legenda possui estilo ".chart-legend-item" e é um Label
            Set<Node> items = legendPane.lookupAll(".chart-legend-item");
            for (Node n : items) {
                if (!(n instanceof Label label)) continue;
                String text = label.getText();
                XYChart.Series<String, Number> series = evolutionChart.getData().stream()
                        .filter(s -> s.getName() != null && s.getName().equals(text))
                        .findFirst()
                        .orElse(null);
                if (series == null) continue;

                // Cursor e handlers
                label.setCursor(Cursor.HAND);
                label.setOnMouseClicked(e -> toggleSeriesVisibility(series, label));

                // Também permite clicar no símbolo da legenda, se existir
                Node symbol = label.getGraphic();
                if (symbol != null) {
                    symbol.setCursor(Cursor.HAND);
                    symbol.setOnMouseClicked(e -> toggleSeriesVisibility(series, label));
                }
            }
        });
    }

    private void toggleSeriesVisibility(XYChart.Series<String, Number> series, Label legendLabel) {
        boolean visible = series.getNode() == null || series.getNode().isVisible();
        boolean newVisible = !visible;
        if (series.getNode() != null) {
            series.getNode().setVisible(newVisible);
        }
        // Alterna visibilidade dos símbolos de dados
        for (XYChart.Data<String, Number> d : series.getData()) {
            Node n = d.getNode();
            if (n != null) n.setVisible(newVisible);
        }
        // Feedback visual: opacidade do gráfico da legenda ou do próprio label
        Node symbol = legendLabel.getGraphic();
        if (symbol != null) symbol.setOpacity(newVisible ? 1.0 : 0.25);
        legendLabel.setOpacity(newVisible ? 1.0 : 0.7);
    }

    public static class DiffRow {
        private final String indicador;
        private final String inicial;
        private final String fim;
        private final String diferenca;

        public DiffRow(String indicador, String inicial, String fim, String diferenca) {
            this.indicador = indicador;
            this.inicial = inicial;
            this.fim = fim;
            this.diferenca = diferenca;
        }

        public String getIndicador() { return indicador; }
        public String getInicial() { return inicial; }
        public String getFim() { return fim; }
        public String getDiferenca() { return diferenca; }
    }
}

