package cl.esperanza.balance.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Integer idBalance;

    @Column(nullable = false, unique = true)
    private LocalDate periodo; 

    @Column(nullable = false)
    private double aguaProducidaM3;

    @Column(nullable = false)
    private double aguaConsumidaM3;

    @Column(nullable = false)
    private double porcentajePerdida;

    @Column(nullable = false)
    private String estadoAlerta;
}