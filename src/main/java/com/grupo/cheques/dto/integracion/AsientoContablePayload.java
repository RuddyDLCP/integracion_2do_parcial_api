package com.grupo.cheques.dto.integracion;

import java.util.List;

public record AsientoContablePayload(
        String descripcion,
        AuxiliarRefDto auxiliar,
        String fechaAsiento,
        double montoTotal,
        List<AsientoDetalleDto> detalles
) {
}
