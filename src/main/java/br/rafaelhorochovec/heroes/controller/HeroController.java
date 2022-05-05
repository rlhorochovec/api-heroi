package br.rafaelhorochovec.heroes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.rafaelhorochovec.heroes.exception.ResourceNotFoundException;
import br.rafaelhorochovec.heroes.model.FileUpload;
import br.rafaelhorochovec.heroes.model.Hero;
import br.rafaelhorochovec.heroes.repository.HeroRepository;
import br.rafaelhorochovec.heroes.service.FileStorageService;

@Transactional
@CrossOrigin
@RestController
@RequestMapping("/api")
public class HeroController {

	@Autowired
	private HeroRepository heroRepository;

	@Autowired
	private FileStorageService fileStorageService;

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
		List<Hero> heroes = heroRepository.findByOrderByNameAsc();
		return new ResponseEntity<>(heroes, HttpStatus.OK);
	}

	@GetMapping("/heroes/{id}")
	public ResponseEntity<Hero> getById(@PathVariable(value = "id") UUID heroId) throws ResourceNotFoundException {
		Hero hero = heroRepository.findById(heroId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroId));
		return ResponseEntity.ok().body(hero);
	}

	@RequestMapping(value = "/heroes", method = RequestMethod.POST, produces = "application/json", consumes = "multipart/form-data")
	public ResponseEntity<Hero> create(@RequestPart Hero hero, @RequestPart MultipartFile image) {
		String fileDownloadUri = null;
		try {
			if(!image.isEmpty()) {
				FileUpload newFile = fileStorageService.storeFile(image);
				fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/heroes/view/")
						.path(newFile.getName()).toUriString();
				hero.setImage(newFile);
			}

			Hero _hero = heroRepository.save(
					new Hero(hero.getName(), hero.getCivil(), hero.getUniverse(), hero.getImage(), fileDownloadUri));
			return new ResponseEntity<>(_hero, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/heroes/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "multipart/form-data")
	public ResponseEntity<Hero> update(@PathVariable(value = "id") UUID heroId, @RequestPart Hero heroRequest,
			@RequestPart MultipartFile image) throws ResourceNotFoundException {
		Hero hero = heroRepository.findById(heroId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroId));

		if(!image.isEmpty()) {
			fileStorageService.deleteFileAsResource(hero.getImage().getName());
			FileUpload newFile = fileStorageService.storeFile(image);
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/heroes/view/")
					.path(newFile.getName()).toUriString();
			hero.setImage(newFile);
			hero.setImagePath(fileDownloadUri);
		}
		
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
		fileStorageService.deleteFileAsResource(hero.getImage().getName());
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}

	@DeleteMapping("/heroes")
	public ResponseEntity<HttpStatus> deleteAll() {
		try {
			heroRepository.deleteAll();
			fileStorageService.deleteAllFiles();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}