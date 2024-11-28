package pe.edu.utp.model;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Especialidad")
public class Especialidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEspecialidad;

    @Column(name = "nombre_especialidad", nullable = false, length = 100)
    private String nombreEspecialidad;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    public Especialidad() {
		// TODO Auto-generated constructor stub
	}

	public Especialidad(Integer idEspecialidad, String nombreEspecialidad, String descripcion, Double precio) {
		super();
		this.idEspecialidad = idEspecialidad;
		this.nombreEspecialidad = nombreEspecialidad;
		this.descripcion = descripcion;
		this.precio = precio;
	}

	public Integer getIdEspecialidad() {
		return idEspecialidad;
	}

	public void setIdEspecialidad(Integer idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}

	public String getNombreEspecialidad() {
		return nombreEspecialidad;
	}

	public void setNombreEspecialidad(String nombreEspecialidad) {
		this.nombreEspecialidad = nombreEspecialidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}
    
	
}