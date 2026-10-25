package com.hotel.huespedes.service.impl;

import com.hotel.huespedes.client.AlquileresClient;
import com.hotel.huespedes.dto.request.HuespedCreateRequest;
import com.hotel.huespedes.dto.request.HuespedUpdateRequest;
import com.hotel.huespedes.dto.response.HuespedResponse;
import com.hotel.huespedes.entity.Huesped;
import com.hotel.huespedes.exception.BusinessRuleException;
import com.hotel.huespedes.exception.DuplicateResourceException;
import com.hotel.huespedes.exception.ResourceNotFoundException;
import com.hotel.huespedes.repository.HuespedRepository;
import com.hotel.huespedes.service.HuespedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class HuespedServiceImpl implements HuespedService {

    private final HuespedRepository huespedRepository;
    private final AlquileresClient alquileresClient;

    @Override
    @Transactional
    public HuespedResponse registrarHuesped(HuespedCreateRequest request) {
        log.info("Iniciando registro de huésped con DNI: {}", request.getDni());
        
        if (huespedRepository.existsByDni(request.getDni())) {
            log.error("Intento de registro con DNI duplicado: {}", request.getDni());
            throw new DuplicateResourceException("Ya existe un huésped registrado con el DNI: " + request.getDni());
        }

        if (request.getFechaIngreso() != null && request.getFechaSalida() != null) {
            if (request.getFechaSalida().isBefore(request.getFechaIngreso())) {
                throw new BusinessRuleException("La fecha de salida (" + request.getFechaSalida() + 
                        ") no puede ser anterior a la fecha de ingreso (" + request.getFechaIngreso() + ").");
            }
        }
        
        Huesped huesped = Huesped.builder()
                .nombres(request.getNombres().trim())
                .apellidos(request.getApellidos().trim())
                .dni(request.getDni().trim())
                .tel(request.getTel() != null ? request.getTel().trim() : null)
                .email(request.getEmail().trim().toLowerCase())
                .direccion(request.getDireccion() != null ? request.getDireccion().trim() : null)
                .nacionalidad(request.getNacionalidad() != null ? request.getNacionalidad().trim() : null)
                .fechaNacimiento(request.getFechaNacimiento())
                .fechaIngreso(request.getFechaIngreso())
                .fechaSalida(request.getFechaSalida())
                .estadoLogico(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Huesped huespedGuardado = huespedRepository.save(huesped);
        log.info("Huésped registrado exitosamente con ID: {}", huespedGuardado.getIdHuesped());

        return mapToResponse(huespedGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HuespedResponse> listarHuespedes(Boolean soloActivos) {
        log.info("Consultando lista de huéspedes. Filtrar soloActivos: {}", soloActivos);
        List<Huesped> lista;
        if (Boolean.TRUE.equals(soloActivos)) {
            lista = huespedRepository.findByEstadoLogicoTrue();
        } else {
            lista = huespedRepository.findAll();
        }

        return lista.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HuespedResponse obtenerPorId(int id) {
        log.info("Buscando huésped por ID: {}", id);
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún huésped con el identificador: " + id));

        return mapToResponse(huesped);
    }

    @Override
    @Transactional(readOnly = true)
    public HuespedResponse obtenerPorDni(String dni) {
        log.info("Buscando huésped por DNI: {}", dni);
        Huesped huesped = huespedRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún huésped con el DNI: " + dni));

        return mapToResponse(huesped);
    }

    @Override
    @Transactional
    public HuespedResponse actualizarHuesped(int id, HuespedUpdateRequest request) {
        log.info("Actualizando datos del huésped ID: {}", id);

        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el huésped con ID: " + id));


        if (!huesped.getDni().equalsIgnoreCase(request.getDni()) &&
                huespedRepository.existsByDniAndIdHuespedNot(request.getDni(), id)) {
            throw new DuplicateResourceException("El DNI " + request.getDni() + " ya se encuentra registrado por otro huésped.");
        }


        if (request.getFechaIngreso() != null && request.getFechaSalida() != null) {
            if (request.getFechaSalida().isBefore(request.getFechaIngreso())) {
                throw new BusinessRuleException("La fecha de salida no puede ser anterior a la fecha de ingreso.");
            }
        }

        
        if (Boolean.FALSE.equals(request.getEstadoLogico()) && Boolean.TRUE.equals(huesped.getEstadoLogico())) {
            if (alquileresClient.tieneAlquilerActivo(id)) {
                throw new BusinessRuleException("No se permite desactivar al huésped porque mantiene un alquiler vigente o activo asociado (RN04).");
            }
        }

        huesped.setNombres(request.getNombres().trim());
        huesped.setApellidos(request.getApellidos().trim());
        huesped.setDni(request.getDni().trim());
        huesped.setTel(request.getTel() != null ? request.getTel().trim() : null);
        huesped.setEmail(request.getEmail().trim().toLowerCase());
        huesped.setDireccion(request.getDireccion() != null ? request.getDireccion().trim() : null);
        huesped.setNacionalidad(request.getNacionalidad() != null ? request.getNacionalidad().trim() : null);
        huesped.setFechaNacimiento(request.getFechaNacimiento());
        huesped.setFechaIngreso(request.getFechaIngreso());
        huesped.setFechaSalida(request.getFechaSalida());
        huesped.setEstadoLogico(request.getEstadoLogico());

        Huesped actualizado = huespedRepository.save(huesped);
        log.info("Huésped ID: {} actualizado exitosamente", id);

        return mapToResponse(actualizado);
    }

    @Override
    @Transactional
    public void darDeBajaLogica(int id) {
        log.info("Solicitud de baja lógica para huésped ID: {}", id);
        
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el huésped con ID: " + id));
        
        if (!huesped.getEstadoLogico()) {
            log.info("El huésped ID: {} ya se encuentra en estado inactivo.", id);
            return;
        }

        boolean tieneAlquilerActivo = alquileresClient.tieneAlquilerActivo(id);
        if (tieneAlquilerActivo) {
            log.warn("Violación de RN04: Intento de eliminar huésped ID: {} con alquiler activo.", id);
            throw new BusinessRuleException("No se permite eliminar ni dar de baja lógica al huésped porque mantiene un alquiler vigente o activo asociado (RN04).");
        }
        
        huesped.setEstadoLogico(false);
        huespedRepository.save(huesped);
        log.info("Baja lógica aplicada correctamente para el huésped ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeYActivo(int id) {
        return huespedRepository.findByIdHuespedAndEstadoLogicoTrue(id).isPresent();
    }

  
    private HuespedResponse mapToResponse(Huesped h) {
        return HuespedResponse.builder()
                .id(h.getIdHuesped())
                .nombres(h.getNombres())
                .apellidos(h.getApellidos())
                .dni(h.getDni())
                .tel(h.getTel())
                .email(h.getEmail())
                .direccion(h.getDireccion())
                .nacionalidad(h.getNacionalidad())
                .fechaNacimiento(h.getFechaNacimiento())
                .fechaIngreso(h.getFechaIngreso())
                .fechaSalida(h.getFechaSalida())
                .estadoLogico(h.getEstadoLogico())
                .fechaRegistro(h.getFechaRegistro())
                .build();
    }
}
