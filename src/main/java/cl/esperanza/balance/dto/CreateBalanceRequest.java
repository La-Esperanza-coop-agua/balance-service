package cl.esperanza.balance.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateBalanceRequest(
    @NotNull(message = "El periodo no puede estar vacío") LocalDate periodo,
    @PositiveOrZero(message = "El agua producida no puede ser negativa") double aguaProducidaM3,
    @PositiveOrZero(message = "El agua consumida no puede ser negativa") double aguaConsumidaM3,
    @PositiveOrZero(message = "El agua consumida no puede ser negativa") double porcentajePerdida,
    @NotBlank(message = "El agua consumida no puede ser negativa") String estadoAlerta 
) {
}