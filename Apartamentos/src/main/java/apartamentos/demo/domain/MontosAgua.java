
package apartamentos.demo.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data​
@Entity​
@Table(name="montos_agua")
public class MontosAgua implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Id​
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_monto")
    private Long idMonto;
    private long monto;

    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;
    
    @ManyToOne​
    @JoinColumn(name="id_agua")
    Agua idAgua;

    public MontosAgua() {
    }

    public MontosAgua(long monto, Agua idAgua, Date fechaRegistro) {
        this.monto = monto;
        this.idAgua = idAgua;
        this.fechaRegistro = fechaRegistro;
    }   
}
