package co.edu.uis.organizationapp.modelo.calendario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.awt.Color;

public class ModeloCalendario {

    private Map<LocalDate, List<Evento>> eventos = new HashMap<>();

    public ModeloCalendario() {
        // Agregar algunos eventos de prueba
        crearEventosDePrueba();
    }

    public List<Evento> getEventos(LocalDate fecha) {
        return eventos.getOrDefault(fecha, List.of());
    }

    public void addEvento(Evento e) {
        eventos.computeIfAbsent(e.getFecha(), d -> new ArrayList<>()).add(e);
    }

    public void eliminarEvento(Evento evento) {
        List<Evento> eventosDelDia = eventos.get(evento.getFecha());
        if (eventosDelDia != null) {
            eventosDelDia.remove(evento);
            // Si no quedan eventos, remover la fecha del mapa
            if (eventosDelDia.isEmpty()) {
                eventos.remove(evento.getFecha());
            }
        }
    }

    private void crearEventosDePrueba() {
        // Evento para hoy
        Evento eventoHoy = new Evento();
        eventoHoy.setFecha(LocalDate.now());
        eventoHoy.setTitulo("Reunión importante");
        eventoHoy.setDescripcion("Discusión del proyecto");
        eventoHoy.setInicio(LocalTime.of(10, 0));
        eventoHoy.setFin(LocalTime.of(11, 30));
        eventoHoy.setColor(new Color(25, 118, 210));
        addEvento(eventoHoy);

        // Evento para mañana
        Evento eventoMañana = new Evento();
        eventoMañana.setFecha(LocalDate.now().plusDays(1));
        eventoMañana.setTitulo("Presentación");
        eventoMañana.setDescripcion("Presentación del proyecto");
        eventoMañana.setInicio(LocalTime.of(14, 0));
        eventoMañana.setFin(LocalTime.of(15, 0));
        eventoMañana.setColor(new Color(76, 175, 80));
        addEvento(eventoMañana);

        // Evento para la próxima semana
        Evento eventoSemana = new Evento();
        eventoSemana.setFecha(LocalDate.now().plusWeeks(1));
        eventoSemana.setTitulo("Entrega final");
        eventoSemana.setDescripcion("Entrega del proyecto");
        eventoSemana.setInicio(LocalTime.of(9, 0));
        eventoSemana.setFin(LocalTime.of(10, 0));
        eventoSemana.setColor(new Color(244, 67, 54));
        addEvento(eventoSemana);

        // Evento para hoy (vista diaria)
        Evento eventoDiario = new Evento();
        eventoDiario.setFecha(LocalDate.now());
        eventoDiario.setTitulo("Reunión de equipo");
        eventoDiario.setDescripcion("Planificación semanal");
        eventoDiario.setInicio(LocalTime.of(15, 0));
        eventoDiario.setFin(LocalTime.of(16, 30));
        eventoDiario.setColor(new Color(156, 39, 176)); // Color morado
        addEvento(eventoDiario);

        // Otro evento para hoy
        Evento eventoDiario2 = new Evento();
        eventoDiario2.setFecha(LocalDate.now());
        eventoDiario2.setTitulo("Revisión de código");
        eventoDiario2.setDescripcion("Sprint review");
        eventoDiario2.setInicio(LocalTime.now().plusHours(1));
        eventoDiario2.setFin(LocalTime.now().plusHours(2));
        eventoDiario2.setColor(new Color(0, 150, 136)); // Color verde azulado
        addEvento(eventoDiario2);
    }
}

