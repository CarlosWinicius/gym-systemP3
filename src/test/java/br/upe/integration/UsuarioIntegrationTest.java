//package br.upe.integration;
//
//import br.upe.controller.business.UsuarioService;
//import br.upe.data.TipoUsuario;
//import br.upe.data.beans.Usuario;
//import br.upe.data.repository.IUsuarioRepository;
//import br.upe.data.repository.impl.UsuarioRepositoryImpl;
//import org.junit.jupiter.api.*;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Testes de integração para o fluxo completo de usuários
// * Integra UsuarioService + UsuarioRepositoryImpl + arquivo CSV
// */
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class UsuarioIntegrationTest {
//
//    private UsuarioService usuarioService;
//    private IUsuarioRepository usuarioRepository;
//    private static final String TEST_CSV_PATH = "src/test/resources/data/usuarios_integration_test.csv";
//
//    @BeforeEach
//    void setUp() throws IOException {
//        Files.createDirectories(Paths.get("src/test/resources/data"));
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//        usuarioRepository = new UsuarioRepositoryImpl(TEST_CSV_PATH);
//        usuarioService = new UsuarioService(usuarioRepository);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//    }
//
//    @Test
//    @Order(1)
//    @DisplayName("Integração: Deve cadastrar usuário e persistir no CSV")
//    void testCadastrarUsuarioCompleto() {
//        // Dado
//        String nome = "João Silva";
//        String email = "joao.silva@email.com";
//        String senha = "senha123";
//        TipoUsuario tipo = TipoUsuario.COMUM;
//
//        // Quando
//        Usuario usuarioCadastrado = usuarioService.cadastrarUsuario(nome, email, senha, tipo);
//
//        // Então
//        assertNotNull(usuarioCadastrado);
//        assertNotEquals(0, usuarioCadastrado.getId());
//        assertEquals(nome, usuarioCadastrado.getNome());
//        assertEquals(email, usuarioCadastrado.getEmail());
//        assertEquals(senha, usuarioCadastrado.getSenha());
//        assertEquals(tipo, usuarioCadastrado.getTipo());
//
//        // Verifica persistência
//        Optional<Usuario> usuarioBuscado = usuarioRepository.buscarPorId(usuarioCadastrado.getId());
//        assertTrue(usuarioBuscado.isPresent());
//        assertEquals(nome, usuarioBuscado.get().getNome());
//    }
//
//    @Test
//    @Order(2)
//    @DisplayName("Integração: Deve autenticar usuário cadastrado")
//    void testFluxoCompletoCadastroEAutenticacao() {
//        // Cadastro
//        String email = "maria@email.com";
//        String senha = "senhaSegura123";
//        Usuario usuarioCadastrado = usuarioService.cadastrarUsuario("Maria", email, senha, TipoUsuario.COMUM);
//
//        // Autenticação com credenciais corretas
//        Usuario usuarioAutenticado = usuarioService.autenticarUsuario(email, senha);
//        assertNotNull(usuarioAutenticado);
//        assertEquals(usuarioCadastrado.getId(), usuarioAutenticado.getId());
//        assertEquals(email, usuarioAutenticado.getEmail());
//
//        // Autenticação com senha incorreta
//        Usuario falhaAutenticacao = usuarioService.autenticarUsuario(email, "senhaErrada");
//        assertNull(falhaAutenticacao);
//    }
//
//    @Test
//    @Order(3)
//    @DisplayName("Integração: Deve promover usuário comum a admin")
//    void testFluxoPromocaoUsuario() {
//        // Cadastra usuário comum
//        Usuario usuario = usuarioService.cadastrarUsuario("Carlos", "carlos@email.com", "senha", TipoUsuario.COMUM);
//        assertEquals(TipoUsuario.COMUM, usuario.getTipo());
//
//        // Promove a admin
//        usuarioService.promoverUsuarioAAdmin(usuario.getId());
//
//        // Verifica promoção
//        Optional<Usuario> usuarioPromovido = usuarioService.buscarUsuarioPorId(usuario.getId());
//        assertTrue(usuarioPromovido.isPresent());
//        assertEquals(TipoUsuario.ADMIN, usuarioPromovido.get().getTipo());
//
//        // Verifica persistência no CSV
//        Optional<Usuario> usuarioRecarregado = usuarioRepository.buscarPorId(usuario.getId());
//        assertTrue(usuarioRecarregado.isPresent());
//        assertEquals(TipoUsuario.ADMIN, usuarioRecarregado.get().getTipo());
//    }
//
//    @Test
//    @Order(4)
//    @DisplayName("Integração: Deve rebaixar admin a comum (exceto admin principal)")
//    void testFluxoRebaixamentoUsuario() {
//        // Cadastra admin
//        Usuario admin = usuarioService.cadastrarUsuario("Admin2", "admin2@email.com", "senha", TipoUsuario.ADMIN);
//        assertEquals(TipoUsuario.ADMIN, admin.getTipo());
//
//        // Rebaixa a comum
//        usuarioService.rebaixarUsuarioAComum(admin.getId());
//
//        // Verifica rebaixamento
//        Optional<Usuario> usuarioRebaixado = usuarioService.buscarUsuarioPorId(admin.getId());
//        assertTrue(usuarioRebaixado.isPresent());
//        assertEquals(TipoUsuario.COMUM, usuarioRebaixado.get().getTipo());
//    }
//
//    @Test
//    @Order(5)
//    @DisplayName("Integração: Não deve permitir cadastro com email duplicado")
//    void testValidacaoEmailDuplicado() {
//        // Cadastra primeiro usuário
//        String emailDuplicado = "duplicado@email.com";
//        usuarioService.cadastrarUsuario("Usuario1", emailDuplicado, "senha1", TipoUsuario.COMUM);
//
//        // Tenta cadastrar com mesmo email
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.cadastrarUsuario("Usuario2", emailDuplicado, "senha2", TipoUsuario.COMUM);
//        });
//
//        assertTrue(exception.getMessage().contains("Já existe um usuário com este email"));
//    }
//
//    @Test
//    @Order(6)
//    @DisplayName("Integração: Deve validar formato de email")
//    void testValidacaoFormatoEmail() {
//        // Email sem @
//        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.cadastrarUsuario("Teste", "emailinvalido", "senha", TipoUsuario.COMUM);
//        });
//        assertTrue(exception1.getMessage().contains("Email inválido"));
//
//        // Email sem .
//        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.cadastrarUsuario("Teste", "email@invalido", "senha", TipoUsuario.COMUM);
//        });
//        assertTrue(exception2.getMessage().contains("Email inválido"));
//    }
//
//    @Test
//    @Order(7)
//    @DisplayName("Integração: Não deve permitir rebaixar admin principal (ID 1)")
//    void testProtecaoAdminPrincipal() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.rebaixarUsuarioAComum(1);
//        });
//        assertTrue(exception.getMessage().contains("administrador principal não pode ser rebaixado"));
//    }
//
//    @Test
//    @Order(8)
//    @DisplayName("Integração: Deve buscar usuário por email")
//    void testBuscaPorEmail() {
//        String email = "busca@email.com";
//        Usuario cadastrado = usuarioService.cadastrarUsuario("Teste Busca", email, "senha", TipoUsuario.COMUM);
//
//        Optional<Usuario> encontrado = usuarioService.buscarUsuarioPorEmail(email);
//        assertTrue(encontrado.isPresent());
//        assertEquals(cadastrado.getId(), encontrado.get().getId());
//        assertEquals(email, encontrado.get().getEmail());
//    }
//
//    @Test
//    @Order(9)
//    @DisplayName("Integração: Deve validar campos obrigatórios no cadastro")
//    void testValidacaoCamposObrigatorios() {
//        // Nome vazio
//        assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.cadastrarUsuario("", "email@test.com", "senha", TipoUsuario.COMUM);
//        });
//
//        // Email vazio
//        assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.cadastrarUsuario("Nome", "", "senha", TipoUsuario.COMUM);
//        });
//
//        // Senha vazia
//        assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.cadastrarUsuario("Nome", "email@test.com", "", TipoUsuario.COMUM);
//        });
//
//        // Nome null
//        assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.cadastrarUsuario(null, "email@test.com", "senha", TipoUsuario.COMUM);
//        });
//    }
//
//    @Test
//    @Order(10)
//    @DisplayName("Integração: Deve lidar com autenticação com campos vazios")
//    void testAutenticacaoCamposVazios() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.autenticarUsuario("", "senha");
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.autenticarUsuario("email@test.com", "");
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            usuarioService.autenticarUsuario(null, "senha");
//        });
//    }
//}
//
