package cl.esperanza.balance.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.esperanza.balance.dto.CreateBalanceRequest;
import cl.esperanza.balance.mapper.BalanceMapper;
import cl.esperanza.balance.model.Balance;
import cl.esperanza.balance.service.BalanceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {
    
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/{periodo}")
    public ResponseEntity<List<Balance>> getBalancePorPeriodo(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        List<Balance> periodos = balanceService.obtenerPorPeriodo(periodo);
        return ResponseEntity.ok(periodos);
    }

    @PostMapping("/generar")
    public ResponseEntity<Balance> generarNuevoBalance(@Valid @RequestBody CreateBalanceRequest request) {
        Balance nuevoBalance = balanceService.generarBalance(BalanceMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBalance);
    }
}