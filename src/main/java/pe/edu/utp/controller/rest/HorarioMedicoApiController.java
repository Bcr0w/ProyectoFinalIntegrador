package pe.edu.utp.controller.rest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.utp.model.HorarioMedico;
import pe.edu.utp.model.Medico;
import pe.edu.utp.security.CustomUserDetails;
import pe.edu.utp.service.HorarioMedicoService;
import pe.edu.utp.service.facade.HorarioMedicoFacade;
import pe.edu.utp.service.facade.MedicoFacade;

@RestController
@RequestMapping("/api/v1/horario")
public class HorarioMedicoApiController {

    @Autowired
    HorarioMedicoFacade horarioFacade;
    @Autowired
    MedicoFacade medicoFacade;

    private HashMap<String, Object> data;
     //LOGS
    private static final Logger logger = LoggerFactory.getLogger(HorarioMedicoApiController.class);

    @PostMapping("/guardar")
    public ResponseEntity<HashMap<String, Object>> guardarDisponibilidad(
            @RequestBody HashMap<String, String> disponibilidad, Authentication authentication) {
        // Obtener el usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Obtener los valores de fecha y hora desde el Map
        String fechaStr = disponibilidad.get("fecha");
        String horaInicialStr = disponibilidad.get("horaInicial");
        String horaFinalStr = disponibilidad.get("horaFinal");

        // Convertir fecha y horas a LocalDate y LocalTime
        LocalDate fecha = LocalDate.parse(fechaStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime horaInicial = LocalTime.parse(horaInicialStr, formatter);
        LocalTime horaFinal = LocalTime.parse(horaFinalStr, formatter);

        // Ver si no hay igualdad de fecha
        List<HorarioMedico> horarios = horarioFacade.obtenerHorariosPorMedico(userDetails.getIdCargo());
        Optional<HorarioMedico> horarioEncontrado = horarios.stream()
                .filter(horario -> horario.getFecha().equals(fecha))
                .findFirst();
        HorarioMedico horario = new HorarioMedico();
        // Para manejar el resultado:
        if (horarioEncontrado.isPresent()) {
            horario = horarioEncontrado.get();
            horario.setHoraFin(horaFinal);
            horario.setHoraInicio(horaInicial);

            //Log para la actualizacion de un horario
            logger.info("INFO: Se ha actualizado el horario medico:" + horario.getIdHorario());
        } else {

            Medico medico = medicoFacade.buscarMedicoPorId(userDetails.getIdCargo());
            horario.setMedico(medico);
            horario.setFecha(fecha);
            horario.setHoraFin(horaFinal);
            horario.setHoraInicio(horaInicial);
             //Log para la actualizacion de un horario
            logger.info("INFO: Se ha agregado un nuevo horario medico");
        }
        horarioFacade.registrarHorarioMedico(horario);

        


        data = new HashMap<>();
        data.put("mensaje", "Disponibilidad guardada correctamente");

        return ResponseEntity.ok(data);
    }

    @GetMapping("/obtener")
    public ResponseEntity<HashMap<LocalDate, List<String>>> obtenerHoras(@RequestParam("idMedico") Integer idMedico) {
        return ResponseEntity.ok(horarioFacade.obtenerHorasPorMedico(idMedico));
    }

}
