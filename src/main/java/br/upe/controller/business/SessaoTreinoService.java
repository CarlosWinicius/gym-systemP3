package br.upe.controller.business;

import br.upe.data.entities.*;
import br.upe.data.dao.*;
import br.upe.data.interfaces.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessaoTreinoService {

    private final ISessaoTreinoRepository sessaoRepo;
    private final IPlanoTreinoRepository planoRepo;
    private final IExercicioRepository exercicioRepo;
    private final IUsuarioRepository usuarioRepo;

    private final ItemSessaoTreinoDAO itemSessaoRepo;
    private final ItemPlanoTreinoDAO itemPlanoRepo;

    private static final Logger logger = Logger.getLogger(SessaoTreinoService.class.getName());

    // Construtor com Injeção de Dependência completa (para testes)
    public SessaoTreinoService(ISessaoTreinoRepository sessaoRepo,
                               IPlanoTreinoRepository planoRepo,
                               IExercicioRepository exercicioRepo,
                               IUsuarioRepository usuarioRepo,
                               ItemSessaoTreinoDAO itemSessaoRepo,
                               ItemPlanoTreinoDAO itemPlanoRepo) {
        this.sessaoRepo = sessaoRepo;
        this.planoRepo = planoRepo;
        this.exercicioRepo = exercicioRepo;
        this.usuarioRepo = usuarioRepo;
        this.itemSessaoRepo = itemSessaoRepo;
        this.itemPlanoRepo = itemPlanoRepo;
    }

    // Construtor parcial (mantido para compatibilidade)
    public SessaoTreinoService(ISessaoTreinoRepository sessaoRepo,
                               IPlanoTreinoRepository planoRepo,
                               IExercicioRepository exercicioRepo,
                               IUsuarioRepository usuarioRepo) {
        this.sessaoRepo = sessaoRepo;
        this.planoRepo = planoRepo;
        this.exercicioRepo = exercicioRepo;
        this.usuarioRepo = usuarioRepo;
        this.itemSessaoRepo = new ItemSessaoTreinoDAO();
        this.itemPlanoRepo = new ItemPlanoTreinoDAO();
    }

    // Construtor Padrão
    public SessaoTreinoService() {
        this.sessaoRepo = new SessaoTreinoDAO();
        this.planoRepo = new PlanoTreinoDAO();
        this.exercicioRepo = new ExercicioDAO();
        this.usuarioRepo = new UsuarioDAO();
        this.itemSessaoRepo = new ItemSessaoTreinoDAO();
        this.itemPlanoRepo = new ItemPlanoTreinoDAO();
    }

    public SessaoTreino iniciarSessao(int idUsuario, int idPlano) {
        Usuario usuario = usuarioRepo.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        PlanoTreino plano = planoRepo.buscarPorId(idPlano)
                .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado."));

        if (!plano.getUsuario().getId().equals(idUsuario)) {
            throw new IllegalArgumentException("Este plano não pertence a você.");
        }

        SessaoTreino sessao = new SessaoTreino();
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());

        // ALTERAÇÃO NECESSÁRIA 1: Salvar aqui para gerar o ID da sessão
        sessaoRepo.salvar(sessao);

        return sessao;
    }

    public void registrarExecucao(SessaoTreino sessao, int idExercicio, int repeticoesRealizadas, double cargaRealizada) {
        Exercicio exercicio = exercicioRepo.buscarPorId(idExercicio)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado: " + idExercicio));

        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setSessaoTreino(sessao);
        item.setExercicio(exercicio);
        item.setRepeticoesRealizadas(repeticoesRealizadas);
        item.setCargaRealizada(cargaRealizada);

        itemSessaoRepo.salvar(item);
    }


    public void salvarSessao(SessaoTreino sessao) {
        sessaoRepo.salvar(sessao); // Ou atualizar, dependendo do seu DAO
        logger.log(Level.INFO, "Sessão de treino ID {0} atualizada com sucesso!", sessao.getId());
    }

    public List<SugestaoAtualizacaoPlano> verificarAlteracoesEGerarSugestoes(SessaoTreino sessao) {

        List<ItemPlanoTreino> itensPlanejados = itemPlanoRepo.listarPorPlano(sessao.getPlanoTreino().getId());

        // 2. Busca itens executados na sessão atual
        List<ItemSessaoTreino> itensExecutados = itemSessaoRepo.listarPorSessao(sessao.getId());

        List<SugestaoAtualizacaoPlano> sugestoes = new ArrayList<>();

        // 3. Compara o Realizado vs Planejado
        for (ItemPlanoTreino planejado : itensPlanejados) {
            // Procura o item executado correspondente ao exercicio planejado
            Optional<ItemSessaoTreino> executadoOpt = itensExecutados.stream()
                    .filter(e -> e.getExercicio().getId().equals(planejado.getExercicio().getId()))
                    .findFirst();

            if (executadoOpt.isPresent()) {
                ItemSessaoTreino executado = executadoOpt.get();

                // Lógica simples: Se fez mais carga ou mais repetições, sugere update
                if (executado.getCargaRealizada() > planejado.getCargaKg() ||
                        executado.getRepeticoesRealizadas() > planejado.getRepeticoes()) {

                    sugestoes.add(new SugestaoAtualizacaoPlano(
                            planejado.getExercicio().getId(),
                            planejado.getExercicio().getNome(),
                            planejado.getRepeticoes(),
                            executado.getRepeticoesRealizadas(),
                            planejado.getCargaKg(),
                            executado.getCargaRealizada()
                    ));
                }
            }
        }

        return sugestoes;
    }

    public void aplicarAtualizacoesNoPlano(int idPlano, int idExercicio, int novasRepeticoes, double novaCarga) {
        List<ItemPlanoTreino> itens = itemPlanoRepo.listarPorPlano(idPlano);

        Optional<ItemPlanoTreino> itemOpt = itens.stream()
                .filter(i -> i.getExercicio().getId() == idExercicio)
                .findFirst();

        if (itemOpt.isPresent()) {
            ItemPlanoTreino item = itemOpt.get();
            item.setRepeticoes(novasRepeticoes);
            item.setCargaKg((int) novaCarga);

            itemPlanoRepo.editar(item);
            logger.log(Level.INFO, "Plano atualizado para o exercício ID {0}.", idExercicio);
        } else {
            logger.log(Level.WARNING, "Exercício ID {0} não encontrado no plano.", idExercicio);
        }
    }

    public static class SugestaoAtualizacaoPlano {
        private final int idExercicio;
        private final String nomeExercicio;
        private final int repPlanejadas;
        private final int repRealizadas;
        private final double cargaPlanejada;
        private final double cargaRealizada;

        public SugestaoAtualizacaoPlano(int idExercicio, String nomeExercicio, int repPlanejadas, int repRealizadas, double cargaPlanejada, double cargaRealizada) {
            this.idExercicio = idExercicio;
            this.nomeExercicio = nomeExercicio;
            this.repPlanejadas = repPlanejadas;
            this.repRealizadas = repRealizadas;
            this.cargaPlanejada = cargaPlanejada;
            this.cargaRealizada = cargaRealizada;
        }

        public int getIdExercicio() {
            return idExercicio;
        }

        public String getNomeExercicio() {
            return nomeExercicio;
        }

        public int getRepPlanejadas() {
            return repPlanejadas;
        }

        public int getRepRealizadas() {
            return repRealizadas;
        }

        public double getCargaPlanejada() {
            return cargaPlanejada;
        }

        public double getCargaRealizada() {
            return cargaRealizada;
        }
    }
}