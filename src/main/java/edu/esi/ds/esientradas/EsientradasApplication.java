package edu.esi.ds.esientradas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling activa las tareas periodicas: liberar prerreservas caducadas
// y expulsar de la cola a quien agota su turno.
@EnableScheduling
@SpringBootApplication
public class EsientradasApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsientradasApplication.class, args);
	}
}
