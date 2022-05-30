package br.rafaelhorochovec.heroes.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaelhorochovec.heroes.model.Hero;

@Repository
public interface HeroRepository extends JpaRepository<Hero, UUID> {
	Page<Hero> findByNameContaining(String name, Pageable pageable);
	List<Hero> findByOrderByNameAsc();
}