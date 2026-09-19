package com.uade.e_commerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ClubRequest;
import com.uade.e_commerce.dto.ClubResponse;
import com.uade.e_commerce.exception.RecursoDuplicadoException;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
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

    public List<ClubResponse> listar(String pais) {
        List<Club> clubes = pais == null ? clubRepository.findAll() : clubRepository.findByPais(pais);
        return clubes.stream().map(this::toResponse).toList();
    }

    public ClubResponse obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    public ClubResponse crear(ClubRequest request) {
        if (clubRepository.existsByNombre(request.getNombre())) {
            throw new RecursoDuplicadoException("Ya existe un club con el nombre " + request.getNombre());
        }

        Club club = new Club();
        club.setNombre(request.getNombre());
        club.setPais(request.getPais());
        club.setEscudoUrl(request.getEscudoUrl());

        return toResponse(clubRepository.save(club));
    }

    public ClubResponse actualizar(Long id, ClubRequest datos) {
        Club club = buscar(id);
        if (clubRepository.existsByNombreAndIdNot(datos.getNombre(), id)) {
            throw new RecursoDuplicadoException("Ya existe un club con el nombre " + datos.getNombre());
        }

        club.setNombre(datos.getNombre());
        club.setPais(datos.getPais());
        club.setEscudoUrl(datos.getEscudoUrl());

        return toResponse(clubRepository.save(club));
    }

    public void eliminar(Long id) {
        Club club = buscar(id);
        clubRepository.delete(club);
    }

    private Club buscar(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Club " + id + " no encontrado"));
    }

    private ClubResponse toResponse(Club club) {
        return new ClubResponse(
                club.getId(),
                club.getNombre(),
                club.getPais(),
                club.getEscudoUrl());
    }

}
