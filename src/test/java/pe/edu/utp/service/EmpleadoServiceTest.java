package pe.edu.utp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pe.edu.utp.model.Empleado;
import pe.edu.utp.model.Usuario;
import pe.edu.utp.repository.EmpleadoRepository;

class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        empleado = new Empleado(1, usuario, "Juan", "Perez", "123456789", "Calle 123", "Gerente");
    }

    @Test
    void testRegistrarEmpleado() {
        when(empleadoRepository.save(empleado)).thenReturn(empleado);

        Empleado resultado = empleadoService.registrarEmpleado(empleado);

        assertNotNull(resultado);
        assertEquals(empleado.getNombre(), resultado.getNombre());
        verify(empleadoRepository, times(1)).save(empleado);
    }

    @Test
    void testObtenerTodosEmpleados() {
        List<Empleado> empleados = Arrays.asList(empleado);
        when(empleadoRepository.findAll()).thenReturn(empleados);

        List<Empleado> resultado = empleadoService.obtenerTodosEmpleados();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorUsuario() {
        when(empleadoRepository.findByUsuario_IdUsuario(1)).thenReturn(empleado);

        Empleado resultado = empleadoService.buscarPorUsuario(1);

        assertNotNull(resultado);
        assertEquals(empleado.getNombre(), resultado.getNombre());
        verify(empleadoRepository, times(1)).findByUsuario_IdUsuario(1);
    }

    @Test
    void testBuscarPorNombre() {
        List<Empleado> empleados = Arrays.asList(empleado);
        when(empleadoRepository.findByNombreContaining("Juan")).thenReturn(empleados);

        List<Empleado> resultado = empleadoService.buscarPorNombre("Juan");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(empleadoRepository, times(1)).findByNombreContaining("Juan");
    }

    @Test
    void testBuscarEmpleadoPorId() {
        when(empleadoRepository.findById(1)).thenReturn(Optional.of(empleado));

        Empleado resultado = empleadoService.buscarEmpleadoPorId(1);

        assertNotNull(resultado);
        assertEquals(empleado.getNombre(), resultado.getNombre());
        verify(empleadoRepository, times(1)).findById(1);
    }

    @Test
    void testActualizarEmpleado() {
        when(empleadoRepository.existsById(empleado.getIdEmpleado())).thenReturn(true);
        when(empleadoRepository.save(empleado)).thenReturn(empleado);

        Empleado resultado = empleadoService.actualizarEmpleado(empleado);

        assertNotNull(resultado);
        assertEquals(empleado.getCargo(), resultado.getCargo());
        verify(empleadoRepository, times(1)).existsById(empleado.getIdEmpleado());
        verify(empleadoRepository, times(1)).save(empleado);
    }

    @Test
    void testEliminarEmpleado() {
        when(empleadoRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> empleadoService.eliminarEmpleado(1));
        verify(empleadoRepository, times(1)).existsById(1);
        verify(empleadoRepository, times(1)).deleteById(1);
    }

    @Test
    void testEliminarEmpleadoNoEncontrado() {
        when(empleadoRepository.existsById(1)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> empleadoService.eliminarEmpleado(1));
        assertEquals("Empleado no encontrado", exception.getMessage());
        verify(empleadoRepository, times(1)).existsById(1);
        verify(empleadoRepository, never()).deleteById(1);
    }
}
