package cl.esperanza.balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.esperanza.balance.model.Balance;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Integer> {
    Optional<Balance> findByPeriodo(String periodo);
}