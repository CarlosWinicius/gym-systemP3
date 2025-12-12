package br.upe.controller.business;

import br.upe.data.entities.IndicadorBiomedico;
import br.upe.data.entities.Usuario;
import br.upe.data.dao.IndicadorBiomedicoDAO;
import br.upe.data.dao.UsuarioDAO;
import br.upe.data.interfaces.IIndicadorBiomedicoRepository;
import br.upe.data.interfaces.IUsuarioRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndicadorBiomedicoService implements IIndicadorBiomedicoService {

    private final IIndicadorBiomedicoRepository indicadorDAO;
    private final IUsuarioRepository usuarioDAO;
    private static final Logger logger = Logger.getLogger(IndicadorBiomedicoService.class.getName());

    // Construtor com Injeção de Dependência (para testes)
    public IndicadorBiomedicoService(IIndicadorBiomedicoRepository indicadorDAO, IUsuarioRepository usuarioDAO) {
        this.indicadorDAO = indicadorDAO;
        this.usuarioDAO = usuarioDAO;
    }

    // Construtor Padrão
    public IndicadorBiomedicoService() {
        this.indicadorDAO = new IndicadorBiomedicoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    public IndicadorBiomedico cadastrarIndicador(int idUsuario, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra) {
        if (pesoKg <= 0 || alturaCm <= 0) {
            throw new IllegalArgumentException("Peso e altura devem ser maiores que zero.");
        }
        if (percentualGordura < 0 || percentualMassaMagra < 0) {
            throw new IllegalArgumentException("Percentuais de gordura e massa magra não podem ser negativos.");
        }
        if (data == null) {
            data = LocalDate.now();
        }

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        double imc = pesoKg / Math.pow(alturaCm / 100.0, 2);

        IndicadorBiomedico novoIndicador = new IndicadorBiomedico();
        novoIndicador.setUsuario(usuario);
        novoIndicador.setDataRegistro(data);
        novoIndicador.setPesoKg(pesoKg);
        novoIndicador.setAlturaCm(alturaCm);
        novoIndicador.setPercentualGordura(percentualGordura);
        novoIndicador.setPercentualMassaMagra(percentualMassaMagra);
        novoIndicador.setImc(imc);

        return indicadorDAO.salvar(novoIndicador);
    }

    @Override
    public void importarIndicadoresCsv(int idUsuario, String caminhoArquivoCsv) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivoCsv))) {
            String linha;
            int linhaNum = 0;
            while ((linha = br.readLine()) != null) {
                linhaNum++;
                if (linhaNum == 1) continue;

                processarLinhaCsv(linha, linhaNum, idUsuario);
            }
            logger.log(Level.INFO, "Importação de indicadores concluída.");
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Arquivo CSV não encontrado: " + caminhoArquivoCsv);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler o arquivo CSV: {0}", e.getMessage());
        }
    }

    @Override
    public List<IndicadorBiomedico> gerarRelatorioPorData(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas de início e fim não podem ser nulas.");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim.");
        }

        List<IndicadorBiomedico> resultados = indicadorDAO.buscarPorPeriodo(idUsuario, dataInicio, dataFim);
        resultados.sort(Comparator.comparing(IndicadorBiomedico::getDataRegistro));
        return resultados;
    }

    @Override
    public RelatorioDiferencaIndicadores gerarRelatorioDiferenca(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas inválidas.");
        }

        List<IndicadorBiomedico> lista = indicadorDAO.buscarPorPeriodo(idUsuario, dataInicio, dataFim);

        RelatorioDiferencaIndicadores relatorio = new RelatorioDiferencaIndicadores();
        relatorio.setDataInicio(dataInicio);
        relatorio.setDataFim(dataFim);

        if (!lista.isEmpty()) {
            lista.sort(Comparator.comparing(IndicadorBiomedico::getDataRegistro));

            relatorio.setIndicadorInicial(Optional.of(lista.get(0)));
            relatorio.setIndicadorFinal(Optional.of(lista.get(lista.size() - 1)));
            relatorio.calcularDiferencas();
        }
        return relatorio;
    }

    @Override
    public List<IndicadorBiomedico> listarTodosDoUsuario(int idUsuario) {
        return indicadorDAO.listarPorUsuario(idUsuario);
    }

    @Override
    public void exportarRelatorioPorDataParaCsv(int idUsuario, LocalDate dataInicio, LocalDate dataFim, String caminhoArquivo) {
        List<IndicadorBiomedico> relatorio = gerarRelatorioPorData(idUsuario, dataInicio, dataFim);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            writer.write("Data;Peso (Kg);Altura (cm);Gordura (%);Massa Magra (%);IMC");
            writer.newLine();

            for (IndicadorBiomedico i : relatorio) {
                writer.write(
                        i.getDataRegistro() + ";" +
                                i.getPesoKg() + ";" +
                                i.getAlturaCm() + ";" +
                                i.getPercentualGordura() + ";" +
                                i.getPercentualMassaMagra() + ";" +
                                String.format("%.2f", i.getImc())
                );
                writer.newLine();
            }
            logger.log(Level.INFO, "Relatório exportado para: {0}", caminhoArquivo);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao exportar: {0}", e.getMessage());
        }
    }

    private void processarLinhaCsv(String linha, int linhaNum, int idUsuario) {
        String[] partes = linha.split(";");
        if (partes.length >= 5) {
            try {
                LocalDate data = LocalDate.parse(partes[0].trim());
                double pesoKg = Double.parseDouble(partes[1].trim());
                double alturaCm = Double.parseDouble(partes[2].trim());
                double percentualGordura = Double.parseDouble(partes[3].trim());
                double percentualMassaMagra = Double.parseDouble(partes[4].trim());

                cadastrarIndicador(idUsuario, data, pesoKg, alturaCm, percentualGordura, percentualMassaMagra);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erro na linha {0}: {1}", new Object[]{linhaNum, e.getMessage()});
            }
        }
    }
}