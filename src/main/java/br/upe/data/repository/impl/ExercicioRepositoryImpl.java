package br.upe.data.repository.impl;

import br.upe.data.beans.Exercicio;
import br.upe.data.repository.IExercicioRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ExercicioRepositoryImpl implements IExercicioRepository {

    private static final String ARQUIVO_CSV = "src/main/resources/data/exercicios.csv";
    private List<Exercicio> exercicios;
    private AtomicInteger proximoId;

    public ExercicioRepositoryImpl() {
        this.exercicios = new ArrayList<>();
        this.proximoId = new AtomicInteger(0);
        carregarDoCsv();
    }

    // Listar usuario do arquivo csv
    private void carregarDoCsv() {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório para CSV: " + e.getMessage());
            return;
        }

        File file = new File(ARQUIVO_CSV);
        if (!file.exists()) {
            System.out.println("Arquivo CSV não encontrado. Será criado vazio no primeiro salvamento.");
            try {
                if (!file.createNewFile()) {
                    System.out.println("Arquivo " + ARQUIVO_CSV + " já existia e não foi recriado.");
                }
            } catch (IOException e) {
                System.err.println("Erro ao criar o arquivo CSV vazio: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;
            int maxId = 0;
            while ((linha = br.readLine()) != null) {
                Exercicio exercicio = parseLinhaCsv(linha);
                if (exercicio != null) {
                    exercicios.add(exercicio);
                    if (exercicio.getIdExercicio() > maxId) {
                        maxId = exercicio.getIdExercicio();
                    }
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV de exercícios: " + e.getMessage());
        }
    }

    // Gravar exercicio no arquivo CSV
    private void escreverParaCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_CSV))) {
            for (Exercicio exercicio : exercicios) {
                bw.write(formatarLinhaCsv(exercicio));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo CSV de exercícios: " + e.getMessage());
        }
    }

    //
    private Exercicio parseLinhaCsv(String linha) {
        String[] partes = linha.split(";");
        if (partes.length == 5) {
            try {
                int id = Integer.parseInt(partes[0]);
                int idUsuario = Integer.parseInt(partes[1]);
                String nome = partes[2];
                String descricao = partes[3];
                String caminhoGif = partes[4];
                return new Exercicio(id, idUsuario, nome, descricao, caminhoGif);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter número em linha CSV: " + linha);
                return null;
            }
        }
        System.err.println("Formato inválido de linha CSV: " + linha);
        return null;
    }

    private String formatarLinhaCsv(Exercicio exercicio) {
        return exercicio.getIdExercicio() + ";" +
                exercicio.getIdUsuario() + ";" +
                exercicio.getNome() + ";" +
                exercicio.getDescricao() + ";" +
                exercicio.getCaminhoGif();
    }

    // Salvar exercicio no arquivo CSV
    @Override
    public Exercicio salvar(Exercicio exercicio) {
        if (exercicio.getIdExercicio() == 0) {
            exercicio.setIdExercicio(proximoId.getAndIncrement());
            exercicios.add(exercicio);
        } else {
            editar(exercicio);
        }
        escreverParaCsv();
        return exercicio;
    }

    // Buscar todos os exercicios pelo id do usuario
    @Override
    public List<Exercicio> buscarTodosDoUsuario(int idUsuario) {
        return exercicios.stream()
                .filter(e -> e.getIdUsuario() == idUsuario)
                .collect(java.util.stream.Collectors.toList());
    }

    // Editar o exercicio no arquivo CSV
    @Override
    public void editar(Exercicio exercicio) {
        Optional<Exercicio> existenteOpt = buscarPorId(exercicio.getIdExercicio());
        if (existenteOpt.isPresent()) {
            exercicios.removeIf(e -> e.getIdExercicio() == exercicio.getIdExercicio());
            exercicios.add(exercicio);
            escreverParaCsv();
        } else {
            System.err.println("Erro: Exercício com ID " + exercicio.getIdExercicio() + " não encontrado para edição.");
        }
    }

    // Deletar exercicio do arquivo CSV pelo id
    @Override
    public void deletar(int idExercicio) {
        boolean removido = exercicios.removeIf(e -> e.getIdExercicio() == idExercicio);
        if (removido) {
            escreverParaCsv();
        } else {
            System.err.println("Erro: Exercício com ID " + idExercicio + " não encontrado para remoção.");
        }
    }

    // Buscar exercicio no arquivo CSV pelo nome
    @Override
    public Optional<Exercicio> buscarPorNome(String nome) {
        return exercicios.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    // Buscar exercicio no arquivo CSV pelo id
    @Override
    public Optional<Exercicio> buscarPorId(int idExercicio) {
        return exercicios.stream()
                .filter(e -> e.getIdExercicio() == idExercicio)
                .findFirst();
    }

    @Override
    public int proximoId() {
        return proximoId.get();
    }
}