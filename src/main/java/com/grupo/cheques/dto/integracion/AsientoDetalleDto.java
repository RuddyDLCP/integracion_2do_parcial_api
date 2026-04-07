package com.grupo.cheques.dto.integracion;

public record AsientoDetalleDto(
        CuentaRefDto cuenta,
        String tipoMovimiento,
        double monto
) {
}
