package br.upe.data.repository.impl;

import br.upe.data.beans.ItemPlanoTreino;
import br.upe.data.beans.PlanoTreino;
import br.upe.data.repository.IPlanoTreinoRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PlanoTreinoRepositoryImpl implements IPlanoTreinoRepository {

    private static final String ARQUIVO_CSV = "src/main/resources/data/planos_treino.csv";
    private List<PlanoTreino> planos;
    private AtomicInteger proximoId;

    public PlanoTreinoRepositoryImpl() {
        this.planos = new ArrayList<>();
        this.proximoId = new AtomicInteger(0);
        carregarDoCsv();
    }

    // Listar usuario do arquivo CSV
    private void carregarDoCsv() {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório para CSV: " + e.getMessage());
            return;
        }

        File file = new File(ARQUIVO_CSV);
        if (!file.exists()) {
            System.out.println("Arquivo " + ARQUIVO_CSV + " não encontrado. Será criado vazio no primeiro salvamento.");
            try {
                if (!file.createNewFile()) {
                    System.out.println("Arquivo " + ARQUIVO_CSV + " já existia e não foi recriado.");
                }
            }
            catch (IOException e) {
                System.err.println("Erro ao criar o arquivo CSV vazio: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;
            int maxId = 0;
            while ((linha = br.readLine()) != null) {
                PlanoTreino plano = parseLinhaCsv(linha);
                if (plano != null) {
                    planos.add(plano);
                    if (plano.getIdPlano() > maxId) {
                        maxId = plano.getIdPlano();
                    }
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV de planos de treino: " + e.getMessage());
        }
    }

    // Gravar plano de treino no arquivo CSV
    private void escreverParaCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_CSV))) {
            for (PlanoTreino plano : planos) {
                bw.write(formatarLinhaCsv(plano));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo CSV de planos de treino: " + e.getMessage());
        }
    }

    // Ler uma linha do arquivo CSV
    private PlanoTreino parseLinhaCsv(String linha) {
        String[] partes = linha.split(";", 4);
        if (partes.length == 4) {
            try {
                int idPlano = Integer.parseInt(partes[0]);
                int idUsuario = Integer.parseInt(partes[1]);
                String nome = partes[2];
                List<ItemPlanoTreino> itensTreino = new ArrayList<>();

                String itensString = partes[3];
                if (!itensString.isEmpty()) {
                    String[] itensSeparados = itensString.split("\\|");
                    for (String itemStr : itensSeparados) {
                        String[] itemPartes = itemStr.split(",");
                        if (itemPartes.length == 3) {
                            int idExercicio = Integer.parseInt(itemPartes[0]);
                            int cargaKg = Integer.parseInt(itemPartes[1]);
                            int repeticoes = Integer.parseInt(itemPartes[2]);
                            itensTreino.add(new ItemPlanoTreino(idExercicio, cargaKg, repeticoes));
                        } else {
                            System.err.println("Formato inválido de item de treino: " + itemStr);
                        }
                    }
                }
                return new PlanoTreino(idPlano, idUsuario, nome, itensTreino);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter número em linha CSV de plano: " + linha);
                return null;
            }
        }
        System.err.println("Formato inválido de linha CSV de plano: " + linha);
        return null;
    }

    // Formata para uma linha no arquivo CSV
    private String formatarLinhaCsv(PlanoTreino plano) {
        String itensString = plano.getItensTreino().stream()
                .map(item -> item.getIdExercicio() + "," + item.getCargaKg() + "," + item.getRepeticoes())
                .collect(Collectors.joining("|"));

        return plano.getIdPlano() + ";" +
                plano.getIdUsuario() + ";" +
                plano.getNome() + ";" +
                itensString;
    }

    // Salvar plano de treino no arquivo CSV
    @Override
    public PlanoTreino salvar(PlanoTreino plano) {
        if (plano.getIdPlano() == 0) {
            plano.setIdPlano(proximoId.getAndIncrement());
            planos.add(plano);
        } else {
            editar(plano);
        }
        escreverParaCsv();
        return plano;
    }

    // Listar todos os planos de treino do usuario
    @Override
    public List<PlanoTreino> buscarTodosDoUsuario(int idUsuario) {
        return planos.stream()
                .filter(p -> p.getIdUsuario() == idUsuario)
                .collect(Collectors.toList());
    }

    // Verifica condições e altera o plano de treino
    @Override
    public void editar(PlanoTreino plano) {
        Optional<PlanoTreino> existenteOpt = buscarPorId(plano.getIdPlano());
        if (existenteOpt.isPresent()) {
            planos.removeIf(p -> p.getIdPlano() == plano.getIdPlano());
            planos.add(plano);
            escreverParaCsv();
        } else {
            System.err.println("Erro: Plano de treino com ID " + plano.getIdPlano() + " não encontrado para edição.");
        }
    }

    // Atualiza o plano de treino de acordo com as alterações
    @Override
    public void atualizar(PlanoTreino plano) {
        editar(plano);
    }

    // Verifica as condições e deleta o plano de treino
    @Override
    public void deletar(int idPlano) {
        boolean removido = planos.removeIf(p -> p.getIdPlano() == idPlano);
        if (removido) {
            escreverParaCsv();
        } else {
            System.err.println("Erro: Plano de treino com ID " + idPlano + " não encontrado para remoção.");
        }
    }

    // Lista o plano de treino pelo nome e id do usuario
    @Override
    public Optional<PlanoTreino> buscarPorNomeEUsuario(int idUsuario, String nomePlano) {
        return planos.stream()
                .filter(p -> p.getIdUsuario() == idUsuario && p.getNome().equalsIgnoreCase(nomePlano))
                .findFirst();
    }

    // Lista o plano de treino pelo id
    @Override
    public Optional<PlanoTreino> buscarPorId(int idPlano) {
        return planos.stream()
                .filter(p -> p.getIdPlano() == idPlano)
                .findFirst();
    }

    @Override
    public int proximoId() {
        return proximoId.get();
    }
}