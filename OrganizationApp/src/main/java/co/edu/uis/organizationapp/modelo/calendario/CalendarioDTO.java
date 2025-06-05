package co.edu.uis.organizationapp.modelo.calendario;

import java.util.*;

public class CalendarioDTO {
    public Map<String, List<Evento>> eventos = new HashMap<>();
    public Map<String, List<Tarea>> tareas = new HashMap<>();
} 