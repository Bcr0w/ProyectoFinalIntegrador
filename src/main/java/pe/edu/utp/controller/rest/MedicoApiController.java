package pe.edu.utp.controller.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.utp.dto.MedicoDto;
import pe.edu.utp.dto.MedicoListaDto;
import pe.edu.utp.model.Medico;
import pe.edu.utp.service.facade.MedicoFacade;

@RestController
@RequestMapping("/api/v1/medico")
public class MedicoApiController {
    @Autowired
    MedicoFacade medicoFacade;

    private static final Logger logger = LoggerFactory.getLogger(MedicoApiController.class);

    @DeleteMapping("/eliminar")
    public ResponseEntity<Void> eliminarEmpleado(@RequestBody Integer id) {
        medicoFacade.eliminarMedico(id);
        //Log para identificar que un medico ha sido eliminado
        logger.info("INFO: Un medico ha sido eliminado");

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<List<MedicoListaDto>> medicos() {
        List<Medico> medicos = medicoFacade.obtenerTodosMedicos();
        List<MedicoListaDto> medicosDto = new ArrayList<>();
        for (Medico medico : medicos) {
         MedicoListaDto medicoDto = new MedicoListaDto(medico.getIdMedico(),medico.getNombre(), medico.getApellidos(), 
         medico.getTelefono(), medico.getDireccion(), medico.getEspecialidad().getIdEspecialidad());
         medicosDto.add(medicoDto);
        }
        System.out.println("Médicos a enviar: " + medicosDto);
        return ResponseEntity.ok(medicosDto);
    }



}
