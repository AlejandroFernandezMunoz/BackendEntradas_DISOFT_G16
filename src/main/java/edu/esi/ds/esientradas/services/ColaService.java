package edu.esi.ds.esientradas.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// Cola virtual de alta demanda (criterio "Gestion de la cola de espera"). Una cola FIFO
// por espectaculo, un unico turno activo, y expulsion automatica si el turno se agota.
@Service
public class ColaService {

    private static final Logger log = LoggerFactory.getLogger(ColaService.class);

    private final Map<Long, Queue<String>> colasPorEspectaculo = new ConcurrentHashMap<>();
    private final Map<Long, String> turnoActual = new ConcurrentHashMap<>();
    private final Map<Long, Long> tiempoInicioTurno = new ConcurrentHashMap<>();
    // Reserva del turno mientras el usuario va a loguearse, para no perder la vez.
    private final Map<Long, String> turnosReservadosLogin = new ConcurrentHashMap<>();

    private static final long TIEMPO_MAXIMO_MS = 3 * 60 * 1000;

    public boolean yaEstaEnCola(Long espectaculoId, String identificador) {
        if (identificador.equals(turnoActual.get(espectaculoId))) return true;
        Queue<String> cola = colasPorEspectaculo.get(espectaculoId);
        return cola != null && cola.contains(identificador);
    }

    public void entrarEnCola(Long espectaculoId, String identificador) {
        colasPorEspectaculo.putIfAbsent(espectaculoId, new ConcurrentLinkedQueue<>());
        Queue<String> cola = colasPorEspectaculo.get(espectaculoId);

        if (!cola.contains(identificador) && !identificador.equals(turnoActual.get(espectaculoId))) {
            cola.add(identificador);
            log.info("[COLA] {} entra en la cola del espectaculo {}", identificador, espectaculoId);
        }
        avanzarTurnoSiEstaLibre(espectaculoId);
    }

    // Posicion 0 = turno activo; >0 = gente delante; -1 = no esta en cola.
    public int obtenerPosicion(Long espectaculoId, String identificador) {
        if (identificador.equals(turnoActual.get(espectaculoId))) return 0;

        Queue<String> cola = colasPorEspectaculo.get(espectaculoId);
        if (cola == null || !cola.contains(identificador)) return -1;

        int posicion = turnoActual.containsKey(espectaculoId) ? 1 : 0;
        for (String idEnCola : cola) {
            posicion++;
            if (idEnCola.equals(identificador)) return posicion;
        }
        return -1;
    }

    public synchronized void finalizarTurno(Long espectaculoId, String identificador) {
        if (identificador.equals(turnoActual.get(espectaculoId))) {
            turnoActual.remove(espectaculoId);
            tiempoInicioTurno.remove(espectaculoId);
            log.info("[COLA] Turno liberado por {}", identificador);
            avanzarTurnoSiEstaLibre(espectaculoId);
        }
    }

    private synchronized void avanzarTurnoSiEstaLibre(Long espectaculoId) {
        if (turnoActual.get(espectaculoId) == null) {
            Queue<String> cola = colasPorEspectaculo.get(espectaculoId);
            if (cola != null && !cola.isEmpty()) {
                String siguiente = cola.poll();
                turnoActual.put(espectaculoId, siguiente);
                tiempoInicioTurno.put(espectaculoId, System.currentTimeMillis());
                log.info("[COLA] Turno asignado a {} en espectaculo {}", siguiente, espectaculoId);
            }
        }
    }

    // Expulsa al titular del turno si supera el tiempo maximo sin reservar (pasa al siguiente).
    @Scheduled(fixedRate = 10000)
    public synchronized void echarUsuariosInactivos() {
        long ahora = System.currentTimeMillis();
        List<Long> expirados = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : tiempoInicioTurno.entrySet()) {
            if (ahora - entry.getValue() > TIEMPO_MAXIMO_MS) {
                expirados.add(entry.getKey());
            }
        }

        for (Long espectaculoId : expirados) {
            String identificador = turnoActual.get(espectaculoId);
            log.warn("[COLA] Tiempo agotado para {}. Pierde el turno.", identificador);
            turnoActual.remove(espectaculoId);
            tiempoInicioTurno.remove(espectaculoId);
            avanzarTurnoSiEstaLibre(espectaculoId);
            turnosReservadosLogin.remove(espectaculoId);
        }
    }

    public void abandonarCola(Long espectaculoId, String identificador) {
        if (identificador.equals(turnoActual.get(espectaculoId))) {
            finalizarTurno(espectaculoId, identificador);
            return;
        }
        Queue<String> cola = colasPorEspectaculo.get(espectaculoId);
        if (cola != null) {
            cola.remove(identificador);
            log.info("[COLA] {} sale de la cola del espectaculo {}", identificador, espectaculoId);
        }
    }

    public void reservarTurnoParaLogin(Long espectaculoId, String sessionIdAnterior) {
        turnosReservadosLogin.put(espectaculoId, sessionIdAnterior);
        log.info("[COLA] Turno reservado para login: sesion {} espectaculo {}", sessionIdAnterior, espectaculoId);
    }

    public synchronized boolean activarTurnoReservado(Long espectaculoId, String sessionIdNueva) {
        String sessionReservada = turnosReservadosLogin.get(espectaculoId);
        if (sessionReservada == null) return false;

        turnosReservadosLogin.remove(espectaculoId);
        turnoActual.put(espectaculoId, sessionIdNueva);
        tiempoInicioTurno.put(espectaculoId, System.currentTimeMillis());
        log.info("[COLA] Turno reservado activado para sesion {} en espectaculo {}", sessionIdNueva, espectaculoId);
        return true;
    }
}
