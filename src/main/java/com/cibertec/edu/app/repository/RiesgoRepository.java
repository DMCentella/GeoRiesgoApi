package com.cibertec.edu.app.repository;

import com.cibertec.edu.app.entity.Riesgo;
import com.cibertec.edu.app.enums.NivelRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {
    List<Riesgo> findByActivoTrue();
    List<Riesgo> findByTipoRiesgoId(Long tipoRiesgoId);
    List<Riesgo> findByNivel(NivelRiesgo nivel);
    List<Riesgo> findByTipoRiesgoIdAndNivel(Long tipoRiesgoId, NivelRiesgo nivel);
    List<Riesgo> findByActivoTrueAndTipoRiesgoId(Long tipoRiesgoId);
    List<Riesgo> findByActivoTrueAndNivel(NivelRiesgo nivel);
    List<Riesgo> findByActivoTrueAndTipoRiesgoIdAndNivel(Long tipoRiesgoId, NivelRiesgo nivel);
}
