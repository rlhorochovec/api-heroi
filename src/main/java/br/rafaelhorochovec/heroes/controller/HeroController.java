package br.rafaelhorochovec.heroes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.rafaelhorochovec.heroes.exception.ResourceNotFoundException;
import br.rafaelhorochovec.heroes.model.Hero;
import br.rafaelhorochovec.heroes.repository.HeroRepository;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class HeroController {

	@Autowired
	private HeroRepository heroRepository;

	@GetMapping("/heroes")
	public ResponseEntity<Map<String, Object>> read(@RequestParam(required = false) String name,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

		try {
			List<Hero> heroes = new ArrayList<Hero>();
			Pageable paging = PageRequest.of(page, size);

			Page<Hero> pageHeroes;
			if (name == null)
				pageHeroes = heroRepository.findAll(paging);
			else
				pageHeroes = heroRepository.findByNameContaining(name, paging);

			heroes = pageHeroes.getContent();

			Map<String, Object> response = new HashMap<>();
			response.put("heroes", heroes);
			response.put("currentPage", pageHeroes.getNumber());
			response.put("totalItems", pageHeroes.getTotalElements());
			response.put("totalPages", pageHeroes.getTotalPages());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/heroes/list")
	public ResponseEntity<List<Hero>> readAll() {
		List<Hero> heroes = heroRepository.findAll();
		return new ResponseEntity<>(heroes, HttpStatus.OK);
	}

	@GetMapping("/heroes/{id}")
	public ResponseEntity<Hero> getById(@PathVariable(value = "id") UUID heroId)
			throws ResourceNotFoundException {
		Hero hero = heroRepository.findById(heroId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroId));
		return ResponseEntity.ok().body(hero);
	}

	@PostMapping("/heroes")
	public ResponseEntity<Hero> create(@Valid @RequestBody Hero hero) {
		try {
			Hero _hero = heroRepository
					.save(new Hero(hero.getName(), hero.getCivil(), hero.getUniverse()));
			return new ResponseEntity<>(_hero, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/heroes/{id}")
	public ResponseEntity<Hero> update(@PathVariable(value = "id") UUID heroId,
			@Valid @RequestBody Hero heroRequest) throws ResourceNotFoundException {
		Hero hero = heroRepository.findById(heroId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroId));
		hero.setName(heroRequest.getName());
		hero.setCivil(heroRequest.getCivil());
		hero.setUniverse(heroRequest.getUniverse());
		final Hero updatedHero = heroRepository.save(hero);
		return ResponseEntity.ok(updatedHero);
	}

	@DeleteMapping("/heroes/{id}")
	public Map<String, Boolean> delete(@PathVariable(value = "id") UUID heroId) throws ResourceNotFoundException {
		Hero hero = heroRepository.findById(heroId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroId));

		heroRepository.delete(hero);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}

	@DeleteMapping("/heroes")
	public ResponseEntity<HttpStatus> deleteAll() {
		try {
			heroRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}