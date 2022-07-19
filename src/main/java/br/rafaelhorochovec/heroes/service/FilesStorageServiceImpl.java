package br.rafaelhorochovec.heroes.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.rafaelhorochovec.heroes.exception.FileStorageException;
import br.rafaelhorochovec.heroes.model.FileInfo;
import br.rafaelhorochovec.heroes.util.Thumbnail;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

	private final Path root = Paths.get("uploads");

	@Override
	public void init() {
		try {
			Files.createDirectory(root);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	public FileInfo save(MultipartFile file) {
		// Normalize file name
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		String fileName = "";
		String fileUrl = "";
		try {
			// Check if the file's name contains invalid characters
			if (originalFileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalFileName);
			}

			String fileExtension = "";
			try {
				fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			} catch (Exception e) {
				fileExtension = "";
			}

			// Rename file
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
			fileName = "img_" + sdf.format(new Date()) + fileExtension;
			fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/heroes/files/").path(fileName)
					.toUriString();

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.root.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			// Resize upload image
			FileOutputStream fout = new FileOutputStream("uploads/" + fileName);
			Thumbnail.generateExactSize(file.getInputStream(), fout, 200, 200, 90, false);

			return new FileInfo(fileName, fileUrl);
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
		}
	}

	@Override
	public Resource loadFile(String filename) {
		try {
			Path file = root.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(root.toFile());
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
		} catch (IOException e) {
			throw new RuntimeException("Could not load the files!");
		}
	}
}