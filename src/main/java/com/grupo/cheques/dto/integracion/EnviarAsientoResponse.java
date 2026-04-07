package com.grupo.cheques.dto.integracion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnviarAsientoResponse {

    private boolean simulacion;
    private String periodo;
    private int cantidadCheques;
    private double totalSuma;
    private AsientoContablePayload asiento;
    private Boolean enviadoExitoso;
    private Integer httpStatusContabilidad;
    private String respuestaContabilidad;
    private String mensaje;

    public static EnviarAsientoResponse preview(String periodo, int cantidad, double total,
                                                AsientoContablePayload asiento, String mensaje) {
        EnviarAsientoResponse r = new EnviarAsientoResponse();
        r.simulacion = true;
        r.periodo = periodo;
        r.cantidadCheques = cantidad;
        r.totalSuma = total;
        r.asiento = asiento;
        r.mensaje = mensaje;
        return r;
    }

    public static EnviarAsientoResponse enviado(String periodo, int cantidad, double total,
                                                  AsientoContablePayload asiento,
                                                  boolean exito, Integer httpStatus, String cuerpoRespuesta,
                                                  String mensaje) {
        EnviarAsientoResponse r = new EnviarAsientoResponse();
        r.simulacion = false;
        r.periodo = periodo;
        r.cantidadCheques = cantidad;
        r.totalSuma = total;
        r.asiento = asiento;
        r.enviadoExitoso = exito;
        r.httpStatusContabilidad = httpStatus;
        r.respuestaContabilidad = cuerpoRespuesta;
        r.mensaje = mensaje;
        return r;
    }

    @JsonIgnore
    public List<AsientoDetalleDto> getLineas() {
        return asiento != null ? asiento.detalles() : List.of();
    }

    public boolean isSimulacion() {
        return simulacion;
    }

    public void setSimulacion(boolean simulacion) {
        this.simulacion = simulacion;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public int getCantidadCheques() {
        return cantidadCheques;
    }

    public void setCantidadCheques(int cantidadCheques) {
        this.cantidadCheques = cantidadCheques;
    }

    public double getTotalSuma() {
        return totalSuma;
    }

    public void setTotalSuma(double totalSuma) {
        this.totalSuma = totalSuma;
    }

    public AsientoContablePayload getAsiento() {
        return asiento;
    }

    public void setAsiento(AsientoContablePayload asiento) {
        this.asiento = asiento;
    }

    public Boolean getEnviadoExitoso() {
        return enviadoExitoso;
    }

    public void setEnviadoExitoso(Boolean enviadoExitoso) {
        this.enviadoExitoso = enviadoExitoso;
    }

    public Integer getHttpStatusContabilidad() {
        return httpStatusContabilidad;
    }

    public void setHttpStatusContabilidad(Integer httpStatusContabilidad) {
        this.httpStatusContabilidad = httpStatusContabilidad;
    }

    public String getRespuestaContabilidad() {
        return respuestaContabilidad;
    }

    public void setRespuestaContabilidad(String respuestaContabilidad) {
        this.respuestaContabilidad = respuestaContabilidad;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
