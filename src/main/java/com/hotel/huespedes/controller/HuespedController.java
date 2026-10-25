package com.hotel.huespedes.controller;

import com.hotel.huespedes.dto.request.HuespedCreateRequest;
import com.hotel.huespedes.dto.request.HuespedUpdateRequest;
import com.hotel.huespedes.dto.response.ApiResponse;
import com.hotel.huespedes.dto.response.HuespedResponse;
import com.hotel.huespedes.service.HuespedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/huespedes")
@RequiredArgsConstructor
@Tag(name = "Huéspedes")
@CrossOrigin(origins = "*")

public class HuespedController {

    private final HuespedService huespedService;

    
    @Operation(summary = "Registrar un nuevo huésped")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Huésped registrado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DNI ya registrado en el sistema")
    })

    @PostMapping
    public ResponseEntity<ApiResponse<HuespedResponse>> registrarHuesped(
            @Valid @RequestBody HuespedCreateRequest request) {

        log.info("REST: Solicitud de registro de huésped con DNI: {}", request.getDni());
        HuespedResponse nuevoHuesped = huespedService.registrarHuesped(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Huésped registrado exitosamente", nuevoHuesped));
    }

    
    @Operation(summary = "Listar huéspedes")
    @GetMapping
    public ResponseEntity<ApiResponse<List<HuespedResponse>>> listarHuespedes(
            @Parameter()
            @RequestParam(required = false, defaultValue = "false") Boolean soloActivos) {

        log.info("REST: Solicitud de listado de huéspedes (soloActivos: {})", soloActivos);
        List<HuespedResponse> lista = huespedService.listarHuespedes(soloActivos);

        return ResponseEntity.ok(ApiResponse.ok("Lista de huéspedes obtenida exitosamente", lista));
    }

    
    @Operation(summary = "Consultar huésped por ID",
            description = "Recupera la información completa de un huésped dado su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HuespedResponse>> obtenerPorId(
            @Parameter(description = "ID del huésped a consultar", example = "1")
            @PathVariable int id) {

        log.info("REST: Consulta de huésped con ID: {}", id);
        HuespedResponse huesped = huespedService.obtenerPorId(id);

        return ResponseEntity.ok(ApiResponse.ok("Huésped encontrado exitosamente", huesped));
    }

    
    @Operation(summary = "Consultar huésped por DNI",
            description = "Recupera los datos del huésped utilizando su DNI.")
    @GetMapping("/dni/{dni}")
    public ResponseEntity<ApiResponse<HuespedResponse>> obtenerPorDni(
            @Parameter(description = "DNI de 8 dígitos", example = "72345678")
            @PathVariable String dni) {

        log.info("REST: Consulta de huésped con DNI: {}", dni);
        HuespedResponse huesped = huespedService.obtenerPorDni(dni);

        return ResponseEntity.ok(ApiResponse.ok("Huésped encontrado exitosamente", huesped));
    }

    
    @Operation(summary = "Validar existencia y vigencia de huésped para alquileres-service",
            description = "Comprueba si el huésped existe en db_huespedes y tiene estado_logico activo.")

    @GetMapping("/{id}/existe")
    public ResponseEntity<Map<String, Object>> validarExistenciaActivo(@PathVariable int id) {
        log.info("REST: Validación de existencia y estado activo para huespedId: {}", id);
        boolean existe = huespedService.existeYActivo(id);

        return ResponseEntity.ok(Map.of(
                "huespedId", id,
                "existe", existe,
                "activo", existe
        ));
    }

    @Operation(summary = "Actualizar datos de un huésped",
            description = "Modifica los campos del huésped respetando la unicidad del DNI y las reglas de negocio.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HuespedResponse>> actualizarHuesped(
            @PathVariable int id,
            @Valid @RequestBody HuespedUpdateRequest request) {

        log.info("REST: Solicitud de actualización para el huésped ID: {}", id);
        HuespedResponse actualizado = huespedService.actualizarHuesped(id, request);

        return ResponseEntity.ok(ApiResponse.ok("Huésped actualizado exitosamente", actualizado));
    }

   
    @Operation(summary = "Baja lógica de un huésped",
            description = "Aplica eliminación lógica (estado_logico = false). Falla con 409 Conflict si mantiene alquileres activos.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Huésped dado de baja lógica correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Huésped no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto: El huésped mantiene un alquiler vigente o activo (RN04)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> darDeBajaLogica(
            @Parameter(description = "ID del huésped a dar de baja", example = "1")
            @PathVariable int id) {

        log.info("REST: Solicitud de baja lógica para huésped ID: {}", id);
        huespedService.darDeBajaLogica(id);

        return ResponseEntity.ok(ApiResponse.ok("Huésped eliminado lógicamente de forma correcta."));
    }
}
