package pe.edu.utp.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;

/**
 * Clase de configuración para integrar con PayPal.
 * Esta clase se encarga de la configuración necesaria para crear un contexto
 * de API que permita interactuar con los servicios de PayPal.
 * Los valores necesarios para la configuración (cliente ID, cliente secret y modo)
 * se obtienen de un archivo de propiedades.
 * 
 * @author Brandy Sinche
 * @version 1.0
 */
@Configuration
public class PaypalConfig {
  
    /**
     * El cliente ID de PayPal utilizado para la autenticación en la API de PayPal.
     * Este valor se obtiene desde el archivo de propiedades.
     */
    @Value("${paypal.cliente-id}")
    private String clientId;

    /**
     * El cliente secret de PayPal utilizado para la autenticación en la API de PayPal.
     * Este valor se obtiene desde el archivo de propiedades.
     */
    @Value("${paypal.cliente-secret}")
    private String clientSecret;

    /**
     * El modo de operación de PayPal (sandbox o live).
     * Este valor se obtiene desde el archivo de propiedades.
     */
    @Value("${paypal.mode}")
    private String mode;

    /**
     * Crea y configura un contexto de API utilizando el cliente ID, el cliente secret
     * y el modo de operación definidos en las propiedades.
     * 
     * @return Un objeto APIContext configurado con los valores necesarios para
     *         realizar solicitudes a la API de PayPal.
     */
    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    } 
}
