package cl.esperanza.balance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String periodo; 

    @Column(nullable = false)
    private double aguaProducidaM3;

    @Column(nullable = false)
    private double aguaConsumidaM3;

    @Column(nullable = false)
    private double porcentajePerdida;

    @Column(nullable = false)
    private String estadoAlerta;
}