package br.rafaelhorochovec.heroes.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import br.rafaelhorochovec.heroes.model.FileInfo;

public interface FilesStorageService {
	public void init();

	public FileInfo save(MultipartFile file);

	public Resource loadFile(String filename);

	public Stream<Path> loadAll();

	public void deleteAll();
}