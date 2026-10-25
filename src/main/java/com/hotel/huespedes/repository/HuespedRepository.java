package com.hotel.huespedes.repository;

import com.hotel.huespedes.entity.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface HuespedRepository extends JpaRepository<Huesped,Integer> {


    Optional<Huesped> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByDniAndIdHuespedNot(String dni, int id);
    List<Huesped> findByEstadoLogicoTrue();
    Optional<Huesped> findByIdHuespedAndEstadoLogicoTrue(int id);
}
