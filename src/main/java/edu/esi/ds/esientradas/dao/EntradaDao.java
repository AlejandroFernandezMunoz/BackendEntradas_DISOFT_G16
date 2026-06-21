package edu.esi.ds.esientradas.dao;

import edu.esi.ds.esientradas.dto.SeatDto;
import edu.esi.ds.esientradas.dto.ZoneSummaryDto;
import edu.esi.ds.esientradas.model.DeZona;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntradaDao extends JpaRepository<Entrada, Long> {

    List<Entrada> findByEspectaculoId(Long espectaculoId);

    @Modifying
    @Query("UPDATE Entrada e SET e.estado = :estado WHERE e.id = :idEntrada")
    void updateEstado(@Param("idEntrada") Long idEntrada, @Param("estado") Estado estado);

    Integer countByEspectaculoId(Long espectaculoId);

    Integer countByEspectaculoIdAndEstado(Long espectaculoId, Estado estado);

    // Mapa de butacas (caso teatro) para pintar el plano del frontend.
    @Query("""
        SELECT new edu.esi.ds.esientradas.dto.SeatDto(
            p.id, p.estado, p.precio, p.columna, p.fila, p.planta)
        FROM Precisa p
        WHERE p.espectaculo.id = :espectaculoId
        ORDER BY p.planta, p.fila, p.columna
        """)
    List<SeatDto> findSeatsByEspectaculo(@Param("espectaculoId") Long espectaculoId);

    // Resumen por zonas (caso concierto): disponibles, total y rango de precios.
    @Query("""
        SELECT new edu.esi.ds.esientradas.dto.ZoneSummaryDto(
            dz.zona,
            SUM(CASE WHEN dz.estado = 'DISPONIBLE' THEN 1 ELSE 0 END),
            COUNT(dz), MIN(dz.precio), MAX(dz.precio))
        FROM DeZona dz
        WHERE dz.espectaculo.id = :espectaculoId
        GROUP BY dz.zona
        ORDER BY dz.zona
        """)
    List<ZoneSummaryDto> findZonaSummaryByEspectaculo(@Param("espectaculoId") Long espectaculoId);

    // Al reservar una zona se entrega la entrada disponible mas barata de esa zona.
    @Query("""
        SELECT dz FROM DeZona dz
        WHERE dz.espectaculo.id = :espectaculoId
          AND dz.zona = :zona
          AND dz.estado = 'DISPONIBLE'
        ORDER BY dz.precio ASC
        """)
    List<DeZona> findDisponiblesByEspectaculoAndZona(@Param("espectaculoId") Long espectaculoId,
                                                     @Param("zona") Integer zona);
}
