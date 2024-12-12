package pe.edu.utp.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.edu.utp.dto.HorarioDto;
import pe.edu.utp.service.facade.HorarioMedicoFacade;

@Controller
@RequestMapping("/horario")
public class HorarioMedicoController {
    @Autowired
    HorarioMedicoFacade horarioFacade;
    
    
    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @GetMapping("/crear")
    public String crearHorario(Model model){
     HorarioDto horario = new HorarioDto();
     model.addAttribute("horarioDto", horario);
     return "medico-disponibilidad-crear";
    }

    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id){
    horarioFacade.eliminarHorarioMedico(id);
    return "redirect:/medico/disponibilidad";
    }  
}
