package cl.esperanza.balance.mapper;

import cl.esperanza.balance.dto.CreateBalanceRequest;
import cl.esperanza.balance.model.Balance;

public class BalanceMapper {

    public static Balance toModel(CreateBalanceRequest request){
        return new Balance(null,
            request.periodo(), request.aguaProducidaM3(), request.aguaConsumidaM3(),
            request.porcentajePerdida(), request.estadoAlerta()
        );
    }

}
