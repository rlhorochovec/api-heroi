package br.rafaelhorochovec.heroi.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaelhorochovec.heroi.model.Heroi;

@Repository
public interface HeroiRepository extends JpaRepository<Heroi, UUID> {

}