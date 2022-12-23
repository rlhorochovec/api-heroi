package br.rafaelhorochovec.heroes.repository;

import br.rafaelhorochovec.heroes.model.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HeroRepository extends JpaRepository<Hero, UUID> {
	List<Hero> findByOrderByNameAsc();
}