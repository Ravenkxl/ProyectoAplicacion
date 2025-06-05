package co.edu.uis.organizationapp.modelo.calendario;

import java.time.LocalDate;
import java.util.*;
import co.edu.uis.organizationapp.modelo.calendario.PersistenciaCalendario;
import co.edu.uis.organizationapp.modelo.calendario.CalendarioDTO;

public class ModeloCalendario {
    private Map<LocalDate, List<Evento>> eventos = new HashMap<>();
    private Map<LocalDate, List<Tarea>> tareas = new HashMap<>();

    public ModeloCalendario() {
        cargarDesdeJson();
    }

    private void cargarDesdeJson() {
        CalendarioDTO dto = PersistenciaCalendario.cargarDatos();
        this.eventos = new HashMap<>();
        for (Map.Entry<String, List<Evento>> entry : dto.eventos.entrySet()) {
            this.eventos.put(LocalDate.parse(entry.getKey()), entry.getValue());
        }
        this.tareas = new HashMap<>();
        for (Map.Entry<String, List<Tarea>> entry : dto.tareas.entrySet()) {
            this.tareas.put(LocalDate.parse(entry.getKey()), entry.getValue());
        }
    }

    private void guardarEnJson() {
        PersistenciaCalendario.guardarDatos(eventos, tareas);
    }

    public List<Evento> getEventos(LocalDate fecha) {
        return eventos.getOrDefault(fecha, new ArrayList<>());
    }

    public void addEvento(Evento e) {
        if (e == null || !(e instanceof Evento)) return;
        eventos.computeIfAbsent(e.getFecha(), d -> new ArrayList<>()).add(e);
        guardarEnJson();
    }

    public void eliminarEvento(Evento evento) {
        List<Evento> eventosDelDia = eventos.get(evento.getFecha());
        if (eventosDelDia != null) {
            eventosDelDia.remove(evento);
            if (eventosDelDia.isEmpty()) {
                eventos.remove(evento.getFecha());
            }
            guardarEnJson();
        }
    }

    public void actualizarEvento(Evento evento) {
        List<Evento> eventosDelDia = eventos.get(evento.getFecha());
        if (eventosDelDia != null) {
            eventosDelDia.removeIf(e -> e.equals(evento));
            if (eventosDelDia.isEmpty()) {
                eventos.remove(evento.getFecha());
            }
        }
        addEvento(evento);
        guardarEnJson();
    }

    public List<Evento> getTodosLosEventos() {
        List<Evento> todosLosEventos = new ArrayList<>();
        for (List<Evento> listaEventos : eventos.values()) {
            todosLosEventos.addAll(listaEventos);
        }
        return todosLosEventos;
    }

    public List<Tarea> getTareas(LocalDate fecha) {
        return tareas.getOrDefault(fecha, new ArrayList<>());
    }

    public void agregarTarea(Tarea tarea) {
        if (tarea != null && tarea.getFecha() != null) {
            tareas.computeIfAbsent(tarea.getFecha(), k -> new ArrayList<>()).add(tarea);
            guardarEnJson();
        }
    }

    public void eliminarTarea(Tarea tarea) {
        if (tarea != null && tarea.getFecha() != null) {
            List<Tarea> tareasDelDia = tareas.get(tarea.getFecha());
            if (tareasDelDia != null) {
                tareasDelDia.remove(tarea);
                if (tareasDelDia.isEmpty()) {
                    tareas.remove(tarea.getFecha());
                }
                guardarEnJson();
            }
        }
    }

    public void actualizarTarea(Tarea tarea) {
        if (tarea != null) {
            List<Tarea> tareasDelDia = tareas.get(tarea.getFecha());
            if (tareasDelDia != null) {
                tareasDelDia.removeIf(t -> t.equals(tarea));
                if (tareasDelDia.isEmpty()) {
                    tareas.remove(tarea.getFecha());
                }
            }
            agregarTarea(tarea);
            tarea.actualizarEstadoCompletado();
            guardarEnJson();
        }
    }

    public List<Subtarea> getSubtareas(Tarea tareaPadre) {
        return tareaPadre.getSubtareas();
    }

    public void agregarSubtarea(Subtarea subtarea) {
        if (subtarea.getTareaPadre() != null) {
            subtarea.getTareaPadre().agregarSubtarea(subtarea);
            actualizarTarea(subtarea.getTareaPadre());
            guardarEnJson();
        }
    }

    public void eliminarSubtarea(Subtarea subtarea) {
        if (subtarea != null && subtarea.getTareaPadre() != null) {
            Tarea tareaPadre = subtarea.getTareaPadre();
            tareaPadre.eliminarSubtarea(subtarea);
            actualizarTarea(tareaPadre);
            guardarEnJson();
        }
    }

    public void actualizarSubtarea(Subtarea subtarea) {
        if (subtarea != null && subtarea.getTareaPadre() != null) {
            Tarea tareaPadre = subtarea.getTareaPadre();
            actualizarTarea(tareaPadre);
            guardarEnJson();
        }
    }

    public List<Tarea> obtenerTareasPorFecha(LocalDate fecha) {
        List<Tarea> tareasDelDia = new ArrayList<>();
        if (tareas.containsKey(fecha)) {
            tareasDelDia.addAll(tareas.get(fecha));
        }
        List<Evento> eventosDelDia = getEventos(fecha);
        if (eventosDelDia != null) {
            for (Evento evento : eventosDelDia) {
                if (evento.getTareas() != null) {
                    tareasDelDia.addAll(evento.getTareas());
                }
            }
        }
        return tareasDelDia;
    }
}

