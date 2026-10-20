
package apartamentos.demo.domain;


import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name="apartamento")
public class Apartamento implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_apartamento")
    private Long idApartamento;
    private String ubicacion;
    private String descripcionDepartamento;
    
    @ManyToOne
    @JoinColumn(name="id_luz")
    private Luz idLuz;
    
    @ManyToOne
    @JoinColumn(name="id_agua")
    private Agua idAgua;
    
    @OneToMany
    @JoinColumn(name="id_apartamento")
    List<Encargado> encargado;

    public Apartamento() {
    }

    public Apartamento(String ubicacion, String descripcionDepartamento, Luz idLuz, Agua idAgua) {
        this.ubicacion = ubicacion;
        this.descripcionDepartamento = descripcionDepartamento;
        this.idLuz = idLuz;
        this.idAgua = idAgua;
    }

    @Override
    public String toString() {
        return "Apartamento{idApartamento=" + idApartamento + ", ubicacion='" + ubicacion + "', descripcionDepartamento='" + descripcionDepartamento + "'}";
    }
}
