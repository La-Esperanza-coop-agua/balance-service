package cl.esperanza.balance.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.esperanza.balance.model.Balance;
import cl.esperanza.balance.service.BalanceService;
import cl.esperanza.balance.dto.CreateBalanceRequest;

@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {
    
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/{periodo}")
    public ResponseEntity<Balance> getBalancePorPeriodo(@PathVariable String periodo) {
        return ResponseEntity.ok(balanceService.obtenerPorPeriodo(periodo));
    }

    @PostMapping("/generar")
    public ResponseEntity<Balance> generarNuevoBalance(@Valid @RequestBody CreateBalanceRequest request) {
        Balance nuevoBalance = balanceService.generarBalance(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBalance);
    }
}