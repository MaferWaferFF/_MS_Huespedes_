package com.hotel.huespedes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema()
public class HuespedResponse {

    @Schema()
    private int id;

    @Schema()
    private String nombres;

    @Schema()
    private String apellidos;

    @Schema()
    private String dni;

    @Schema()
    private String tel;

    @Schema()
    private String email;

    @Schema()
    private String direccion;

    @Schema()
    private String nacionalidad;

    @Schema()
    private LocalDate fechaNacimiento;

    @Schema()
    private LocalDate fechaIngreso;

    @Schema()
    private LocalDate fechaSalida;

    @Schema()
    private Boolean estadoLogico;

    @Schema()
    private LocalDateTime fechaRegistro;
}
