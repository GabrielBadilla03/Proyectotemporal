
package apartamentos.demo.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data​
@Entity​
@Table(name="agua")
public class Agua implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Id​
    @Column(name="id_agua")
    private Long idAgua;
    
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Temporal(TemporalType.DATE)
    private Date fechaPagar;

    @Temporal(TemporalType.DATE)
    private Date fechaInicioPago;
    
    private String titular;
    
    private Boolean pagar;

    @OneToMany
    @JoinColumn(name="id_agua")
    List<Apartamento> idApartamento;

    @OneToMany
    @JoinColumn(name="id_agua")
    List<MontosAgua> montosAgua;
    
    public Agua() {
    }

    public Agua(Date fechaVencimiento, Date fechaPagar, Date fechaInicioPago, String titular, Boolean pagar) {
        this.fechaVencimiento = fechaVencimiento;
        this.fechaPagar = fechaPagar;
        this.fechaInicioPago = fechaInicioPago;
        this.titular = titular;
        this.pagar = pagar;
    }
    
    @Override
    public String toString() {
        return "Agua{idAgua=" + idAgua + ", fechaVencimiento=" + fechaVencimiento + ", fechaPagar=" + fechaPagar + ", fechaInicioPago=" + fechaInicioPago + ", titular=" + titular + "}";
    }
}
