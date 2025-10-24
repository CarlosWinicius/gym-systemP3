package br.upe.data.repository.impl;

import br.upe.data.beans.Usuario;
import br.upe.data.TipoUsuario;
import br.upe.data.repository.IUsuarioRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private static final Logger logger = Logger.getLogger(UsuarioRepositoryImpl.class.getName());
    private static final String CAMINHO_ARQUIVO = "src/main/resources/data/usuarios.csv";
    private List<Usuario> usuarios;
    private AtomicInteger proximoId;

    public UsuarioRepositoryImpl() {
        this.usuarios = new ArrayList<>();
        this.proximoId = new AtomicInteger(1);
        carregarDoCsv();
    }

    // Lista o usuario do arquivo CSV
    private void carregarDoCsv() {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar diretório para CSV: {0}", e.getMessage());
            return;
        }

        File file = new File(CAMINHO_ARQUIVO);
        if (!file.exists()) {
            logger.info("Arquivo CSV de usuários não encontrado. Criando usuário 'adm' inicial...");
            Usuario adminUser = new Usuario(gerarProximoId(), "Administrador", "adm", "adm", TipoUsuario.ADMIN);
            usuarios.add(adminUser);
            escreverParaCsv();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            // Ler e armazenar a primeira linha (cabeçalho). Evita descartar o valor lido.
            String header = reader.readLine();
            if (header == null) {
                // Arquivo vazio além do que já foi tratado; nada a carregar.
                return;
            }
            int maxId = 0;
            while ((linha = reader.readLine()) != null) {
                Usuario usuario = parseLinhaCsv(linha);
                if (usuario != null) {
                    usuarios.add(usuario);
                    if (usuario.getId() > maxId) {
                        maxId = usuario.getId();
                    }
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler usuários do arquivo CSV: {0}", e.getMessage());
        }
    }

    // Grava o usuario no arquivo CSV
    private void escreverParaCsv() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO))) {
            writer.write("id;nome;email;senha;tipo\n");
            for (Usuario usuario : usuarios) {
                writer.write(formatarLinhaCsv(usuario));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao escrever usuários no arquivo CSV: {0}", e.getMessage());
        }
    }

    // Ler uma linha do arquivo CSV
    private Usuario parseLinhaCsv(String linha) {
        String[] partes = linha.split(";");
        if (partes.length == 5) {
            try {
                int id = Integer.parseInt(partes[0]);
                String nome = partes[1];
                String email = partes[2];
                String senha = partes[3];
                TipoUsuario tipo = TipoUsuario.valueOf(partes[4]);
                return new Usuario(id, nome, email, senha, tipo);
            } catch (IllegalArgumentException e) {
                logger.log(Level.SEVERE, "Erro ao parsear linha CSV de usuário: {0} - {1}", new Object[]{linha, e.getMessage()});
                return null;
            }
        }
        logger.log(Level.SEVERE, "Formato inválido de linha CSV de usuário: {0}", linha);
        return null;
    }

    // Formata para uma linha do arquivo CSV
    private String formatarLinhaCsv(Usuario usuario) {
        return usuario.getId() + ";" +
                usuario.getNome() + ";" +
                usuario.getEmail() + ";" +
                usuario.getSenha() + ";" +
                usuario.getTipo().name();
    }

    // Salva o usuario no arquivo CSV
    @Override
    public Usuario salvar(Usuario usuario) {
        Optional<Usuario> existenteOpt = buscarPorId(usuario.getId());
        if (!existenteOpt.isPresent() || usuario.getId() == 0) {
            usuario.setId(gerarProximoId());
            usuarios.add(usuario);
        } else {
            editar(usuario);
        }
        escreverParaCsv();
        return usuario;
    }

    // Lista o usuario por id
    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return usuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    // Lista o usuario por email
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    // Lista todos os usuarios
    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    // Verifica condições e altera o usuario
    @Override
    public void editar(Usuario usuario) {
        Optional<Usuario> existenteOpt = buscarPorId(usuario.getId());
        if (existenteOpt.isPresent()) {
            usuarios.removeIf(u -> u.getId() == usuario.getId());
            usuarios.add(usuario);
            escreverParaCsv();
        } else {
            logger.log(Level.SEVERE, "Erro: Usuário com ID {0} não encontrado para edição.", usuario.getId());
        }
    }

    //Verifica condições e deleta o usuario
    @Override
    public void deletar(int id) {
        boolean removido = usuarios.removeIf(u -> u.getId() == id);
        if (removido) {
            escreverParaCsv();
        } else {
            logger.log(Level.SEVERE, "Erro: Usuário com ID {0} não encontrado para remoção.", id);
        }
    }

    @Override
    public int gerarProximoId() {
        return proximoId.getAndIncrement();
    }
}