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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/balances")
@Tag(name = "Balance Hídrico", description = "Análisis cruzado de agua producida (Telemetría) vs agua consumida (Facturación) para detectar fugas.")
public class BalanceController {
    
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Operation(summary = "Verificar fugas por periodo", description = "Calcula el porcentaje de pérdida de agua comparando lo extraído en los estanques contra lo facturado a los socios.")
    @PostMapping("/verificar-fugas")
    public ResponseEntity<BalanceResponse> verificarFugas(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        BalanceResponse response = balanceService.verificarFugas(periodo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener historial de balances", description = "Lista los balances hídricos registrados en la base de datos para una fecha específica.")
    @GetMapping("/historial/{periodo}")
    public ResponseEntity<List<Balance>> obtenerPorPeriodo(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        List<Balance> balances = balanceService.obtenerPorPeriodo(periodo);
        
        if (balances.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron balances registrados para el periodo: " + periodo);
        }
        
        return ResponseEntity.ok(balances);
    }
}