package cl.esperanza.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {  
    private double porcentajePerdida;
    private boolean alerta;
    private String mensaje;
}
