package br.rafaelhorochovec.heroi.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaelhorochovec.heroi.exception.ResourceNotFoundException;
import br.rafaelhorochovec.heroi.model.Heroi;
import br.rafaelhorochovec.heroi.repository.HeroiRepository;

@RestController
@RequestMapping("/api")
public class HeroiController {

	@Autowired
	private HeroiRepository heroiRepository;

	@GetMapping("/herois")
	public Page<Heroi> getAllHerois(Pageable pageable) {
		return heroiRepository.findAll(pageable);
	}
	
	@GetMapping("/herois/{id}")
	public ResponseEntity<Heroi> getHeroiById(@PathVariable(value = "id") Long heroiId)
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
	public ResponseEntity<Heroi> updateHeroi(@PathVariable(value = "id") Long heroiId,
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
	public Map<String, Boolean> deleteHeroi(@PathVariable(value = "id") Long heroiId) throws ResourceNotFoundException {
		Heroi heroi = heroiRepository.findById(heroiId)
				.orElseThrow(() -> new ResourceNotFoundException("Não existe herói com o id: " + heroiId));

		heroiRepository.delete(heroi);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
}