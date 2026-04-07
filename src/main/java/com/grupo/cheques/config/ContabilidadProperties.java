package com.grupo.cheques.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integracion.contabilidad")
public class ContabilidadProperties {

    /**
     * URL completa del endpoint de Contabilidad (POST). Vacía = solo simulación.
     */
    private String url = "";

    private int auxiliarId = 9;
    private long cuentaDebitoDefault = 1;
    private long cuentaCreditoDefault = 2;

    /**
     * Plantilla con año y mes: %1$d = año, %2$d = mes (1-12).
     */
    private String descripcionPlantilla = "Asiento de Cheques correspondiente al periodo %04d-%02d";

    private int connectTimeoutSeconds = 10;

    private int readTimeoutSeconds = 30;

    /** Nombre del header si se configura apiKey (ej. X-API-Key). */
    private String apiKeyHeader = "";

    private String apiKey = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? "" : url.trim();
    }

    public int getAuxiliarId() {
        return auxiliarId;
    }

    public void setAuxiliarId(int auxiliarId) {
        this.auxiliarId = auxiliarId;
    }

    public long getCuentaDebitoDefault() {
        return cuentaDebitoDefault;
    }

    public void setCuentaDebitoDefault(long cuentaDebitoDefault) {
        this.cuentaDebitoDefault = cuentaDebitoDefault;
    }

    public long getCuentaCreditoDefault() {
        return cuentaCreditoDefault;
    }

    public void setCuentaCreditoDefault(long cuentaCreditoDefault) {
        this.cuentaCreditoDefault = cuentaCreditoDefault;
    }

    public String getDescripcionPlantilla() {
        return descripcionPlantilla;
    }

    public void setDescripcionPlantilla(String descripcionPlantilla) {
        this.descripcionPlantilla = descripcionPlantilla;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public void setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    public String getApiKeyHeader() {
        return apiKeyHeader;
    }

    public void setApiKeyHeader(String apiKeyHeader) {
        this.apiKeyHeader = apiKeyHeader == null ? "" : apiKeyHeader.trim();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
    }

    public boolean hasUrl() {
        return !getUrl().isEmpty();
    }

    public boolean hasApiKey() {
        return !getApiKeyHeader().isEmpty() && !getApiKey().isEmpty();
    }
}
