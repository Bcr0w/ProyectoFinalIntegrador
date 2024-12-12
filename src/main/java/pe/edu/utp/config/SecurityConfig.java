package pe.edu.utp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import pe.edu.utp.security.CustomAccessDeniedHandler;
import pe.edu.utp.security.CustomAuthenticationFailureHandler;
import pe.edu.utp.security.CustomUserDetailsService;

/**
 * Configuración de seguridad para la aplicación.
 * 
 * Esta clase se encarga de configurar las reglas de seguridad de la aplicación, como la autenticación,
 * autorización, manejo de accesos y la configuración de las URLs permitidas para diferentes roles de usuario.
 * 
 * Además, se habilitan características como el login personalizado, manejo de errores de autenticación 
 * y el manejo de la sesión de usuario al cerrar sesión.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	/**
     * Servicio de detalles de usuario personalizado para la autenticación.
     */
	@Autowired
	CustomUserDetailsService customUserDetailsServices;

	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	 /**
     * Constructor para inyectar el handler de acceso denegado.
     * 
     * @param customAccessDeniedHandler El handler para gestionar el acceso denegado.
     */
    @Autowired
	public SecurityConfig(CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }
	 /**
     * Bean para el codificador de contraseñas utilizando el algoritmo BCrypt.
     * 
     * @return Un objeto {@link PasswordEncoder} configurado con el algoritmo BCrypt.
     */

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
     * Configura el filtro de seguridad de HTTP para la aplicación.
     * 
     * Configura las reglas de autorización para las distintas rutas de la aplicación, 
     * como la autorización basada en roles y la configuración del formulario de inicio de sesión.
     * 
     * @param http El objeto {@link HttpSecurity} para configurar la seguridad de la aplicación.
     * @return Un objeto {@link SecurityFilterChain} que contiene las reglas de seguridad configuradas.
     * @throws Exception Si ocurre algún error durante la configuración de seguridad.
     */
	@SuppressWarnings("removal")
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.csrf().disable().authorizeHttpRequests()
		.requestMatchers("/autenticacion/**").permitAll()
		.requestMatchers("/**").permitAll()
		.requestMatchers("/admin/**").hasRole("ADMIN")
		.requestMatchers("/home").permitAll().and()	
		.formLogin()
		.loginPage("/usuario/login")
		.failureHandler(new CustomAuthenticationFailureHandler())
		.loginProcessingUrl("/usuario/login")
		.usernameParameter("correo")
		.passwordParameter("contraseña")
		.defaultSuccessUrl("/home", true).permitAll()
		.and()
		.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
		.and()
		.logout()
		.invalidateHttpSession(true)
	     .clearAuthentication(true)
	     .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	     .logoutSuccessUrl("/usuario/login?logout").permitAll();
		
		return http.build();
	}
	/**
     * Configura el servicio de detalles de usuario y el codificador de contraseñas para la autenticación global.
     * 
     * @param auth El objeto {@link AuthenticationManagerBuilder} para configurar la autenticación global.
     * @throws Exception Si ocurre un error en la configuración de la autenticación.
     */
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(customUserDetailsServices).passwordEncoder(passwordEncoder());
	}
}