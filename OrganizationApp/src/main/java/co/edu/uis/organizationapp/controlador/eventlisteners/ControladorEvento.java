package co.edu.uis.organizationapp.controlador.eventlisteners;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import java.util.ArrayList;
import java.util.List;

public class ControladorEvento {
    private List<Evento> events;

    public ControladorEvento() {
        this.events = new ArrayList<>();
    }

    public void crearEvento(Evento event) {
        events.add(event);
    }

    public void actualizarEvento(int index, Evento event) {
        if (index >= 0 && index < events.size()) {
            events.set(index, event);
        }
    }

    public void eliminarEvento(int index) {
        if (index >= 0 && index < events.size()) {
            events.remove(index);
        }
    }

    public List<Evento> getEventos() {
        return events;
    }
}