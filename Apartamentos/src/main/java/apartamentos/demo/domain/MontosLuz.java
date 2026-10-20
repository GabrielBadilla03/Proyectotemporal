/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data​
@Entity​
@Table(name="montos_luz")
public class MontosLuz implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id​
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_monto")
    private Long idMonto;
    private long monto;
    
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    @ManyToOne​
    @JoinColumn(name="id_luz")
    Luz idLuz;

    public MontosLuz() {
    }

    public MontosLuz(long monto, Luz idLuz, Date fechaRegistro) {
        this.monto = monto;
        this.idLuz = idLuz;
        this.fechaRegistro = fechaRegistro;
    }    
}
