//package br.upe.data.repository.impl;
//
//import br.upe.data.beans.ItemSessaoTreino;
//import br.upe.data.beans.SessaoTreino;
//import org.junit.jupiter.api.*;
//import java.io.File;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class SessaoTreinoRepositoryImplTest {
//
//    private SessaoTreinoRepositoryImpl repository;
//    private final String arquivoTeste = "src/main/resources/data/sessoes_treino_test.csv";
//
//    @BeforeEach
//    void setUp() {
//        // Criar repositório para teste
//        repository = new SessaoTreinoRepositoryImpl() {
//            @Override
//
//            protected String getArquivoCSV() {
//                return arquivoTeste;
//            }
//        };
//
//        // Limpar o arquivo de teste antes de cada execução
//        new File(arquivoTeste).delete();
//    }
//
//    @Test
//    void deveSalvarSessaoDeTreino() {
//        SessaoTreino nova = new SessaoTreino(0, 1, 1, LocalDate.now(),Arrays.asList(new ItemSessaoTreino(10, 12, 50.0))
//        );
//
//        SessaoTreino salva = repository.salvar(nova);
//
//        assertTrue(salva.getIdSessao() > 0);
//        Optional<SessaoTreino> encontrada = repository.buscarPorId(salva.getIdSessao());
//        assertTrue(encontrada.isPresent());
//        assertEquals(salva.getIdUsuario(), encontrada.get().getIdUsuario());
//    }
//
//    @Test
//    void deveEditarSessaoDeTreino() {
//        SessaoTreino original = repository.salvar(
//                new SessaoTreino(0, 1, 1, LocalDate.now(), List.of())
//        );
//
//        original.setIdPlanoTreino(99);
//        repository.editar(original);
//
//        Optional<SessaoTreino> editada = repository.buscarPorId(original.getIdSessao());
//        assertTrue(editada.isPresent());
//        assertEquals(99, editada.get().getIdPlanoTreino());
//    }
//
//    @Test
//    void deveDeletarSessao() {
//        SessaoTreino salva = repository.salvar(
//                new SessaoTreino(0, 2, 1, LocalDate.now(), List.of())
//        );
//
//        repository.deletar(salva.getIdSessao());
//        Optional<SessaoTreino> resultado = repository.buscarPorId(salva.getIdSessao());
//        assertFalse(resultado.isPresent());
//    }
//
//    @AfterEach
//    void limpar() {
//        // Limpa o arquivo depois de cada teste
//        new File(arquivoTeste).delete();
//    }
//}
