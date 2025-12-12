package br.upe.test.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory de conexões para testes de integração.
 * Usa banco H2 em memória configurado no persistence.xml de teste.
 */
public class TestConnectionFactory {

    private static final Logger logger = Logger.getLogger(TestConnectionFactory.class.getName());
    private static EntityManagerFactory emf;

    private TestConnectionFactory() {}

    /**
     * Retorna um EntityManager conectado ao banco H2 de testes.
     */
    public static EntityManager getTestEntityManager() {
        if (emf == null || !emf.isOpen()) {
            inicializarFactory();
        }
        return emf.createEntityManager();
    }

    /**
     * Retorna a EntityManagerFactory para uso nos DAOs de teste.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            inicializarFactory();
        }
        return emf;
    }

    private static void inicializarFactory() {
        try {
            // "TestPU" é o nome definido no persistence.xml de teste
            emf = Persistence.createEntityManagerFactory("TestPU");
            logger.info("TestConnectionFactory inicializada com sucesso (H2 em memória).");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao inicializar TestConnectionFactory: {0}", e.getMessage());
            throw new RuntimeException("Falha na conexão com o banco de testes", e);
        }
    }

    /**
     * Limpa todas as tabelas do banco de testes.
     * Deve ser chamado no @BeforeEach para garantir isolamento entre testes.
     */
    public static void clearDatabase(EntityManager em) {
        try {
            em.getTransaction().begin();

            // Ordem de deleção respeita as foreign keys (filhos primeiro)
            em.createQuery("DELETE FROM ItemSessaoTreino").executeUpdate();
            em.createQuery("DELETE FROM SessaoTreino").executeUpdate();
            em.createQuery("DELETE FROM ItemPlanoTreino").executeUpdate();
            em.createQuery("DELETE FROM PlanoTreino").executeUpdate();
            em.createQuery("DELETE FROM Exercicio").executeUpdate();
            em.createQuery("DELETE FROM IndicadorBiomedico").executeUpdate();
            em.createQuery("DELETE FROM Usuario").executeUpdate();

            em.getTransaction().commit();
            logger.info("Banco de testes limpo com sucesso.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.WARNING, "Erro ao limpar banco de testes: {0}", e.getMessage());
        }
    }

    /**
     * Fecha a EntityManagerFactory.
     * Deve ser chamado no @AfterAll.
     */
    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
            logger.info("TestConnectionFactory fechada com sucesso.");
        }
    }
}

