package co.edu.uis.organizationapp.modelo.calendario;

import java.time.LocalDate;
import java.util.*;

public class ModeloCalendario {

    private Map<LocalDate, List<Evento>> eventos = new HashMap<>();

    public List<Evento> getEventos(LocalDate fecha) {
        return eventos.getOrDefault(fecha, List.of());
    }

    public void addEvento(Evento e) {
        eventos.computeIfAbsent(e.getFecha(), d -> new ArrayList<>()).add(e);
    }
    // update, delete…
}

