package br.rafaelhorochovec.heroes.service;

import br.rafaelhorochovec.heroes.model.FileInfo;
import br.rafaelhorochovec.heroes.model.Hero;
import br.rafaelhorochovec.heroes.repository.HeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class HeroService {

    @Autowired
    HeroRepository heroRepository;

    @Autowired
    private FilesStorageService storageService;

    public HeroService(FilesStorageService storageService) {
        this.storageService = storageService;
    }

    // CREATE 
    public Hero createHero(Hero hero, MultipartFile file) {
        if (!file.isEmpty()) {
            FileInfo fileUpload = storageService.save(file);
            hero.setImage(fileUpload.getName());
            hero.setImagePath(fileUpload.getUrl());
        }
        return heroRepository.save(hero);
    }

    // READ
    public List<Hero> getHeroes() {
        return heroRepository.findByOrderByNameAsc();
    }

    // UPDATE
    public Hero updateHero(UUID id, Hero heroDetails, MultipartFile file) {
        Hero hero = heroRepository.findById(id).get();
        if (!file.isEmpty()) {
            FileInfo fileUpload = storageService.save(file);
            hero.setImage(fileUpload.getName());
            hero.setImagePath(fileUpload.getUrl());
        }
        hero.setName(heroDetails.getName());
        hero.setCivil(heroDetails.getCivil());
        hero.setUniverse(heroDetails.getUniverse());
        return heroRepository.save(hero);
    }

    // GET BY ID
    public Hero getById(UUID id) {
        Hero hero = heroRepository.findById(id).get();
        return heroRepository.getOne(id);
    }

    // DELETE
    public void deleteHero(UUID id) {
        heroRepository.deleteById(id);
    }
}
