package cl.esperanza.balance.dto;

import cl.esperanza.balance.model.Balance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateBalanceRequest(
    @NotBlank(message = "El periodo no puede estar vacío")
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