package br.upe.data.repository.impl;

import br.upe.data.beans.ItemSessaoTreino;
import br.upe.data.beans.SessaoTreino;
import br.upe.data.repository.ISessaoTreinoRepository;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SessaoTreinoRepositoryImpl implements ISessaoTreinoRepository {

    private final List<SessaoTreino> sessoes;
    private final AtomicInteger proximoId;
    private static final Logger logger = Logger.getLogger(SessaoTreinoRepositoryImpl.class.getName());

    public SessaoTreinoRepositoryImpl() {
        this.sessoes = new ArrayList<>();
        this.proximoId = new AtomicInteger(1); // Inicia em 1 para novos IDs
        carregarDoCsv();
    }

    /**
     * Define o caminho do arquivo CSV.
     * Este método é 'protected' para que classes de teste possam sobrescrevê-lo
     * e usar um arquivo de dados separado, evitando corromper os dados de produção.
     * @return O caminho completo para o arquivo CSV.
     */
    protected String getArquivoCSV() {
        return "src/main/resources/data/sessoes_treino.csv";
    }

    private void carregarDoCsv() {
        File arquivo = new File(getArquivoCSV());

        if (!arquivo.exists()) {
            logger.log(Level.INFO, "Arquivo {0} não encontrado, será criado.", getArquivoCSV());
            try {
                // Garante que os diretórios pais existam
                Files.createDirectories(arquivo.getParentFile().toPath());
                if (!arquivo.createNewFile()) {
                    logger.log(Level.INFO, "Arquivo {0} já existia e não foi recriado.", getArquivoCSV());
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erro crítico ao criar arquivo CSV: {0}", e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            int maxId = 0;
            String linha;
            while ((linha = br.readLine()) != null) {
                SessaoTreino sessao = parseLinhaCsv(linha);
                if (sessao != null) {
                    sessoes.add(sessao);
                    if (sessao.getIdSessao() > maxId) {
                        maxId = sessao.getIdSessao();
                    }
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler o arquivo CSV de sessões: {0}", e.getMessage());
        }
    }

    private synchronized void escreverParaCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getArquivoCSV()))) {
            for (SessaoTreino sessao : sessoes) {
                bw.write(formatarLinhaCsv(sessao));
                bw.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao escrever no arquivo CSV de sessões: {0}", e.getMessage());
        }
    }

    private SessaoTreino parseLinhaCsv(String linha) {
        String[] partes = linha.split(";", 5);
        if (partes.length < 5) {
            logger.log(Level.SEVERE, "Linha CSV de sessão com formato inválido (menos de 5 partes): {0}", linha);
            return null;
        }

        try {
            int idSessao = Integer.parseInt(partes[0]);
            int idUsuario = Integer.parseInt(partes[1]);
            int idPlanoTreino = Integer.parseInt(partes[2]);
            LocalDate dataSessao = LocalDate.parse(partes[3]);
            List<ItemSessaoTreino> itensExecutados = new ArrayList<>();

            String itensString = partes[4];
            if (!itensString.isEmpty()) {
                for (String itemStr : itensString.split("\\|")) {
                    String[] itemPartes = itemStr.split(",");
                    if (itemPartes.length == 3) {
                        int idExercicio = Integer.parseInt(itemPartes[0]);
                        int repeticoes = Integer.parseInt(itemPartes[1]);
                        double carga = Double.parseDouble(itemPartes[2]);
                        itensExecutados.add(new ItemSessaoTreino(idExercicio, repeticoes, carga));
                    }
                }
            }
            return new SessaoTreino(idSessao, idUsuario, idPlanoTreino, dataSessao, itensExecutados);
        } catch (NumberFormatException | DateTimeParseException e) {
            logger.log(Level.SEVERE, "Erro de parsing em linha CSV de sessão: {0} - {1}", new Object[]{linha, e.getMessage()});
            return null;
        }
    }

    private String formatarLinhaCsv(SessaoTreino sessao) {
        String itensString = sessao.getItensExecutados().stream()
                .map(item -> String.join(",",
                        String.valueOf(item.getIdExercicio()),
                        String.valueOf(item.getRepeticoesRealizadas()),
                        String.valueOf(item.getCargaRealizada())))
                .collect(Collectors.joining("|"));

        return String.join(";",
                String.valueOf(sessao.getIdSessao()),
                String.valueOf(sessao.getIdUsuario()),
                String.valueOf(sessao.getIdPlanoTreino()),
                sessao.getDataSessao().toString(),
                itensString);
    }

    @Override
    public synchronized SessaoTreino salvar(SessaoTreino sessao) {
        if (sessao.getIdSessao() == 0) { // Considera 0 como um novo registro
            sessao.setIdSessao(proximoId.getAndIncrement());
            sessoes.add(sessao);
        } else {
            // Se já existe um com esse ID, remove o antigo para adicionar o novo
            sessoes.removeIf(s -> s.getIdSessao() == sessao.getIdSessao());
            sessoes.add(sessao);
        }
        escreverParaCsv();
        return sessao;
    }

    @Override
    public Optional<SessaoTreino> buscarPorId(int idSessao) {
        return sessoes.stream().filter(s -> s.getIdSessao() == idSessao).findFirst();
    }

    @Override
    public List<SessaoTreino> buscarTodosDoUsuario(int idUsuario) {
        return sessoes.stream().filter(s -> s.getIdUsuario() == idUsuario).collect(Collectors.toList());
    }

    @Override
    public List<SessaoTreino> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        return sessoes.stream()
                .filter(s -> s.getIdUsuario() == idUsuario &&
                        !s.getDataSessao().isBefore(dataInicio) &&
                        !s.getDataSessao().isAfter(dataFim))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void editar(SessaoTreino sessao) {
        // A lógica de 'salvar' já cobre a edição.
        salvar(sessao);
    }

    @Override
    public synchronized void deletar(int idSessao) {
        boolean removido = sessoes.removeIf(s -> s.getIdSessao() == idSessao);
        if (removido) {
            escreverParaCsv();
        } else {
            logger.log(Level.SEVERE, "Erro: Sessão de treino com ID {0} não encontrada para remoção.", idSessao);
        }
    }

    @Override
    public int proximoId() {
        return proximoId.get();
    }
}