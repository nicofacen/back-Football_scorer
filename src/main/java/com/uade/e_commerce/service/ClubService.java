package com.uade.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ClubRequest;
import com.uade.e_commerce.dto.ClubResponse;
import com.uade.e_commerce.model.Club;
import com.uade.e_commerce.repository.ClubRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ClubService {

    private final ClubRepository clubRepository;

    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public List<ClubResponse> listar() {
        return clubRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ClubResponse> obtenerPorId(Long id) {
        return clubRepository.findById(id)
                .map(this::toResponse);
    }

    public Optional<ClubResponse> crear(ClubRequest request) {
        if (!esValido(request) || clubRepository.existsByNombre(request.getNombre())) {
            return Optional.empty();
        }

        Club club = new Club();
        club.setNombre(request.getNombre());
        club.setPais(request.getPais());
        club.setEscudoUrl(request.getEscudoUrl());

        return Optional.of(toResponse(clubRepository.save(club)));
    }

    public Optional<ClubResponse> actualizar(Long id, ClubRequest datos) {
        return clubRepository.findById(id)
                .map(club -> {
                    club.setNombre(datos.getNombre());
                    club.setPais(datos.getPais());
                    club.setEscudoUrl(datos.getEscudoUrl());
                    return clubRepository.save(club);
                })
                .map(this::toResponse);
    }

    public boolean eliminar(Long id) {
        if (!clubRepository.existsById(id)) {
            return false;
        }
        clubRepository.deleteById(id);
        return true;
    }

    public boolean esValido(ClubRequest request) {
        return request.getNombre() != null && !request.getNombre().isBlank();
    }

    public boolean nombreDuplicado(Long id, String nombre) {
        return clubRepository.existsByNombreAndIdNot(nombre, id);
    }

    private ClubResponse toResponse(Club club) {
        return new ClubResponse(
                club.getId(),
                club.getNombre(),
                club.getPais(),
                club.getEscudoUrl());
    }

}
