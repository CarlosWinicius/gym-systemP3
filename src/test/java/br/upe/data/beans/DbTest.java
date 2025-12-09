package br.upe.data.beans;

import br.upe.data.infra.ConnectionFactory;
import jakarta.persistence.EntityManager;


public class DbTest {
    public static void main(String[] args) {
        System.out.println("--------------------------------------------------");
        System.out.println("⏳ TESTANDO CONEXÃO COM O SUPABASE...");
        System.out.println("--------------------------------------------------");

        try {
            // ATUALIZADO: Agora chamamos .getConnection()
            EntityManager em = ConnectionFactory.getConnection();

            // Se chegou aqui, funcionou!
            System.out.println("✅ SUCESSO! O Banco conectou corretamente.");
            System.out.println("   Status: " + (em.isOpen() ? "Aberto" : "Fechado"));

            em.close();

            // Removi o ConnectionFactory.closeFactory() pois ele não existe
            // no seu código novo. Para um teste rápido, não tem problema.

        } catch (Exception e) {
            System.err.println("❌ FALHA: Não foi possível conectar.");
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}