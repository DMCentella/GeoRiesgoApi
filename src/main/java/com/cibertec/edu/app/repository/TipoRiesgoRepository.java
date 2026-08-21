package com.cibertec.edu.app.repository;

import com.cibertec.edu.app.entity.TipoRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoRiesgoRepository extends JpaRepository<TipoRiesgo, Long> {
    Optional<TipoRiesgo> findByNombre(String nombre);
}
