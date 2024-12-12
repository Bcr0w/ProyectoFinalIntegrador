package pe.edu.utp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.utp.model.Medico;
import pe.edu.utp.repository.MedicoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private MedicoService medicoService;

    private Medico medico;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        medico = new Medico(1, null, null, "Juan", "Perez", "123456789", "Calle Falsa 123");
    }

    @Test
    void registrarMedico() {
        when(medicoRepository.save(medico)).thenReturn(medico);
        Medico result = medicoService.registrarMedico(medico);
        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        verify(medicoRepository, times(1)).save(medico);
    }

    @Test
    void obtenerTodosMedicos() {
        List<Medico> medicos = Arrays.asList(medico, new Medico(2, null, null, "Ana", "Lopez", "987654321", "Avenida Siempre Viva"));
        when(medicoRepository.findAll()).thenReturn(medicos);

        List<Medico> result = medicoService.obtenerTodosMedicos();
        assertEquals(2, result.size());
        verify(medicoRepository, times(1)).findAll();
    }

    @Test
    void buscarMedicos() {
        when(medicoRepository.findByNombreContaining("Juan")).thenReturn(List.of(medico));
        List<Medico> result = medicoService.buscarMedicos("Juan");
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getNombre());
        verify(medicoRepository, times(1)).findByNombreContaining("Juan");
    }

    @Test
    void buscarPorUsuario() {
        when(medicoRepository.findByUsuario_IdUsuario(1)).thenReturn(medico);
        Medico result = medicoService.buscarPorUsuario(1);
        assertNotNull(result);
        assertEquals(1, result.getIdMedico());
        verify(medicoRepository, times(1)).findByUsuario_IdUsuario(1);
    }

    @Test
    void buscarMedicoPorId() {
        when(medicoRepository.findById(1)).thenReturn(Optional.of(medico));
        Medico result = medicoService.buscarMedicoPorId(1);
        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        verify(medicoRepository, times(1)).findById(1);
    }

    @Test
    void buscarMedicoPorIdNotFound() {
        when(medicoRepository.findById(1)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> medicoService.buscarMedicoPorId(1));
        assertEquals("no se encontro medico", exception.getMessage());
        verify(medicoRepository, times(1)).findById(1);
    }

    @Test
    void actualizarMedico() {
        when(medicoRepository.existsById(medico.getIdMedico())).thenReturn(true);
        when(medicoRepository.save(medico)).thenReturn(medico);

        Medico result = medicoService.actualizarMedico(medico);
        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        verify(medicoRepository, times(1)).existsById(medico.getIdMedico());
        verify(medicoRepository, times(1)).save(medico);
    }

    @Test
    void actualizarMedicoNotFound() {
        when(medicoRepository.existsById(medico.getIdMedico())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> medicoService.actualizarMedico(medico));
        assertEquals("Médico no encontrado", exception.getMessage());
        verify(medicoRepository, times(1)).existsById(medico.getIdMedico());
        verify(medicoRepository, never()).save(medico);
    }

    @Test
    void eliminarMedico() {
        when(medicoRepository.existsById(1)).thenReturn(true);
        doNothing().when(medicoRepository).deleteById(1);

        medicoService.eliminarMedico(1);

        verify(medicoRepository, times(1)).existsById(1);
        verify(medicoRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarMedicoNotFound() {
        when(medicoRepository.existsById(1)).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> medicoService.eliminarMedico(1));
        assertEquals("Médico no encontrado", exception.getMessage());
        verify(medicoRepository, times(1)).existsById(1);
        verify(medicoRepository, never()).deleteById(1);
    }
}
