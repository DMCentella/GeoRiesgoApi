package com.cibertec.edu.app.repository;

import com.cibertec.edu.app.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByActivaTrue();
}
