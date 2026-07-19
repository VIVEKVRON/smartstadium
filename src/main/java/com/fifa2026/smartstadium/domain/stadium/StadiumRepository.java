package com.fifa2026.smartstadium.domain.stadium;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, UUID> {
    List<Stadium> findByCity(String city);
    List<Stadium> findByCountry(String country);
    List<Stadium> findByNameContainingIgnoreCase(String name);
}
