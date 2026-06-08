package cl.esperanza.balance.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cl.esperanza.balance.dto.BalanceResponse;
import cl.esperanza.balance.dto.TelemetriaDTO;
import cl.esperanza.balance.model.Balance;
import cl.esperanza.balance.repository.BalanceRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class BalanceService {
    
    @Autowired
    private BalanceRepository balanceRepo;

    @Autowired
    private WebClient telemetriaWebClient;

    @Autowired
    private WebClient facturacionWebClient;

    public List<Balance> obtenerPorPeriodo(LocalDate periodo) {
        return balanceRepo.findByPeriodo(periodo);
    }

    public BalanceResponse verificarFugas(LocalDate periodo) {
        String periodoStr = periodo.toString();

        double aguaProducida = obtenerTotalExtraido(periodoStr);

        if (aguaProducida <= 0) {
            throw new IllegalArgumentException("No hay datos de telemetría suficientes para el periodo: " + periodoStr);
        }

        double aguaConsumida = obtenerTotalFacturado(periodoStr);

        // Evitar división por cero y calcular pérdida y porcentaje de manera segura
        double perdida = Math.max(0, aguaProducida - aguaConsumida);
        double porcentaje = (perdida / aguaProducida) * 100.0;
        porcentaje = Math.round(porcentaje * 100.0) / 100.0;

        boolean hayFuga = porcentaje > 25.0;
        String estadoAlertaStr = hayFuga ? "ALERTA_FUGA" : "NORMAL";
        String mensajeRespuesta = hayFuga ? "Fuga detectada mayor al 25%" : "Balance hidrico normal";

        Balance nuevoBalance = new Balance();

        nuevoBalance.setPeriodo(periodo);
        nuevoBalance.setAguaProducidaM3(aguaProducida);
        nuevoBalance.setAguaConsumidaM3(aguaConsumida);
        nuevoBalance.setPorcentajePerdida(porcentaje);
        nuevoBalance.setEstadoAlerta(estadoAlertaStr);

        balanceRepo.save(nuevoBalance);

        return new BalanceResponse(porcentaje, hayFuga, mensajeRespuesta);
    }

    private double obtenerTotalExtraido(String periodoStr) {
        try {
            TelemetriaDTO[] telemetrias = telemetriaWebClient.get()
                .uri("http://localhost:8086/api/v1/telemetria/fecha/" + periodoStr) // Ruta real de tu TelemetriaController
                .retrieve()
                .bodyToMono(TelemetriaDTO[].class)
                .block();

            if (telemetrias == null) return 0.0;
            return Arrays.stream(telemetrias).mapToDouble(TelemetriaDTO::getNivelEstanque).sum();
        } catch (Exception e) {
            System.err.println("Error consultando Telemetría: " + e.getMessage());
            return 0.0; 
        }
    }

    private double obtenerTotalFacturado(String periodoStr) {
        try {
            // ¡AHORA ES MUCHO MÁS SIMPLE! Solo recibimos un número.
            Double totalFacturado = facturacionWebClient.get()
                .uri("/api/v1/facturacion/periodo/" + periodoStr + "/total-consumo") // La nueva ruta que acabamos de crear
                .retrieve()
                .bodyToMono(Double.class)
                .block();

            return totalFacturado != null ? totalFacturado : 0.0;
        } catch (Exception e) {
            System.err.println("Error consultando Facturación: " + e.getMessage());
            return 0.0;
        }
    }
}