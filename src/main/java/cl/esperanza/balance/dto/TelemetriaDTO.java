package cl.esperanza.balance.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetriaDTO {
    private LocalDate fecha;
    private double nivelEstanque;
}
