package pe.edu.utp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.utp.model.CitaMedica;
import pe.edu.utp.model.Medico;
import pe.edu.utp.model.Paciente;
import pe.edu.utp.repository.CitaMedicaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository citaMedicaRepository;

    @InjectMocks
    private CitaMedicaService citaMedicaService;

    private CitaMedica citaMedica;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Paciente paciente = new Paciente();
        Medico medico = new Medico();
        citaMedica = new CitaMedica(1, paciente, medico, LocalDate.now(), LocalTime.now(), CitaMedica.EstadoCita.CONFIRMADA, "Consulta general", CitaMedica.PagoCita.TARJETA);
    }

    @Test
    void registrarCitaMedica() {
        when(citaMedicaRepository.save(citaMedica)).thenReturn(citaMedica);
        CitaMedica result = citaMedicaService.registrarCitaMedica(citaMedica);
        assertNotNull(result);
        assertEquals(CitaMedica.EstadoCita.CONFIRMADA, result.getEstado());
        verify(citaMedicaRepository, times(1)).save(citaMedica);
    }

    @Test
    void obtenerTodasCitasMedicas() {
        List<CitaMedica> citas = Arrays.asList(citaMedica, new CitaMedica(2, new Paciente(), new Medico(), LocalDate.now(), LocalTime.now(), CitaMedica.EstadoCita.EN_ESPERA, "Revision", CitaMedica.PagoCita.EFECTIVO));
        when(citaMedicaRepository.findAll()).thenReturn(citas);

        List<CitaMedica> result = citaMedicaService.obtenerTodasCitasMedicas();
        assertEquals(2, result.size());
        verify(citaMedicaRepository, times(1)).findAll();
    }

    @Test
    void obtenerCitasPorPaciente() {
        when(citaMedicaRepository.findByPaciente(1)).thenReturn(List.of(citaMedica));
        List<CitaMedica> result = citaMedicaService.obtenerCitasPorPaciente(1);
        assertEquals(1, result.size());
        assertEquals(citaMedica.getIdCita(), result.get(0).getIdCita());
        verify(citaMedicaRepository, times(1)).findByPaciente(1);
    }

    @Test
    void obtenerCitasPorMedicos() {
        when(citaMedicaRepository.findByMedicoAndEstado(1, CitaMedica.EstadoCita.CONFIRMADA)).thenReturn(List.of(citaMedica));
        List<CitaMedica> result = citaMedicaService.obtenerCitasPorMedicos(1, CitaMedica.EstadoCita.CONFIRMADA);
        assertEquals(1, result.size());
        assertEquals(CitaMedica.EstadoCita.CONFIRMADA, result.get(0).getEstado());
        verify(citaMedicaRepository, times(1)).findByMedicoAndEstado(1, CitaMedica.EstadoCita.CONFIRMADA);
    }

    @Test
    void buscarCitaMedicaPorId() {
        when(citaMedicaRepository.findById(1)).thenReturn(Optional.of(citaMedica));
        CitaMedica result = citaMedicaService.buscarCitaMedicaPorId(1);
        assertNotNull(result);
        assertEquals(1, result.getIdCita());
        verify(citaMedicaRepository, times(1)).findById(1);
    }

    @Test
    void buscarCitaMedicaPorIdNotFound() {
        when(citaMedicaRepository.findById(1)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> citaMedicaService.buscarCitaMedicaPorId(1));
        assertEquals("No se encontro", exception.getMessage());
        verify(citaMedicaRepository, times(1)).findById(1);
    }

    @Test
    void actualizarCitaMedica() {
        when(citaMedicaRepository.existsById(citaMedica.getIdCita())).thenReturn(true);
        when(citaMedicaRepository.save(citaMedica)).thenReturn(citaMedica);

        CitaMedica result = citaMedicaService.actualizarCitaMedica(citaMedica);
        assertNotNull(result);
        assertEquals("Consulta general", result.getDetalle());
        verify(citaMedicaRepository, times(1)).existsById(citaMedica.getIdCita());
        verify(citaMedicaRepository, times(1)).save(citaMedica);
    }

    @Test
    void actualizarCitaMedicaNotFound() {
        when(citaMedicaRepository.existsById(citaMedica.getIdCita())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> citaMedicaService.actualizarCitaMedica(citaMedica));
        assertEquals("Cita médica no encontrada", exception.getMessage());
        verify(citaMedicaRepository, times(1)).existsById(citaMedica.getIdCita());
        verify(citaMedicaRepository, never()).save(citaMedica);
    }

    @Test
    void eliminarCitaMedica() {
        when(citaMedicaRepository.existsById(1)).thenReturn(true);
        doNothing().when(citaMedicaRepository).deleteById(1);

        citaMedicaService.eliminarCitaMedica(1);

        verify(citaMedicaRepository, times(1)).existsById(1);
        verify(citaMedicaRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarCitaMedicaNotFound() {
        when(citaMedicaRepository.existsById(1)).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> citaMedicaService.eliminarCitaMedica(1));
        assertEquals("Cita médica no encontrada", exception.getMessage());
        verify(citaMedicaRepository, times(1)).existsById(1);
        verify(citaMedicaRepository, never()).deleteById(1);
    }
}
