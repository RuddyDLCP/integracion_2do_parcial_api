package com.grupo.cheques.dto.integracion;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LineaAsientoDto(
        @JsonProperty("idCuenta") String idCuenta,
        @JsonProperty("descripcion") String descripcion,
        @JsonProperty("tipoMovimiento") String tipoMovimiento,
        @JsonProperty("monto") double monto
) {
}
