package cl.esperanza.balance.service;

import org.springframework.stereotype.Service;
import cl.esperanza.balance.model.Balance;
import cl.esperanza.balance.repository.BalanceRepository;
import cl.esperanza.balance.exception.ResourceNotFoundException;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepo;

    public BalanceService(BalanceRepository balanceRepo) {
        this.balanceRepo = balanceRepo;
    }

    public Balance generarBalance(Balance balance) {
        double producida = balance.getAguaProducidaM3();
        double consumida = balance.getAguaConsumidaM3();

        if (producida <= 0) {
            balance.setPorcentajePerdida(0);
            balance.setEstadoAlerta("ERROR - PRODUCCION INVALIDA");
            return balanceRepo.save(balance);
        }

        double diferencia = producida - consumida;
        double porcentaje = (diferencia / producida) * 100.0;

        balance.setPorcentajePerdida(Math.round(porcentaje * 100.0) / 100.0);

// Aca se hace una alerta de fuga en caso de que si la perdida de agua es mayor a 30 se disparara una fuga

        if (balance.getPorcentajePerdida() > 30.0) {
            balance.setEstadoAlerta("ALERTA_FUGA - REVISAR CAÑERIAS");
        } else {
            balance.setEstadoAlerta("NORMAL");
        }

        return balanceRepo.save(balance);
    }

    public Balance obtenerPorPeriodo(String periodo) {
        return balanceRepo.findByPeriodo(periodo)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró balance para el periodo: " + periodo));
    }
}