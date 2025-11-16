package com.serasa.DesafioTecnicoBackEnd.repository;

import com.serasa.DesafioTecnicoBackEnd.models.PesagensModel;
import com.serasa.DesafioTecnicoBackEnd.models.RelatorioCustosDTO;
import com.serasa.DesafioTecnicoBackEnd.models.RelatorioLucrosPossiveisDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PesagensRepository extends CrudRepository<com.serasa.DesafioTecnicoBackEnd.models.PesagensModel, Integer> {
    @Query(value = """
            SELECT b.id_filial as filial, p.placa, p.id_tipo_grao as tipo_grao, SUM(custo_carga) AS custoTotal 
            FROM pesagens p
            INNER JOIN balanca b ON b.id = p.id_balanca AND b.id_filial = :idFilial
            WHERE (p.placa = :placa) AND 
                  (p.id_tipo_grao = :idTipoGrao) AND
                  (p.data_hora_pesagem BETWEEN :dataInicial AND :dataFinal)
            GROUP BY b.id_filial, p.placa, p.id_tipo_grao
        """,
            nativeQuery = true
    )
    List<RelatorioCustosDTO> queryRelatorioCustosDeCompra(
            @Param("idFilial") Integer idFilial,
            @Param("placa") String placa,
            @Param("idTipoGrao") Integer idTipoGrao,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal
    );

    @Query(value = """
        SELECT p.*
        FROM pesagens p
        INNER JOIN balanca b 
            ON p.id_balanca = b.id and (b.id_filial = :idFilial)
        WHERE 
            (p.placa = :placa)
        AND 
            (p.id_tipo_grao = :idTipoGrao)
        AND
            (p.data_hora_pesagem BETWEEN :dataInicial AND :dataFinal)
        """,
            nativeQuery = true
    )
    List<PesagensModel> queryRelatorioPesagens(
            @Param("idFilial") Integer idFilial,
            @Param("placa") String placa,
            @Param("idTipoGrao") Integer idTipoGrao,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal
    );

    @Query(value = """
            WITH pesagens_com_estoque AS (
                 SELECT
                     b.id_filial,
                     p.placa,
                     p.id_tipo_grao,
                     p.custo_carga,
                     -- Estoque acumulado em kg antes desta pesagem
                     COALESCE(
                         SUM(p.peso_liquido) OVER (
                             PARTITION BY p.id_tipo_grao
                             ORDER BY p.data_hora_pesagem
                             ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING
                         ), 0
                     ) AS estoque_kg
                         
                 FROM pesagens p
                 INNER JOIN balanca b 
                     ON p.id_balanca = b.id and (b.id_filial = :idFilial)
                 WHERE 
                     (p.placa = :placa)
                 AND 
                     (p.id_tipo_grao = :idTipoGrao)
                 AND
                     (p.data_hora_pesagem BETWEEN :dataInicial AND :dataFinal)       
             )
             SELECT
                 p.id_filial as filial,
                 p.placa,
                 p.id_tipo_grao as tipo_grao,             
                 -- Margem calculada
                 GREATEST(0.20 - (p.estoque_kg / 500.0) * 0.005, 0.05) AS margem,
             
                 -- Lucro = custo * margem
                 p.custo_carga * GREATEST(0.20 - (p.estoque_kg / 500.0) * 0.005, 0.05) AS lucro_possivel
             FROM pesagens_com_estoque p
             ORDER BY p.id_filial, p.placa, p.id_tipo_grao;
        """,
            nativeQuery = true
    )
    List<RelatorioLucrosPossiveisDTO> queryRelatorioLucrosPossiveis(
            @Param("idFilial") Integer idFilial,
            @Param("placa") String placa,
            @Param("idTipoGrao") Integer idTipoGrao,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal
    );

}