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

import pe.edu.utp.model.Rol;
import pe.edu.utp.model.Rol.RolEnum;
import pe.edu.utp.repository.RolRepository;

class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarRol_DeberiaRegistrarYDevolverRol() {
        Rol rol = new Rol(1, RolEnum.ADMIN);
        when(rolRepository.save(rol)).thenReturn(rol);

        Rol resultado = rolService.registrarRol(rol);

        assertNotNull(resultado);
        assertEquals(RolEnum.ADMIN, resultado.getNombreRol());
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void obtenerTodosRoles_DeberiaDevolverListaDeRoles() {
        List<Rol> roles = Arrays.asList(
            new Rol(1, RolEnum.ADMIN),
            new Rol(2, RolEnum.MEDICO)
        );
        when(rolRepository.findAll()).thenReturn(roles);

        List<Rol> resultado = rolService.obtenerTodosRoles();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void buscarRolPorId_DeberiaDevolverRolSiExiste() {
        Rol rol = new Rol(1, RolEnum.ADMIN);
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));

        Rol resultado = rolService.buscarRolPorId(1);

        assertNotNull(resultado);
        assertEquals(RolEnum.ADMIN, resultado.getNombreRol());
        verify(rolRepository, times(1)).findById(1);
    }

    @Test
    void buscarRolPorId_DeberiaLanzarExcepcionSiNoExiste() {
        when(rolRepository.findById(1)).thenReturn(Optional.empty());

        Exception excepcion = assertThrows(RuntimeException.class, () -> rolService.buscarRolPorId(1));
        assertEquals("No se encontro el rol", excepcion.getMessage());
        verify(rolRepository, times(1)).findById(1);
    }

    @Test
    void buscarRolPorNombre_DeberiaDevolverRolSiExiste() {
        Rol rol = new Rol(1, RolEnum.MEDICO);
        when(rolRepository.findByNombreRol(RolEnum.MEDICO)).thenReturn(rol);

        Rol resultado = rolService.buscarRoLPorNombre(RolEnum.MEDICO);

        assertNotNull(resultado);
        assertEquals(RolEnum.MEDICO, resultado.getNombreRol());
        verify(rolRepository, times(1)).findByNombreRol(RolEnum.MEDICO);
    }

    @Test
    void actualizarRol_DeberiaActualizarYDevolverRolSiExiste() {
        Rol rol = new Rol(1, RolEnum.ADMIN);
        when(rolRepository.existsById(1)).thenReturn(true);
        when(rolRepository.save(rol)).thenReturn(rol);

        Rol resultado = rolService.actualizarRol(rol);

        assertNotNull(resultado);
        assertEquals(RolEnum.ADMIN, resultado.getNombreRol());
        verify(rolRepository, times(1)).existsById(1);
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void actualizarRol_DeberiaLanzarExcepcionSiNoExiste() {
        Rol rol = new Rol(1, RolEnum.ADMIN);
        when(rolRepository.existsById(1)).thenReturn(false);

        Exception excepcion = assertThrows(RuntimeException.class, () -> rolService.actualizarRol(rol));
        assertEquals("Rol no encontrado", excepcion.getMessage());
        verify(rolRepository, times(1)).existsById(1);
        verify(rolRepository, never()).save(rol);
    }

    @Test
    void eliminarRol_DeberiaEliminarRolSiExiste() {
        when(rolRepository.existsById(1)).thenReturn(true);

        rolService.eliminarRol(1);

        verify(rolRepository, times(1)).existsById(1);
        verify(rolRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarRol_DeberiaLanzarExcepcionSiNoExiste() {
        when(rolRepository.existsById(1)).thenReturn(false);

        Exception excepcion = assertThrows(RuntimeException.class, () -> rolService.eliminarRol(1));
        assertEquals("Rol no encontrado", excepcion.getMessage());
        verify(rolRepository, times(1)).existsById(1);
        verify(rolRepository, never()).deleteById(1);
    }
}
