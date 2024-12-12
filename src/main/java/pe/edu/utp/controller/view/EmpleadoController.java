package pe.edu.utp.controller.view;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import pe.edu.utp.dto.EmpleadoDto;
import pe.edu.utp.model.Empleado;
import pe.edu.utp.model.Rol;
import pe.edu.utp.model.Usuario;
import pe.edu.utp.service.facade.EmpleadoFacade;
import pe.edu.utp.service.facade.RolFacade;
import pe.edu.utp.service.facade.UsuarioFacade;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {
    @Autowired
    private EmpleadoFacade empleadoFacade;
    @Autowired
    private RolFacade rolFacade;
    @Autowired
    private UsuarioFacade usuarioFacade;
    //LOGS
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/listar")
    public String empleados(Model model) {
        List<Empleado> empleados = empleadoFacade.obtenerTodosEmpleados();
        EmpleadoDto empleadoDto = new EmpleadoDto();
        model.addAttribute("empleados", empleados);
        model.addAttribute("empleadoDto", empleadoDto);
        return "admin-empleados";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/crearEmpleado")
    public String crearEmpleado(@ModelAttribute EmpleadoDto empleadoDto) {
        Empleado empleado = new Empleado();
        Rol rol = rolFacade.findById(empleadoDto.getIdRol());
        Usuario usuario = new Usuario(null, empleadoDto.getCorreo(), empleadoDto.getContrasena(), rol);
        usuarioFacade.guardarUsuario(usuario);

        empleado.setNombre(empleadoDto.getNombre());
        empleado.setApellidos(empleadoDto.getApellidos());
        empleado.setCargo(empleadoDto.getCargo());
        empleado.setDireccion(empleadoDto.getDireccion());
        empleado.setTelefono(empleadoDto.getTelefono());
        empleado.setUsuario(usuario);

        empleadoFacade.registrarEmpleado(empleado);
        //LOG TIPO INFO
        logger.info("INFO: Se agrego un nuevo empleado");

        return "redirect:/empleado/listar";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/buscarEmpleado")
    public String buscarEmpleado(@RequestParam("nombre") String nombre, Model model){
        List<Empleado> empleados = empleadoFacade.buscarPorNombre(nombre);
        EmpleadoDto empleadoDto = new EmpleadoDto();
        model.addAttribute("empleados", empleados);
        model.addAttribute("empleadoDto", empleadoDto);
        return "admin-empleados";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public RedirectView eliminarEmpleado(@PathVariable Integer id){
    empleadoFacade.eliminarEmpleado(id);
    //logger tipo info
    logger.info("INFO: El empleado con el" + id + " ha sido eliminado");
    return new RedirectView("/empleado/listar");
    }
    

}
