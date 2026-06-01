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
    public ResponseEntity<BalanceResponse> verificarFugas(@RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        String periodoTexto = periodo.toString(); // es necesario pasar el LocalDate a String para que funcione con WebClient

        TelemetriaDTO[] listaTelemetria = null;
        try {
            listaTelemetria = telemetriaWebClient.get()
                .uri("/mes/{periodo}", periodoTexto)
                .retrieve()
                .bodyToMono(TelemetriaDTO[].class)
                .block(); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new BalanceResponse(0, false, "Error: Telemetría apagada."));
        }

        FacturaDTO[] listaFacturas = null;
        try {
            listaFacturas = facturaWebClient.get()
                    .uri("/periodo/{periodo}", periodoTexto) 
                    .retrieve()
                    .bodyToMono(FacturaDTO[].class)
                    .block();
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                 .body(new BalanceResponse(0, false, "Error: Facturación apagada."));
        }
        
        double totalExtraido = 0;
        if (listaTelemetria != null) {
            for (TelemetriaDTO telemetria : listaTelemetria) {
                totalExtraido = totalExtraido + telemetria.getNivelEstanque(); 
            }
        }
        
        double totalFacturado = 0;
        if (listaFacturas != null) {
            for (FacturaDTO factura : listaFacturas) {
                totalFacturado = totalFacturado + factura.getMetrosCubicosFacturados();
            }
        }

        double porcentajePerdida = 0;
        boolean alerta = false;
        String mensaje = "No hay datos de extracción para este periodo.";

        if (totalExtraido > 0) {
            double aguaPerdida = totalExtraido - totalFacturado;
            porcentajePerdida = (aguaPerdida / totalExtraido) * 100.0;
            if (porcentajePerdida > 25.0) {
                alerta = true;
                mensaje = "¡ALERTA! Las pérdidas de agua superan el 25%. Posible tubería rota.";
            } else {
                alerta = false;
                mensaje = "Todo en orden. Las pérdidas de agua están dentro de lo normal.";
            }
        }

        return ResponseEntity.ok(new BalanceResponse(porcentajePerdida, alerta, mensaje));
    }
        

    @PostMapping("/generar")
    public ResponseEntity<Balance> generarNuevoBalance(@Valid @RequestBody CreateBalanceRequest request) {
        Balance nuevoBalance = balanceService.generarBalance(BalanceMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBalance);
    }
}