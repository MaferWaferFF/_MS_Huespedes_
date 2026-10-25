package com.hotel.huespedes.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema()
public class HuespedCreateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "Los nombres solo deben contener letras y espacios")
    @Schema()
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "Los apellidos solo deben contener letras y espacios")
    @Schema()
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener un formato válido de exactamente 8 dígitos numéricos")
    @Schema()
    private String dni;

    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe contener exactamente 9 dígitos numéricos")
    @Schema()
    private String tel;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido")
    @Size(max = 150, message = "El correo electrónico no puede superar los 150 caracteres")
    @Schema()
    private String email;

    @Size(max = 200, message = "La dirección no puede exceder los 200 caracteres")
    @Schema()
    private String direccion;

    @Size(max = 50, message = "La nacionalidad no puede exceder los 50 caracteres")
    @Schema()
    private String nacionalidad;

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @Schema()
    private LocalDate fechaNacimiento;

    @Schema()
    private LocalDate fechaIngreso;

    @Schema()
    private LocalDate fechaSalida;
}
