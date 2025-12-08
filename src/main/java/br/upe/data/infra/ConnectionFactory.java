package br.upe.data.infra;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class ConnectionFactory {

    private static EntityManagerFactory emf;

    static {
        try {
            // Carrega variáveis do .env
            Dotenv dotenv = Dotenv.load();

            // Mapa de propriedades para sobrepor o persistence.xml
            Map<String, String> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", dotenv.get("DB_URL"));
            props.put("jakarta.persistence.jdbc.user", dotenv.get("DB_USER"));
            props.put("jakarta.persistence.jdbc.password", dotenv.get("DB_PASSWORD"));
            props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");

            // Cria a EntityManagerFactory passando as propriedades
            emf = Persistence.createEntityManagerFactory("SupabasePU", props);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManager getConnection() {
        return emf.createEntityManager();
    }
}