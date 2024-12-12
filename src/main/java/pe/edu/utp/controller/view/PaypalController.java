package pe.edu.utp.controller.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.servlet.http.HttpSession;
import pe.edu.utp.model.CitaMedica;
import pe.edu.utp.model.CitaMedica.EstadoCita;
import pe.edu.utp.model.CitaMedica.PagoCita;
import pe.edu.utp.security.CustomUserDetails;
import pe.edu.utp.service.facade.CitaMedicaFacade;
import pe.edu.utp.service.facade.MedicoFacade;
import pe.edu.utp.service.facade.PacienteFacade;
import pe.edu.utp.service.facade.PaypalFacade;

@Controller
@RequestMapping("/paypal")
public class PaypalController {
    @Autowired
    private MedicoFacade medicoFacade;
    @Autowired
    private CitaMedicaFacade citaFacade;
    @Autowired
    private PacienteFacade pacienteFacade;

    private final PaypalFacade paypalFacade;

    //LOGS
    private static final Logger logger = LoggerFactory.getLogger(PaypalController.class);

    public PaypalController(PaypalFacade paypalFacade) {
        this.paypalFacade = paypalFacade;
    }

    @PostMapping("/payment/create")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody HashMap<String, Object> data,
            HttpSession session) {
        try {
            // Extraer datos del request
            String idMedicoStr = (String) data.get("idMedico");
            String fechaStr = (String) data.get("fechaConfirmada");
            String horaStr = (String) data.get("horaConfirmada");

            // Validar y guardar en sesión
            Integer idMedico = Integer.parseInt(idMedicoStr);
            session.setAttribute("idMedico", idMedicoStr);
            session.setAttribute("fecha", fechaStr);
            session.setAttribute("hora", horaStr);

            // Obtener precio
            Double precio = medicoFacade.buscarMedicoPorId(idMedico).getEspecialidad().getPrecio();
            if (precio == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Precio no disponible"));
            }

            // Crear el pago en PayPal
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String cancelUrl = baseUrl + "/paypal/payment/cancel";
            String successUrl = baseUrl + "/paypal/payment/success";

            Payment payment = paypalFacade.createPayment(
                    precio,
                    "USD",
                    "paypal",
                    "sale",
                    "Payment Description",
                    cancelUrl,
                    successUrl);

            // Buscar y devolver la URL de aprobación
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return ResponseEntity.ok(Map.of("redirectUrl", links.getHref()));
                }
            }

            return ResponseEntity.badRequest().body(Map.of("error", "No se encontró la URL de aprobación"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el pago"));
        }
    }

    @GetMapping("/payment/success")
    public String paymentSucces(@RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId, Authentication authentication, HttpSession session) {

        //verificar duplicidad
        if(session.getAttribute("idMedico") == null){
            return "redirect:/paciente/perfil";
        }
        // Obtener el usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String idMedicoStr = (String) session.getAttribute("idMedico");
        Integer idMedico = Integer.valueOf(idMedicoStr);
        String fechaStr = (String) session.getAttribute("fecha");
        String horaStr = (String) session.getAttribute("hora");
        LocalDate fecha = LocalDate.parse(fechaStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime hora = LocalTime.parse(horaStr, formatter);
        try {
            Payment payment = paypalFacade.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                CitaMedica cita = new CitaMedica(null, pacienteFacade.buscarPacientePorId(userDetails.getIdCargo()),
                        medicoFacade.buscarMedicoPorId(idMedico), fecha, hora, EstadoCita.EN_ESPERA, "",
                        PagoCita.TARJETA);
                citaFacade.registrarCitaMedica(cita);
               session.removeAttribute("idMedico");
               session.removeAttribute("fecha");
               session.removeAttribute("hora");

                 
                return "redirect:/paciente/perfil";
            }
        } catch (PayPalRESTException e) {
            //LOGGER
           logger.error("ERROR: Hubo un problema al momento de crear una orden en paypal", e);
            System.err.println("Error :" + e);
        }

        return "redirect:/paciente/perfil";
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        //LOGGER
        logger.info("INFO: Se cancelo un proceso de paga por paypal");
        return "redirect:/paciente/reservar";
    }

    @GetMapping("/payment/error")
    public String paymentError() {
        logger.error("ERROR: Hubo un problema al momento de procesar el pago");
        return "acceso-denegado";
    }

}
