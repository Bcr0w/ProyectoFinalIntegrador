package pe.edu.utp.controller.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.utp.dto.CitaDto;
import pe.edu.utp.dto.MedicoDto;
import pe.edu.utp.dto.PerfilDto;
import pe.edu.utp.model.CitaMedica;
import pe.edu.utp.model.Especialidad;
import pe.edu.utp.model.HorarioMedico;
import pe.edu.utp.model.Medico;
import pe.edu.utp.model.Rol;
import pe.edu.utp.model.Usuario;
import pe.edu.utp.model.CitaMedica.EstadoCita;
import pe.edu.utp.security.CustomUserDetails;
import pe.edu.utp.service.facade.CitaMedicaFacade;
import pe.edu.utp.service.facade.EspecialidadFacade;
import pe.edu.utp.service.facade.HorarioMedicoFacade;
import pe.edu.utp.service.facade.MedicoFacade;
import pe.edu.utp.service.facade.RolFacade;
import pe.edu.utp.service.facade.UsuarioFacade;

@Controller
@RequestMapping("/medico")
public class MedicoController {
    @Autowired
    MedicoFacade medicoFacade;
    @Autowired
    EspecialidadFacade especialidadFacade;
    @Autowired
    RolFacade rolFacade;
    @Autowired
    CitaMedicaFacade citaFacade;
    @Autowired
    HorarioMedicoFacade horarioFacade;

    @Autowired
    UsuarioFacade usuarioFacade;

    //LOGS
    private static final Logger logger = LoggerFactory.getLogger(MedicoController.class);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/listar")
    public String medicos(Model model) {
        List<Medico> medicos = medicoFacade.obtenerTodosMedicos();
        MedicoDto medicoDto = new MedicoDto();
        List<Especialidad> especialidades = especialidadFacade.obtenerTodasEspecialidades();
        model.addAttribute("medicos", medicos);
        model.addAttribute("medicoDto", medicoDto);
        model.addAttribute("especialidades", especialidades);
        return "admin-medicos";
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/crear")
    public String crearMedico(@ModelAttribute MedicoDto medicoDto) {
        Medico medico = new Medico();
        Rol rol = rolFacade.findById(medicoDto.getIdRol());
        Especialidad especialidad = especialidadFacade.buscarEspecialidadPorId(medicoDto.getIdEspecialidad());
        Usuario usuario = new Usuario(null, medicoDto.getCorreo(), medicoDto.getContrasena(), rol);
        usuarioFacade.guardarUsuario(usuario);

        medico.setNombre(medicoDto.getNombre());
        medico.setApellidos(medicoDto.getApellidos());
        medico.setEspecialidad(especialidad);
        medico.setDireccion(medicoDto.getDireccion());
        medico.setTelefono(medicoDto.getTelefono());
        medico.setUsuario(usuario);
        

        medicoFacade.registrarMedico(medico);

        //LOGGER 
        logger.info("INFO: Se creo un nuevo medico");
        return "redirect:/medico/listar";
    }
  
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/buscar")
    public String buscarMedico(@RequestParam("nombre") String nombre, Model model) {
        List<Medico> medicos = medicoFacade.buscarMedicos(nombre);
        MedicoDto medicoDto = new MedicoDto();
        List<Especialidad> especialidades = especialidadFacade.obtenerTodasEspecialidades();
        model.addAttribute("medicos", medicos);
        model.addAttribute("medicoDto", medicoDto);
        model.addAttribute("especialidades", especialidades);
        return "admin-medicos";

    }

    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @GetMapping("/home")
    public String dashboard(Model model){
      List<CitaMedica> citasMedicas = citaFacade.obtenerTodasCitasMedicas();
      List<CitaDto> citas = new ArrayList<>();
      for (CitaMedica cita : citasMedicas) {
        citas.add(new CitaDto(cita.getIdCita(),cita.getPaciente().getNombre(),cita.getMedico().getNombre(),cita.getFecha(),cita.getHora(),cita.getEstado().name(),cita.getDetalle(),cita.getPago().name()));
      }
       model.addAttribute("citas", citas);
       return "medico-home";
    }

    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @GetMapping("/disponibilidad")
    public String disponibilidad(Model model,Authentication authentication){
    // Obtener el usuario autenticado
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    List<HorarioMedico> horarios = horarioFacade.obtenerHorariosPorMedico(userDetails.getIdCargo());
    model.addAttribute("horarios", horarios);
    return "medico-disponibilidad";
    }

    /*buscar disponibilidad por fecha */
    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @PostMapping("/disponibilidad")
    public String disponibilidad(@RequestParam("fecha") LocalDate fecha,Model model,Authentication authentication){
    // Obtener el usuario autenticado
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    List<HorarioMedico> horarios = horarioFacade.obtenerHorariosPorMedico(userDetails.getIdCargo());
    horarios.removeIf(horario -> !horario.getFecha().equals(fecha));
    model.addAttribute("horarios", horarios);
    return "medico-disponibilidad";
    }

    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @GetMapping("/citasRealizadas")
    public String citasRealizadas(Model model, Authentication authentication){
      // Obtener el usuario autenticado
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    List<CitaMedica> citas = citaFacade.obtenerCitasPorMedicos(userDetails.getIdCargo(),EstadoCita.CONFIRMADA);
    model.addAttribute("citas", citas);
    return "medico-citas-realizadas";
    }

    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @GetMapping("/citasPendientes")
    public String citasPendientes(Model model, Authentication authentication){
      // Obtener el usuario autenticado
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    List<CitaMedica> citas = citaFacade.obtenerCitasPorMedicos(userDetails.getIdCargo(),EstadoCita.EN_ESPERA);
    model.addAttribute("citas", citas);
    return "medico-citas-pendientes";
    }

    /*metodo para buscar segun la fecha */
    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @PostMapping("/citasRealizadas")
    public String citasRealizadas(@RequestParam("fecha") LocalDate fecha ,Model model, Authentication authentication){
    // Obtener el usuario autenticado
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    List<CitaMedica> citas = citaFacade.obtenerCitasPorMedicos(userDetails.getIdCargo(),EstadoCita.CONFIRMADA);
    //Filtrar las fechas
    citas.removeIf(cita -> !cita.getFecha().equals(fecha));
    model.addAttribute("citas", citas);
    return "medico-citas-realizadas";
    }

    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @PostMapping("/citasPendientes")
    public String citasPendientes(@RequestParam("fecha") LocalDate fecha,Model model, Authentication authentication){
      // Obtener el usuario autenticado
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    List<CitaMedica> citas = citaFacade.obtenerCitasPorMedicos(userDetails.getIdCargo(),EstadoCita.EN_ESPERA);
    //Filtrar las fechas
    citas.removeIf(cita -> !cita.getFecha().equals(fecha));
    model.addAttribute("citas", citas);
    return "medico-citas-pendientes";
    }

    @PreAuthorize("hasRole('ROLE_MEDICO')")
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication){
      // Obtener el usuario autenticado
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Medico medico = medicoFacade.buscarMedicoPorId(userDetails.getIdCargo());
    PerfilDto perfilDto = new PerfilDto(medico.getNombre(),medico.getApellidos(),medico.getTelefono());
    model.addAttribute("perfilDto", perfilDto);
    return "perfil";
    }


    

}
