package pe.edu.utp.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.utp.dto.CitaDto;
import pe.edu.utp.model.CitaMedica;
import pe.edu.utp.service.facade.CitaMedicaFacade;

@RestController
@RequestMapping("/api/v1/cita")
public class CitaMedicaApiController {
    @Autowired
    CitaMedicaFacade citaFacade;

    HashMap<String, Object> data;

   
    @GetMapping("/listar")
    public List<CitaDto> listar(){
      List<CitaMedica> citasMedicas = citaFacade.obtenerTodasCitasMedicas();
      List<CitaDto> citas = new ArrayList<>();
      for (CitaMedica cita : citasMedicas) {
        citas.add(new CitaDto(cita.getIdCita(),cita.getPaciente().getNombre(),cita.getMedico().getNombre(),cita.getFecha(),cita.getHora(),cita.getEstado().name(),cita.getDetalle(),cita.getPago().name()));
      }
      return citas;
    }


    
}
