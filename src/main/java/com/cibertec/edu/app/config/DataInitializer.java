package com.cibertec.edu.app.config;

import com.cibertec.edu.app.entity.TipoRiesgo;
import com.cibertec.edu.app.repository.TipoRiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TipoRiesgoRepository tipoRiesgoRepository;

    @Override
    public void run(String... args) {
        if (tipoRiesgoRepository.count() == 0) {
            List<TipoRiesgo> tipos = List.of(
                    TipoRiesgo.builder().nombre("INCENDIO").descripcion("Incendio forestal o urbano").icono("flame").activo(true).build(),
                    TipoRiesgo.builder().nombre("INUNDACION").descripcion("Inundación por lluvias o desborde").icono("water").activo(true).build(),
                    TipoRiesgo.builder().nombre("DESBORDE").descripcion("Desborde de río o canal").icono("waves").activo(true).build(),
                    TipoRiesgo.builder().nombre("DESLIZAMIENTO").descripcion("Deslizamiento de tierra").icono("mountain").activo(true).build(),
                    TipoRiesgo.builder().nombre("SISMO").descripcion("Movimiento sísmico").icono("activity").activo(true).build()
            );
            tipoRiesgoRepository.saveAll(tipos);
        }
    }
}
