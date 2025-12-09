package br.upe.data.infra;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class ConnectionFactory {

    // O objeto "pesado" que deve ser único na aplicação
    private static EntityManagerFactory emf;

    // 1. Construtor privado para impedir instâncias (new ConnectionFactory())
    private ConnectionFactory() {}

    // 2. Método para fabricar o EntityManager
    public static EntityManager getConnection() {
        if (emf == null || !emf.isOpen()) {
            inicializarFactory();
        }
        return emf.createEntityManager();
    }

    // Método auxiliar para configurar o Hibernate (Lazy Loading)
    private static void inicializarFactory() {
        try {
            Dotenv dotenv = Dotenv.load();

            Map<String, String> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", dotenv.get("DB_URL"));
            props.put("jakarta.persistence.jdbc.user", dotenv.get("DB_USER"));
            props.put("jakarta.persistence.jdbc.password", dotenv.get("DB_PASSWORD"));
            props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");

            // ATENÇÃO: O nome aqui ("NeonPU") deve ser IGUAL ao do persistence.xml
            emf = Persistence.createEntityManagerFactory("NeonPU", props);

        } catch (Exception e) {
            // Logar o erro de forma clara é crucial aqui
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
            throw new RuntimeException("Falha na conexão com o banco de dados", e);
        }
    }

    // 3. Método para fechar a fábrica ao encerrar a aplicação
    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory fechada com sucesso.");
        }
    }
}