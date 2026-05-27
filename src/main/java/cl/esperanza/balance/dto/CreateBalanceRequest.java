package cl.esperanza.balance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import cl.esperanza.balance.model.Balance;

public record CreateBalanceRequest(
    @NotBlank(message = "El periodo no puede estar vacío (Ej: 2026-05)")
    String periodo,

    @PositiveOrZero(message = "El agua producida no puede ser negativa")
    double aguaProducidaM3,

    @PositiveOrZero(message = "El agua consumida no puede ser negativa")
    double aguaConsumidaM3
) {
    public Balance toEntity() {
        Balance balance = new Balance();
        balance.setPeriodo(this.periodo());
        balance.setAguaProducidaM3(this.aguaProducidaM3());
        balance.setAguaConsumidaM3(this.aguaConsumidaM3());
        return balance;
    }
}