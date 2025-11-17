package com.serasa.DesafioTecnicoBackEnd.repository.specifications;

import com.serasa.DesafioTecnicoBackEnd.models.PesagensModel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class PesagensSpecification {

    public static Specification<PesagensModel> filter(
            Integer idFilial,
            String placa,
            Integer idTipoGrao,
            LocalDateTime dataInicial,
            LocalDateTime dataFinal) {

        return (Root<PesagensModel> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            if (idFilial != null) {
                predicate = cb.and(predicate, cb.equal(root.get("balanca").get("filial").get("id"), idFilial));
            }

            if (placa != null && !placa.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("caminhao").get("placa"), placa));
            }

            if (idTipoGrao != null) {
                predicate = cb.and(predicate, cb.equal(root.get("tipoGrao").get("id"), idTipoGrao));
            }

            predicate = cb.and(predicate, cb.between(root.get("dataHoraPesagem"), dataInicial, dataFinal));

            return predicate;
        };
    }
}
