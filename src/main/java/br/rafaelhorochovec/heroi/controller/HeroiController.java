package br.rafaelhorochovec.heroi.controller;

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

import br.rafaelhorochovec.heroi.exception.ResourceNotFoundException;
import br.rafaelhorochovec.heroi.model.Heroi;
import br.rafaelhorochovec.heroi.repository.HeroiRepository;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class HeroiController {

	@Autowired
	private HeroiRepository heroiRepository;

	@GetMapping("/herois")
	public ResponseEntity<Map<String, Object>> getHerois(@RequestParam(required = false) String nome,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

		try {
			List<Heroi> herois = new ArrayList<Heroi>();
			Pageable paging = PageRequest.of(page, size);

			Page<Heroi> pageHeroes;
			if (nome == null)
				pageHeroes = heroiRepository.findAll(paging);
			else
				pageHeroes = heroiRepository.findByNomeContaining(nome, paging);

			herois = pageHeroes.getContent();

			Map<String, Object> response = new HashMap<>();
			response.put("herois", herois);
			response.put("currentPage", pageHeroes.getNumber());
			response.put("totalItems", pageHeroes.getTotalElements());
			response.put("totalPages", pageHeroes.getTotalPages());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/herois/all")
	public ResponseEntity<List<Heroi>> getAllHerois() {
		List<Heroi> herois = heroiRepository.findAll();
		return new ResponseEntity<>(herois, HttpStatus.OK);
	}

	@GetMapping("/herois/{id}")
	public ResponseEntity<Heroi> getHeroiById(@PathVariable(value = "id") UUID heroiId)
			throws ResourceNotFoundException {
		Heroi heroi = heroiRepository.findById(heroiId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroiId));
		return ResponseEntity.ok().body(heroi);
	}

	@PostMapping("/herois")
	public Heroi createHeroi(@Valid @RequestBody Heroi heroi) {
		return heroiRepository.save(heroi);
	}

	@PutMapping("/herois/{id}")
	public ResponseEntity<Heroi> updateHeroi(@PathVariable(value = "id") UUID heroiId,
			@Valid @RequestBody Heroi heroiRequest) throws ResourceNotFoundException {
		Heroi heroi = heroiRepository.findById(heroiId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroiId));
		heroi.setNome(heroiRequest.getNome());
		heroi.setNomeCivil(heroiRequest.getNomeCivil());
		heroi.setUniverso(heroiRequest.getUniverso());
		final Heroi updatedHeroi = heroiRepository.save(heroi);
		return ResponseEntity.ok(updatedHeroi);
	}

	@DeleteMapping("/herois/{id}")
	public Map<String, Boolean> deleteHeroi(@PathVariable(value = "id") UUID heroiId) throws ResourceNotFoundException {
		Heroi heroi = heroiRepository.findById(heroiId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroiId));

		heroiRepository.delete(heroi);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}

	@DeleteMapping("/herois/delete/all")
	public ResponseEntity<HttpStatus> deleteHerois() {
		try {
			heroiRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}