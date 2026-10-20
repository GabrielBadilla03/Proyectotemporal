package apartamentos.demo.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data​
@Entity​
@Table(name="encargado")
public class Encargado implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Id​
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_encargado")
    private Long idEncargado;
    private String nombre;
    private String apellidos;
    private int cedula;
    private String fotoCedula;

    @ManyToOne​
    @JoinColumn(name="id_apartamento")
    Apartamento idApartamento;

    public Encargado() {
    }

    public Encargado(String nombre, String apellidos, int cedula, String fotoCedula, Apartamento idapartamento) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cedula = cedula;
        this.fotoCedula = fotoCedula;
        this.idApartamento = idapartamento;
    }
}
