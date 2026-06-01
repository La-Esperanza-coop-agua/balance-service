package cl.esperanza.balance.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import cl.esperanza.balance.model.Balance;
import cl.esperanza.balance.repository.BalanceRepository;

@Service
@Transactional
public class BalanceService {
    
    @Autowired
    private BalanceRepository balanceRepo;

    public List<Balance> obtenerPorPeriodo(LocalDate periodo) {
        return balanceRepo.findByPeriodo(periodo);
    }

    public Balance generarBalance(Balance balance) {
        return balanceRepo.save(balance);
    }
}