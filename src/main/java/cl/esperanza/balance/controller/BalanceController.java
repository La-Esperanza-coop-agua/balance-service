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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import cl.esperanza.balance.dto.BalanceResponse;
import cl.esperanza.balance.dto.CreateBalanceRequest;
import cl.esperanza.balance.dto.FacturaDTO;
import cl.esperanza.balance.dto.TelemetriaDTO;
import cl.esperanza.balance.mapper.BalanceMapper;
import cl.esperanza.balance.model.Balance;
import cl.esperanza.balance.service.BalanceService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {
    
    private final BalanceService balanceService;
    private final WebClient telemetriaWebClient;
    private final WebClient facturaWebClient;

    public BalanceController(BalanceService balanceService, WebClient telemetriaWebClient, WebClient facturaWebClient) {
        this.balanceService = balanceService;
        this.telemetriaWebClient = telemetriaWebClient;
        this.facturaWebClient = facturaWebClient;
    }

    @GetMapping("/{periodo}")
    public ResponseEntity<List<Balance>> getBalancePorPeriodo(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        List<Balance> periodos = balanceService.obtenerPorPeriodo(periodo);
        return ResponseEntity.ok(periodos);
    }

    @GetMapping("/verificar-fugas")
    public ResponseEntity<BalanceResponse> verificarFugas(@RequestParam String periodo) {
        TelemetriaDTO[] telemetrias = null;
        try {
            telemetrias = telemetriaWebClient.get()
                .uri("/mes/{periodo}", periodo)
                .retrieve()
                .bodyToMono(TelemetriaDTO[].class)
                .block();   
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new BalanceResponse(0, false, "Error: El microservicio de Telemetría no responde o la ruta no existe."));
        }

        FacturaDTO[] facturas = null;
        try {
            facturas = facturaWebClient.get()
                    .uri("/periodo/{periodo}", periodo) 
                    .retrieve()
                    .bodyToMono(FacturaDTO[].class)
                    .block();
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new BalanceResponse(0, false, "Error: El microservicio de Facturación no responde o la ruta no existe."));
        }
        /* 
        double totalAguaExtraida = 0;
        if (telemetrias != null) {
            for (TelemetriaDTO t : telemetrias) {
                totalAguaExtraida += t.getNivelEstanque(); 
            }
        }
        */
    }
    

    @PostMapping("/generar")
    public ResponseEntity<Balance> generarNuevoBalance(@Valid @RequestBody CreateBalanceRequest request) {
        Balance nuevoBalance = balanceService.generarBalance(BalanceMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBalance);
    }
}