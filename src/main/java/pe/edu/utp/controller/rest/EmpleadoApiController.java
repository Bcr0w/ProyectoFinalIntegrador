package pe.edu.utp.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import org.springframework.http.HttpStatus;
import pe.edu.utp.dto.EmpleadoDto;
import pe.edu.utp.model.Empleado;
import pe.edu.utp.service.facade.EmpleadoFacade;

@RestController
@RequestMapping("/api/v1/empleado")
public class EmpleadoApiController {
    
    @Autowired
    EmpleadoFacade empleadoFacade;

    HashMap<String,Object> data;
    //LOGS
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoApiController.class);

    
   
   @GetMapping("/info/{id}")
   public ResponseEntity<Object> infoEmpleado(@PathVariable Integer id) {
    data = new HashMap<>();
    try{
      Empleado empleado = empleadoFacade.buscarEmpleadoPorId(id);
      EmpleadoDto empleadoEnviar = new EmpleadoDto(empleado.getNombre(),empleado.getApellidos(),empleado.getTelefono(),
      empleado.getDireccion(),empleado.getCargo(),empleado.getUsuario().getCorreo(),"NO VISIBLE",empleado.getUsuario().getRol().getIdRol());
      data.put("empleado",empleadoEnviar);
      data.put("message", "Empleado encontrado");
    return new ResponseEntity(data,HttpStatus.ACCEPTED);
    } catch(RuntimeException ex){
      data.put("message", ex.getMessage());
      return new ResponseEntity(data,HttpStatus.CONFLICT);
    }
    
   }

   @DeleteMapping("/eliminar")
public ResponseEntity<Void> eliminarEmpleado(@RequestBody Integer id) {
    empleadoFacade.eliminarEmpleado(id);
    logger.info("INFO: Se ha eliminado un empleado correctamente ");
    return ResponseEntity.noContent().build();
}

   


}
