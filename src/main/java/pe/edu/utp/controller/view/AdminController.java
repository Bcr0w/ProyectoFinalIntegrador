package pe.edu.utp.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.edu.utp.dto.PerfilDto;
import pe.edu.utp.model.CitaMedica;
import pe.edu.utp.model.Empleado;
import pe.edu.utp.model.Medico;
import pe.edu.utp.security.CustomUserDetails;
import pe.edu.utp.service.facade.CitaMedicaFacade;
import pe.edu.utp.service.facade.EmpleadoFacade;
import pe.edu.utp.service.facade.MedicoFacade;

/*
 * Controlador de admin para gestione y responde las solicitudes de los usuarios. 
 * 
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    CitaMedicaFacade citaFacade;
    @Autowired
    MedicoFacade medicoFacade;
    @Autowired
    EmpleadoFacade empleadoFacade;

    /**
     * Muestra el dashboard del administrador.
     * 
     *@param model El modelo que almacenara los datos para que se muestren en la vista.
     * 
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<CitaMedica> citas = citaFacade.obtenerTodasCitasMedicas();
        List<Empleado> empleados = empleadoFacade.obtenerTodosEmpleados();
        List<Medico> medicos = medicoFacade.obtenerTodosMedicos();
        // Cantidad de los medicos y empleados
        int cantEmpl = empleados.size();
        int cantMed = medicos.size();
        model.addAttribute("numEmpleados", cantEmpl);
        model.addAttribute("numMedicos", cantMed);
        model.addAttribute("citas", citas);
        return "admin-home";
    }

    /*
     * Muestra la pagina de reportes del administrador.
     * 
     */

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/reportes")
    public String reportes() {
        return "admin-reportes";
    }

    /**
     * Muestra la pagina de reportes del administrador.
     * @model El modelo que almacenara los datos para ser mostrados en la vista.
     * @param authenticaction El objeto de autenticacion que contiene la informacion del usuario actual como sus roles,nombre,etc.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication) {
        // Obtener el usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Empleado empleado = empleadoFacade.buscarEmpleadoPorId(userDetails.getIdCargo());
        PerfilDto perfilDto = new PerfilDto(empleado.getNombre(), empleado.getApellidos(), empleado.getTelefono());
        model.addAttribute("perfilDto", perfilDto);
        return "perfil";
    }

}
