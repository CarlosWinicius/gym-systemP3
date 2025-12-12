package br.upe.data.interfaces;

import br.upe.data.entities.PlanoTreino;
import java.util.List;
import java.util.Optional;

public interface IPlanoTreinoRepository extends IGenericRepository<PlanoTreino> {
    //
    List<PlanoTreino> buscarTodosDoUsuario(int idUsuario);
    Optional<PlanoTreino> buscarPorNomeEUsuario(int idUsuario, String nomePlano);
    void atualizar(PlanoTreino plano); // Se for diferente de editar, mantenha. Se for igual, apague.
}