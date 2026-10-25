package com.hotel.huespedes.service;

import com.hotel.huespedes.dto.request.HuespedCreateRequest;
import com.hotel.huespedes.dto.request.HuespedUpdateRequest;
import com.hotel.huespedes.dto.response.HuespedResponse;

import java.util.List;

public interface HuespedService {

   
    HuespedResponse registrarHuesped(HuespedCreateRequest request);
    List<HuespedResponse> listarHuespedes(Boolean soloActivos);
    HuespedResponse obtenerPorId(int id);
    HuespedResponse obtenerPorDni(String dni);
    HuespedResponse actualizarHuesped(int id, HuespedUpdateRequest request);
    void darDeBajaLogica(int id);
    boolean existeYActivo(int id);
}
