package pe.edu.utp.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import pe.edu.utp.model.Usuario;
import pe.edu.utp.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    public void setUp() {
        // Inicializa los mocks
        MockitoAnnotations.openMocks(this);
        // Usamos el constructor con parámetros
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    public void testRegistrarUsuario() {
        // Datos de prueba
        String contrasena = "contrasena123";
        Usuario usuario = new Usuario();
        usuario.setCorreo("usuario@ejemplo.com");
        usuario.setContrasena(contrasena);

        // Comportamiento esperado de los mocks
        when(passwordEncoder.encode(contrasena)).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Ejecutar la prueba
        Usuario result = usuarioService.registrarUsuario(usuario);

        // Verificaciones
        assertNotNull(result);
        assertEquals("hashedPassword", result.getContrasena()); // Verifica que la contraseña fue encriptada
        verify(usuarioRepository, times(1)).save(usuario); // Verifica que el repositorio 'save' fue llamado una vez
    }

    @Test
    public void testBuscarPorCorreo() {
        // Datos de prueba
        String correo = "usuario@ejemplo.com";
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContrasena("hashedPassword");

        // Comportamiento esperado de los mocks
        when(usuarioRepository.findByCorreo(correo)).thenReturn(usuario);

        // Ejecutar la prueba
        Usuario result = usuarioService.buscarPorCorreo(correo);

        // Verificaciones
        assertNotNull(result);
        assertEquals(correo, result.getCorreo());
        verify(usuarioRepository, times(1)).findByCorreo(correo); // Verifica que el repositorio 'findByCorreo' fue llamado una vez
    }
    
    @Test
    public void testVerificarContrasena() {
        // Datos de prueba
        String contrasena = "contrasena123";
        String contrasenaEncriptada = "hashedPassword";

        // Comportamiento esperado de los mocks
        when(passwordEncoder.matches(contrasena, contrasenaEncriptada)).thenReturn(true);

        // Ejecutar la prueba
        boolean result = usuarioService.verificarContrasena(contrasena, contrasenaEncriptada);

        // Verificaciones
        assertTrue(result); // Verifica que la contraseña coincida con la encriptada
        verify(passwordEncoder, times(1)).matches(contrasena, contrasenaEncriptada); // Verifica que el método matches fue llamado una vez
    }

    @Test
    public void testSaveUsuario() {
        // Datos de prueba
        String contrasena = "contrasena123";
        Usuario usuario = new Usuario();
        usuario.setCorreo("usuario@ejemplo.com");
        usuario.setContrasena(contrasena);

        // Comportamiento esperado de los mocks
        when(passwordEncoder.encode(contrasena)).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Ejecutar la prueba
        usuarioService.saveUsuario(usuario);

        // Verificaciones
        verify(passwordEncoder, times(1)).encode(contrasena); // Verifica que la contraseña fue encriptada
        verify(usuarioRepository, times(1)).save(usuario); // Verifica que el repositorio 'save' fue llamado una vez
    }
}
