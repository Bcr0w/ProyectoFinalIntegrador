package pe.edu.utp.controller.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.utp.dto.registroDto;
import pe.edu.utp.model.Paciente;
import pe.edu.utp.model.Rol;
import pe.edu.utp.model.Usuario;
import pe.edu.utp.model.Rol.RolEnum;
import pe.edu.utp.service.facade.EmpleadoFacade;
import pe.edu.utp.service.facade.MedicoFacade;
import pe.edu.utp.service.facade.PacienteFacade;
import pe.edu.utp.service.facade.RolFacade;
import pe.edu.utp.service.facade.UsuarioFacade;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    UsuarioFacade facade;
    @Autowired
    RolFacade rolFacade;
    @Autowired
    PacienteFacade pacienteFacade;
    @Autowired
    EmpleadoFacade empleadoFacade;
    @Autowired
    MedicoFacade medicoFacade;
    //LOGS
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

  @GetMapping("/login")
  public String iniciar(@RequestParam(value = "error", required = false) String error,Model model) {
    if (error != null) {
      model.addAttribute("error", "Las credenciales son incorrectas");
  }
     return "iniciarSesion";
  }
  
  @GetMapping("/registrarse")
 public String registrarse(Model model) {
    model.addAttribute("registroDto", new registroDto());
    return "registrarse";
 }

 @PostMapping("/crear")
  public String crear(@ModelAttribute registroDto registroDTO, Model model){
	//Validar que las contraseñas coincidan
    if (!registroDTO.getContrasena().equals(registroDTO.getConfirmarContrasena())) {
        model.addAttribute("error", "Las contraseñas no coinciden.");
        return "registrarse"; // Redirigir al formulario de registro
    }

    // Crear objeto Usuario
    Rol rol = rolFacade.getRol(RolEnum.PACIENTE); // el rol que desees asignar
    Usuario usuario = new Usuario(null, registroDTO.getCorreo(), registroDTO.getContrasena(), rol);
    facade.guardarUsuario(usuario); // Guardar el usuario en la base de datos

    // Crear objeto Paciente
    Paciente paciente = new Paciente();
    paciente.setUsuario(usuario); // Establecer la relación
    paciente.setNombre(registroDTO.getNombre());
    paciente.setApellidos(registroDTO.getApellidos());
    paciente.setTelefono(registroDTO.getTelefono());
    paciente.setDni(registroDTO.getDni());
    paciente.setFechaNacimiento(registroDTO.getFechaNacimiento());
    
    pacienteFacade.registrarPaciente(paciente); // Guardar el paciente en la base de datos

    //LOGGER
    logger.info("INFO: Se creo un nuevo paciente");

    return "redirect:/usuario/login"; // Redirigir a la página de inicio de sesión
  }

}
