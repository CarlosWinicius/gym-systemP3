//package br.upe.data;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TipoUsuarioTest {
//
//    @Test
//    @DisplayName("Deve retornar o valor correto para COMUM")
//    void testGetValorComum() {
//        assertEquals(0, TipoUsuario.COMUM.getValor());
//    }
//
//    @Test
//    @DisplayName("Deve retornar o valor correto para ADMIN")
//    void testGetValorAdmin() {
//        assertEquals(1, TipoUsuario.ADMIN.getValor());
//    }
//
//    @Test
//    @DisplayName("Deve converter valor 0 para COMUM")
//    void testFromValor0() {
//        assertEquals(TipoUsuario.COMUM, TipoUsuario.fromValor(0));
//    }
//
//    @Test
//    @DisplayName("Deve converter valor 1 para ADMIN")
//    void testFromValor1() {
//        assertEquals(TipoUsuario.ADMIN, TipoUsuario.fromValor(1));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para valor inválido")
//    void testFromValorInvalido() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            TipoUsuario.fromValor(99);
//        });
//
//        assertTrue(exception.getMessage().contains("inválido"));
//    }
//
//    @Test
//    @DisplayName("Deve retornar todos os valores do enum")
//    void testValues() {
//        TipoUsuario[] tipos = TipoUsuario.values();
//        assertEquals(2, tipos.length);
//        assertEquals(TipoUsuario.COMUM, tipos[0]);
//        assertEquals(TipoUsuario.ADMIN, tipos[1]);
//    }
//
//    @Test
//    @DisplayName("Deve fazer valueOf corretamente")
//    void testValueOf() {
//        assertEquals(TipoUsuario.COMUM, TipoUsuario.valueOf("COMUM"));
//        assertEquals(TipoUsuario.ADMIN, TipoUsuario.valueOf("ADMIN"));
//    }
//}
//
