//package br.upe.data.dao;
//
//import br.upe.data.beans.IndicadorBiomedico;
//import br.upe.data.interfaces.IIndicadorBiomedicoRepository;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.stream.Collectors;
//
//public class IndicadorBiomedicoRepositoryImpl implements IIndicadorBiomedicoRepository {
//
//    private final String caminhoArquivo;
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
//    private List<IndicadorBiomedico> indicadores;
//    private AtomicInteger proximoId;
//    private static final Logger logger = Logger.getLogger(IndicadorBiomedicoRepositoryImpl.class.getName());
//
//    public IndicadorBiomedicoRepositoryImpl() {
//        this("src/main/resources/data/indicadores.csv");
//    }
//
//    public IndicadorBiomedicoRepositoryImpl(String caminhoArquivo) {
//        this.caminhoArquivo = caminhoArquivo;
//        this.indicadores = new ArrayList<>();
//        this.proximoId = new AtomicInteger(1);
//        carregarDoCsv();
//    }
//
//    // Busca o usuario pelo arquivo CSV
//    private void carregarDoCsv() {
//        try {
//            Files.createDirectories(Paths.get(caminhoArquivo.substring(0, caminhoArquivo.lastIndexOf("/"))));
//        } catch (IOException e) {
//            logger.log(Level.SEVERE, "Erro ao criar diretório para CSV: {0}", e.getMessage());
//            return;
//        }
//
//        File file = new File(caminhoArquivo);
//        if (!file.exists()) {
//            logger.log(Level.INFO, "Arquivo CSV de indicadores não encontrado. Será criado um novo na primeira inserção.");
//            return;
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String linha;
//            // Ler e armazenar a primeira linha (cabeçalho). Evita descartar o valor lido.
//            String header = reader.readLine();
//            if (header == null) {
//                // Arquivo vazio além do que já foi tratado; nada a carregar.
//                return;
//            }
//            int maxId = 0;
//            while ((linha = reader.readLine()) != null) {
//                IndicadorBiomedico indicador = parseLinhaCsv(linha);
//                if (indicador != null) {
//                    indicadores.add(indicador);
//                    if (indicador.getId() > maxId) {
//                        maxId = indicador.getId();
//                    }
//                }
//            }
//            proximoId.set(maxId + 1);
//        } catch (IOException e) {
//            logger.log(Level.SEVERE, "Erro ao ler indicadores do arquivo CSV: {0}", e.getMessage());
//        }
//    }
//
//    // Grava os indicadores no arquivo CSV
//    private void escreverParaCsv() {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
//            writer.write("id;idUsuario;data;pesoKg;alturaCm;percentualGordura;percentualMassaMagra;imc\n");
//            for (IndicadorBiomedico indicador : indicadores) {
//                writer.write(formatarLinhaCsv(indicador));
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            logger.log(Level.SEVERE, "Erro ao escrever indicadores no arquivo CSV: {0}", e.getMessage());
//        }
//    }
//
//    // Lê uma linha do arquivo CSV
//    private IndicadorBiomedico parseLinhaCsv(String linha) {
//        String[] partes = linha.split(";");
//        if (partes.length == 8) {
//            try {
//                int id = Integer.parseInt(partes[0]);
//                int idUsuario = Integer.parseInt(partes[1]);
//                LocalDate data = LocalDate.parse(partes[2], DATE_FORMATTER);
//                double pesoKg = Double.parseDouble(partes[3]);
//                double alturaCm = Double.parseDouble(partes[4]);
//                double percentualGordura = Double.parseDouble(partes[5]);
//                double percentualMassaMagra = Double.parseDouble(partes[6]);
//                double imc = Double.parseDouble(partes[7]);
//                return IndicadorBiomedico.builder()
//                        .id(id)
//                        .idUsuario(idUsuario)
//                        .data(data)
//                        .pesoKg(pesoKg)
//                        .alturaCm(alturaCm)
//                        .percentualGordura(percentualGordura)
//                        .percentualMassaMagra(percentualMassaMagra)
//                        .imc(imc)
//                        .build();
//            } catch (Exception e) {
//                logger.log(Level.SEVERE, "Erro ao parsear linha CSV de indicador: {0} - {1}", new Object[]{linha, e.getMessage()});
//                return null;
//            }
//        }
//        logger.log(Level.SEVERE, "Formato inválido de linha CSV de indicador: {0}", linha);
//        return null;
//    }
//
//    // Formata uma linha no arquivo CSV
//    private String formatarLinhaCsv(IndicadorBiomedico indicador) {
//        return String.join(";",
//                String.valueOf(indicador.getId()),
//                String.valueOf(indicador.getIdUsuario()),
//                indicador.getData().format(DATE_FORMATTER),
//                String.valueOf(indicador.getPesoKg()),
//                String.valueOf(indicador.getAlturaCm()),
//                String.valueOf(indicador.getPercentualGordura()),
//                String.valueOf(indicador.getPercentualMassaMagra()),
//                String.valueOf(indicador.getImc())
//        );
//    }
//
//    // Salva os indicadores no arquivo CSV
//    @Override
//    public IndicadorBiomedico salvar(IndicadorBiomedico indicador) {
//        if (indicador.getId() == 0) {
//            indicador.setId(gerarProximoId());
//            indicadores.add(indicador);
//        } else {
//            indicadores.removeIf(i -> i.getId() == indicador.getId());
//            indicadores.add(indicador);
//        }
//        escreverParaCsv();
//        return indicador;
//    }
//
//    // Buscar indicadores por id
//    @Override
//    public Optional<IndicadorBiomedico> buscarPorId(int id) {
//        return indicadores.stream()
//                .filter(i -> i.getId() == id)
//                .findFirst();
//    }
//
//    // Buscar todos os indicadores de um usuario
//    @Override
//    public List<IndicadorBiomedico> listarTodos() {
//        return new ArrayList<>(indicadores);
//    }
//
//    @Override
//    public List<IndicadorBiomedico> listarPorUsuario(int idUsuario) {
//        return indicadores.stream()
//                .filter(i -> i.getIdUsuario() == idUsuario)
//                .collect(Collectors.toList());
//    }
//
//    // Listar indicadores de um usuário pelo período
//    @Override
//    public List<IndicadorBiomedico> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
//        return indicadores.stream()
//                .filter(i -> i.getIdUsuario() == idUsuario &&
//                              !i.getData().isBefore(dataInicio) &&
//                              !i.getData().isAfter(dataFim))
//                .collect(Collectors.toList());
//    }
//
//    // Verifica condições e altera indicadores
//    @Override
//
//    public void editar(IndicadorBiomedico indicador) {
//        Optional<IndicadorBiomedico> existenteOpt = buscarPorId(indicador.getId());
//        if (existenteOpt.isPresent()) {
//            indicadores.removeIf(i -> i.getId() == indicador.getId());
//            indicadores.add(indicador);
//            escreverParaCsv();
//        } else {
//            logger.log(Level.SEVERE, "Erro: Indicador com ID {0} não encontrado para edição.", indicador.getId());
//        }
//    }
//
//    // Verifica condições e deleta indicadores pelo id
//    @Override
//
//    public void deletar(int id) {
//        boolean removido = indicadores.removeIf(i -> i.getId() == id);
//        if (removido) {
//            escreverParaCsv();
//        } else {
//            logger.log(Level.SEVERE, "Erro: Indicador com ID {0} não encontrado para remoção.", id);
//        }
//    }
//
//
//    @Override
//    public int gerarProximoId() {
//        return proximoId.getAndIncrement();
//    }
//}
