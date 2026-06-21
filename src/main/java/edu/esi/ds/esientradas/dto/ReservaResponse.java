package edu.esi.ds.esientradas.dto;

// Respuesta a una prerreserva: precio acumulado y el token que el front debe reenviar
// en la siguiente prerreserva (mensajes 20 y 22 del escenario del enunciado).
public record ReservaResponse(Long precioTotal, String tokenReserva) {
}
