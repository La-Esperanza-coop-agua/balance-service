package cl.esperanza.balance.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
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
    private final WebClient incidenciasWebClient;

    // Tuve que agregar el Qualifer debido que dio errores al iniciar en la application
    public BalanceController(
            BalanceService balServ, 
            @Qualifier("telemetriaWebClient") WebClient teleWebClient, 
            @Qualifier("facturacionWebClient") WebClient factWebClient, 
            @Qualifier("incidenciasWebClient") WebClient incWebClient) {
        
        this.balanceService = balServ;
        this.telemetriaWebClient = teleWebClient;
        this.facturaWebClient = factWebClient;
        this.incidenciasWebClient = incWebClient;
    }

    // EndPoint 1 obtener para tener periodo
    @GetMapping("/{periodo}")
    public ResponseEntity<List<Balance>> getBalancePorPeriodo(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        List<Balance> periodos = balanceService.obtenerPorPeriodo(periodo);
        return ResponseEntity.ok(periodos);
    }

    // EndPoint 2 para verificar las fugas
    @GetMapping("/verificar-fugas")
    public ResponseEntity<BalanceResponse> verificarFugas(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        String periodoStr = periodo.toString();
        double totalExtraido = 0;
        double totalFacturado = 0;

        // Para pedir datos a Telemetria
        try {
            TelemetriaDTO[] telemetrias = telemetriaWebClient.get()
                .uri("/mes/" + periodoStr)
                .retrieve().bodyToMono(TelemetriaDTO[].class).block();

            if (telemetrias != null) {
                for (TelemetriaDTO t : telemetrias) {
                    totalExtraido += t.getNivelEstanque();
                }
            }
        } catch (Exception e) {
            System.out.println("Error de conexion con Telemetria");
        }

        // Para pedir datos a Facturacion
        try {
            FacturaDTO[] facturas = facturaWebClient.get()
                .uri("/periodo/" + periodoStr)
                .retrieve().bodyToMono(FacturaDTO[].class).block();

            if (facturas != null) {
                for (FacturaDTO f : facturas) {
                    totalFacturado += f.getMetrosCubicosFacturados();
                }
            }
        } catch (Exception e) {
            System.out.println("Error de conexion con Facturacion");
        }

        // Calcula la perdida (Para poder sacar el porcentaje)
        double porcentaje = 0;
        boolean alerta = false;
        String mensaje = "Faltan datos para el calculo";

        if (totalExtraido > 0) {
            double perdida = totalExtraido - totalFacturado;
            porcentaje = (perdida / totalExtraido) * 100.0;

            if (porcentaje > 25.0) {
                alerta = true;
                mensaje = "Fuga detectada mayor al 25%";

                // Guarda ka incidencia de manera automatica
                try {
                    Map<String, String> reclamo = new HashMap<>();
                    reclamo.put("runSocio", "SISTEMA");
                    reclamo.put("descripcion", "Fuga en periodo " + periodoStr);

                    incidenciasWebClient.post()
                        .uri("/registrar")
                        .bodyValue(reclamo)
                        .retrieve()
                        .bodyToMono(Void.class).block();
                } catch (Exception e) {
                    System.out.println("Error al crear la incidencia automatica");
                }
            } else {
                mensaje = "Balance hidrico normal";
            }
        }

        // Redondeo a 2 decimales
        porcentaje = Math.round(porcentaje * 100.0) / 100.0;

        return ResponseEntity.ok(new BalanceResponse(porcentaje, alerta, mensaje));
    }

    // EndPoint 3 para guardar balance
    @PostMapping("/generar")
    public ResponseEntity<Balance> generarNuevoBalance(@Valid @RequestBody CreateBalanceRequest request) {
        Balance nuevoBalance = balanceService.generarBalance(BalanceMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBalance);
    }
}