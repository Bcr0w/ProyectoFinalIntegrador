package pe.edu.utp.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import pe.edu.utp.model.Especialidad;
import pe.edu.utp.repository.EspecialidadRepository;


import java.util.List;
import java.util.Optional;

public class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository especialidadRepository;  // Mock del repositorio

    @InjectMocks
    private EspecialidadService especialidadService;  // Servicio que estamos probando

    private Especialidad especialidadMock;  // Un mock de Especialidad para las pruebas

    // Configuración de la prueba
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializa los mocks

        // Inicialización del mock de Especialidad
        especialidadMock = new Especialidad();
        especialidadMock.setIdEspecialidad(1);
        especialidadMock.setNombreEspecialidad("Cardiología");
    }

    // Método de prueba para registrar una especialidad
    @Test
    public void testRegistrarEspecialidad() {
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidadMock);  // Simulamos el guardado

        Especialidad resultado = especialidadService.registrarEspecialidad(especialidadMock);  // Llamamos al método

        // Verificamos que el resultado no sea nulo y que los datos sean correctos
        assertNotNull(resultado);
        assertEquals("Cardiología", resultado.getNombreEspecialidad());
    }

    // Método de prueba para obtener todas las especialidades
    @Test
    public void testObtenerTodasEspecialidades() {
        when(especialidadRepository.findAll()).thenReturn(List.of(especialidadMock));  // Simulamos la lista de especialidades

        List<Especialidad> especialidades = especialidadService.obtenerTodasEspecialidades();  // Llamamos al método

        // Verificamos que la lista no esté vacía y contenga la especialidad esperada
        assertNotNull(especialidades);
        assertFalse(especialidades.isEmpty());
        assertEquals(1, especialidades.size());
        assertEquals("Cardiología", especialidades.get(0).getNombreEspecialidad());
    }

    // Método de prueba para buscar una especialidad por ID
    @Test
    public void testBuscarEspecialidadPorId() {
        when(especialidadRepository.findById(1)).thenReturn(Optional.of(especialidadMock));  // Simulamos la búsqueda

        Especialidad resultado = especialidadService.buscarEspecialidadPorId(1);  // Llamamos al método

        // Verificamos que la especialidad encontrada no sea nula y tenga el ID esperado
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdEspecialidad());
        assertEquals("Cardiología", resultado.getNombreEspecialidad());
    }

    // Método de prueba para actualizar una especialidad
    @Test
    public void testActualizarEspecialidad() {
        when(especialidadRepository.existsById(1)).thenReturn(true);  // Simulamos que la especialidad existe
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidadMock);  // Simulamos el guardado actualizado

        Especialidad resultado = especialidadService.actualizarEspecialidad(especialidadMock);  // Llamamos al método

        // Verificamos que la especialidad actualizada no sea nula y que los valores sean correctos
        assertNotNull(resultado);
        assertEquals("Cardiología", resultado.getNombreEspecialidad());
    }

    // Método de prueba para eliminar una especialidad
    @Test
    public void testEliminarEspecialidad() {
        when(especialidadRepository.existsById(1)).thenReturn(true);  // Simulamos que la especialidad existe

        especialidadService.eliminarEspecialidad(1);  // Llamamos al método

        // Verificamos que el método deleteById se haya llamado correctamente
        verify(especialidadRepository, times(1)).deleteById(1);
    }

    // Método de prueba para eliminar una especialidad que no existe
    @Test
    public void testEliminarEspecialidadNoExistente() {
        when(especialidadRepository.existsById(1)).thenReturn(false);  // Simulamos que la especialidad no existe

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            especialidadService.eliminarEspecialidad(1);  // Llamamos al método
        });

        // Verificamos que se haya lanzado la excepción con el mensaje adecuado
        assertEquals("Especialidad no encontrada", exception.getMessage());
    }
}