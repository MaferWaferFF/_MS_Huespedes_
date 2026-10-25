package com.hotel.huespedes.service;

import com.hotel.huespedes.client.AlquileresClient;
import com.hotel.huespedes.dto.request.HuespedCreateRequest;
import com.hotel.huespedes.dto.response.HuespedResponse;
import com.hotel.huespedes.entity.Huesped;
import com.hotel.huespedes.exception.BusinessRuleException;
import com.hotel.huespedes.exception.DuplicateResourceException;
import com.hotel.huespedes.repository.HuespedRepository;
import com.hotel.huespedes.service.impl.HuespedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HuespedServiceTest {

    @Mock
    private HuespedRepository huespedRepository;

    @Mock
    private AlquileresClient alquileresClient;

    @InjectMocks
    private HuespedServiceImpl huespedService;

    private HuespedCreateRequest request;
    private Huesped huesped;

    @BeforeEach
    void setUp() {
        request = HuespedCreateRequest.builder()
                .nombres("Ernesto Andres")
                .apellidos("Atencio Campos")
                .dni("72345678")
                .tel("987654321")
                .email("ernesto.atencio@email.com")
                .direccion("Av. Centenario Km 5")
                .nacionalidad("Peruana")
                .fechaNacimiento(LocalDate.of(1998, 5, 15))
                .fechaIngreso(LocalDate.of(2026, 10, 24))
                .fechaSalida(LocalDate.of(2026, 10, 28))
                .build();

        huesped = Huesped.builder()
                .idHuesped(1)
                .nombres("Ernesto Andres")
                .apellidos("Atencio Campos")
                .dni("72345678")
                .tel("987654321")
                .email("ernesto.atencio@email.com")
                .direccion("Av. Centenario Km 5")
                .nacionalidad("Peruana")
                .fechaNacimiento(LocalDate.of(1998, 5, 15))
                .fechaIngreso(LocalDate.of(2026, 10, 24))
                .fechaSalida(LocalDate.of(2026, 10, 28))
                .estadoLogico(true)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("RF04: Registrar huésped con éxito")
    void registrarHuesped_Exitoso() {
        when(huespedRepository.existsByDni("72345678")).thenReturn(false);
        when(huespedRepository.save(any(Huesped.class))).thenReturn(huesped);

        HuespedResponse response = huespedService.registrarHuesped(request);

        assertNotNull(response);
        assertEquals("72345678", response.getDni());
        assertEquals("Ernesto Andres", response.getNombres());
        assertTrue(response.getEstadoLogico());
        verify(huespedRepository, times(1)).save(any(Huesped.class));
    }

    @Test
    @DisplayName("RF04: Error al registrar con DNI duplicado")
    void registrarHuesped_DniDuplicado() {
        when(huespedRepository.existsByDni("72345678")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            huespedService.registrarHuesped(request);
        });

        verify(huespedRepository, never()).save(any(Huesped.class));
    }

    @Test
    @DisplayName("RN04: No permitir baja lógica si el huésped mantiene un alquiler activo")
    void darDeBajaLogica_ErrorAlquilerActivo() {
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        when(alquileresClient.tieneAlquilerActivo(1)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            huespedService.darDeBajaLogica(1);
        });

        assertTrue(exception.getMessage().contains("RN04"));
        verify(huespedRepository, never()).save(any(Huesped.class));
    }

    @Test
    @DisplayName("RN04: Permitir baja lógica cuando no tiene alquileres activos")
    void darDeBajaLogica_Exitoso() {
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        when(alquileresClient.tieneAlquilerActivo(1)).thenReturn(false);

        huespedService.darDeBajaLogica(1);

        assertFalse(huesped.getEstadoLogico());
        verify(huespedRepository, times(1)).save(huesped);
    }
}
