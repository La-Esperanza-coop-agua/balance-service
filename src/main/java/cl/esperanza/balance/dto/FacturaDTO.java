package cl.esperanza.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private String runSocio;
    private String periodo;
    private double metrosCubicosFacturados;
}
