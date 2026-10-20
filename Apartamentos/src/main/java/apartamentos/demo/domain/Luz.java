package apartamentos.demo.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name="luz")
public class Luz implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id_luz")
    private Long idLuz;

    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Temporal(TemporalType.DATE)
    private Date fechaPagar;

    @Temporal(TemporalType.DATE)
    private Date fechaInicioPago;
    
    private String titular;
    
    private Boolean pagar;

    @OneToMany
    @JoinColumn(name="id_luz")
    List<Apartamento> idApartamento;

    @OneToMany
    @JoinColumn(name="id_luz")
    private List<MontosLuz> montosLuz;

    public Luz() {
    }

    public Luz(Date fechaVencimiento, Date fechaPagar, Date fechaInicioPago, String titular, Boolean pagar) {
        this.fechaVencimiento = fechaVencimiento;
        this.fechaPagar = fechaPagar;
        this.fechaInicioPago = fechaInicioPago;
        this.titular = titular;
        this.pagar = pagar;
    }
}
