package com.cibertec.edu.app.repository;

import com.cibertec.edu.app.entity.Reporte;
import com.cibertec.edu.app.enums.EstadoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByUsuarioId(Long usuarioId);
    List<Reporte> findByEstado(EstadoReporte estado);
}
