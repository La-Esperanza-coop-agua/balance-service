package cl.esperanza.balance.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.esperanza.balance.dto.BalanceResponse;
import cl.esperanza.balance.exception.ResourceNotFoundException;
import cl.esperanza.balance.model.Balance;
import cl.esperanza.balance.service.BalanceService;

@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {
    
    private final BalanceService balanceService;

    public BalanceController(BalanceService balServ) {
        this.balanceService = balServ;
    }

    // EndPoint 1 postea un balance
    @PostMapping("/verificar-fugas")
    public ResponseEntity<BalanceResponse> verificarFugas(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        BalanceResponse response = balanceService.verificarFugas(periodo);
        return ResponseEntity.ok(response);
    }

    // EndPoint 2 obtiene balances por periodo 
    @GetMapping("/{periodo}")
    public ResponseEntity<List<Balance>> obtenerPorPeriodo(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        List<Balance> balances = balanceService.obtenerPorPeriodo(periodo);
        
        if (balances.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron balances registrados para el periodo: " + periodo);
        }
        
        return ResponseEntity.ok(balances);
    }
}