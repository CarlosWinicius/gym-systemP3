package br.upe.business;

import br.upe.data.beans.IndicadorBiomedico;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class RelatorioDiferencaIndicadores {

    public LocalDate dataInicio;
    public LocalDate dataFim;
    public Optional<IndicadorBiomedico> indicadorInicial = Optional.empty();
    public Optional<IndicadorBiomedico> indicadorFinal = Optional.empty();

    public double diferencaPeso;
    public double diferencaPercentualGordura;
    public double diferencaPercentualMassaMagra;
    public double diferencaImc;

    // Define the formatter here
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            return String.format("Relatório de Evolução (%s a %s)%nNenhum dado encontrado no período.",
                                 dataInicio.format(DATE_FORMATTER), dataFim.format(DATE_FORMATTER)); // Use formatter here
        }

        IndicadorBiomedico inicial = indicadorInicial.get();
        IndicadorBiomedico finalObj = indicadorFinal.get();

        return String.format(
            "--- Relatório de Evolução: %s a %s ---%n" +
            "| Indicador              | %-15s | %-15s | %-17s |%n" + // Increased width
            "|------------------------|-----------------|-----------------|-------------------|%n" +
            "| Peso (kg)              | %-15.1f | %-15.1f | %+-17.1f |%n" + // Increased width
            "| Gordura (%%)            | %-15.1f | %-15.1f | %+-17.1f |%n" + // Increased width
            "| Massa Magra (%%)       | %-15.1f | %-15.1f | %+-17.1f |%n" + // Increased width
            "| IMC                    | %-15.2f | %-15.2f | %+-17.2f |%n" + // Increased width
            "-----------------------------------------------------------------------------------", // Adjusted separator
            dataInicio.format(DATE_FORMATTER), dataFim.format(DATE_FORMATTER), // Use formatter here
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
