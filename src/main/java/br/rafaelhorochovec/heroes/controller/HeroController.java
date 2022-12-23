package br.rafaelhorochovec.heroes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Id;
import javax.transaction.Transactional;

import br.rafaelhorochovec.heroes.service.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.rafaelhorochovec.heroes.exception.ResourceNotFoundException;
import br.rafaelhorochovec.heroes.model.FileInfo;
import br.rafaelhorochovec.heroes.model.Hero;
import br.rafaelhorochovec.heroes.repository.HeroRepository;
import br.rafaelhorochovec.heroes.service.FilesStorageService;

@Transactional
@CrossOrigin
@RestController
@RequestMapping("/api")
public class HeroController {

	@Autowired
	private HeroService heroService;

	// GET
	@GetMapping("/heroes")
	public List<Hero> listHeroes() {
		return heroService.getHeroes();
	}

	// GET BY ID
	@GetMapping("/heroes/{id}")
	public Hero getHero(@PathVariable(value = "id") UUID id) {
		return heroService.getById(id);
	}

	// POST
	@PostMapping("/heroes")
	public Hero createHero(@RequestPart Hero hero, @RequestPart MultipartFile file) {
		return heroService.createHero(hero, file);
	}

	// PUT
	@PutMapping("/heroes/{id}")
	public Hero updateHero(@PathVariable(value = "id") UUID id, @RequestPart Hero heroDetails, @RequestPart MultipartFile file) {
		return heroService.updateHero(id, heroDetails, file);
	}

	// DELETE
	@DeleteMapping("/heroes/{id}")
	public void deletehero(@PathVariable(value = "id") UUID id) {
		heroService.deleteHero(id);
	}
}