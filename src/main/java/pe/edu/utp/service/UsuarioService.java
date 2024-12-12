package pe.edu.utp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.utp.model.Usuario;
import pe.edu.utp.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Método para registrar un nuevo usuario
    public Usuario registrarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardarla
        usuario.setContrasena(encriptarContrasena(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    // Método para encriptar la contraseña
    public String encriptarContrasena(String contrasena) {
        return passwordEncoder.encode(contrasena);
    }

    // Método para verificar la contraseña
    public boolean verificarContrasena(String contrasena, String contrasenaEncriptada) {
        return passwordEncoder.matches(contrasena, contrasenaEncriptada);
    }

    // Método para buscar un usuario por correo
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // Otros métodos de negocio relacionados con Usuario
    public void saveUsuario(Usuario usuario) {
    	usuario.setContrasena(encriptarContrasena(usuario.getContrasena()));
    	usuarioRepository.save(usuario);
    }
}