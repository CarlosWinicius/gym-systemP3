package br.upe.controller.business;

import br.upe.data.entities.IndicadorBiomedico; // Ajustado para entities para compatibilidade
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class RelatorioDiferencaIndicadores {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Optional<IndicadorBiomedico> indicadorInicial = Optional.empty();
    private Optional<IndicadorBiomedico> indicadorFinal = Optional.empty();

    private double diferencaPeso;
    private double diferencaPercentualGordura;
    private double diferencaPercentualMassaMagra;
    private double diferencaImc;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Getters e setters
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public Optional<IndicadorBiomedico> getIndicadorInicial() {
        return indicadorInicial;
    }

    public void setIndicadorInicial(Optional<IndicadorBiomedico> indicadorInicial) {
        this.indicadorInicial = indicadorInicial;
    }

    public Optional<IndicadorBiomedico> getIndicadorFinal() {
        return indicadorFinal;
    }

    public void setIndicadorFinal(Optional<IndicadorBiomedico> indicadorFinal) {
        this.indicadorFinal = indicadorFinal;
    }

    public double getDiferencaPeso() {
        return diferencaPeso;
    }

    public double getDiferencaPercentualGordura() {
        return diferencaPercentualGordura;
    }

    public double getDiferencaPercentualMassaMagra() {
        return diferencaPercentualMassaMagra;
    }

    public double getDiferencaImc() {
        return diferencaImc;
    }

    public void calcularDiferencas() {
        if (indicadorInicial.isPresent() && indicadorFinal.isPresent()) {
            IndicadorBiomedico inicial = indicadorInicial.get();
            IndicadorBiomedico finalObj = indicadorFinal.get();

            this.diferencaPeso = finalObj.getPesoKg() - inicial.getPesoKg();
            this.diferencaPercentualGordura = finalObj.getPercentualGordura() - inicial.getPercentualGordura();
            this.diferencaPercentualMassaMagra = finalObj.getPercentualMassaMagra() - inicial.getPercentualMassaMagra();
            this.diferencaImc = finalObj.getImc() - inicial.getImc();
        }
    }

    @Override
    public String toString() {
        if (!indicadorInicial.isPresent() || !indicadorFinal.isPresent()) {
            String inicioStr = (dataInicio != null) ? dataInicio.format(DATE_FORMATTER) : "N/A";
            String fimStr = (dataFim != null) ? dataFim.format(DATE_FORMATTER) : "N/A";
            return String.format("Relatório de Evolução (%s a %s)%nNenhum dado encontrado no período.", inicioStr, fimStr);
        }

        IndicadorBiomedico inicial = indicadorInicial.get();
        IndicadorBiomedico finalObj = indicadorFinal.get();

        return String.format(
                """
                --- Relatório de Evolução: %s a %s ---%n\
                | Indicador              | %-15s | %-15s | %-17s |%n\
                |------------------------|-----------------|-----------------|-------------------|%n\
                | Peso (kg)              | %-15.1f | %-15.1f | %+-17.1f |%n\
                | Gordura (%%)            | %-15.1f | %-15.1f | %+-17.1f |%n\
                | Massa Magra (%%)       | %-15.1f | %-15.1f | %+-17.1f |%n\
                | IMC                    | %-15.2f | %-15.2f | %+-17.2f |%n\
                -----------------------------------------------------------------------------------\
                """,
                dataInicio.format(DATE_FORMATTER), dataFim.format(DATE_FORMATTER),
                "Inicial", "Final", "Diferença",
                inicial.getPesoKg(), finalObj.getPesoKg(), diferencaPeso,
                inicial.getPercentualGordura(), finalObj.getPercentualGordura(), diferencaPercentualGordura,
                inicial.getPercentualMassaMagra(), finalObj.getPercentualMassaMagra(), diferencaPercentualMassaMagra,
                inicial.getImc(), finalObj.getImc(), diferencaImc
        );
    }

    public void exportarParaCsv(String caminhoArquivo) throws IOException {
        if (indicadorInicial.isEmpty() || indicadorFinal.isEmpty()) {
            throw new IllegalStateException("Indicadores inicial ou final não estão presentes para exportar.");
        }
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            writer.append("Indicador,Inicial,Final,Diferença\n");
            writer.append(String.format("Peso (kg),%.1f,%.1f,%+.1f%n",
                    indicadorInicial.get().getPesoKg(),
                    indicadorFinal.get().getPesoKg(),
                    diferencaPeso));

            writer.append(String.format("Gordura (%%),%.1f,%.1f,%+.1f%n",
                    indicadorInicial.get().getPercentualGordura(),
                    indicadorFinal.get().getPercentualGordura(),
                    diferencaPercentualGordura));

            writer.append(String.format("Massa Magra (%%),%.1f,%.1f,%+.1f%n",
                    indicadorInicial.get().getPercentualMassaMagra(),
                    indicadorFinal.get().getPercentualMassaMagra(),
                    diferencaPercentualMassaMagra));

            writer.append(String.format("IMC,%.2f,%.2f,%+.2f%n",
                    indicadorInicial.get().getImc(),
                    indicadorFinal.get().getImc(),
                    diferencaImc));
        }
    }
}