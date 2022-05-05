package br.rafaelhorochovec.heroes.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.rafaelhorochovec.heroes.exception.FileStorageException;
import br.rafaelhorochovec.heroes.exception.MyFileNotFoundException;
import br.rafaelhorochovec.heroes.model.FileUpload;
import br.rafaelhorochovec.heroes.property.FileStorageProperties;
import br.rafaelhorochovec.heroes.repository.FileUploadRepository;
import br.rafaelhorochovec.heroes.util.Thumbnail;

@Service
public class FileStorageService {

	@Autowired
	private FileUploadRepository fileUploadRepository;

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public FileUpload storeFile(MultipartFile file) {
		// Normalize file name
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		String fileName = "";
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
			fileName = "img_upload_" + sdf.format(new Date()) + fileExtension;

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			// Resize upload image
			// FileInputStream fint = new FileInputStream("uploads/" + fileName);
			FileOutputStream fout = new FileOutputStream("uploads/" + fileName);
			Thumbnail.generateExactSize(file.getInputStream(), fout, 200, 200, 90, false);

			// BufferedImage originalImage = ImageIO.read(new File("uploads/" + fileName));
			// BufferedImage outputImage = ImageResize.resizeImageScalr(originalImage, 200,
			// 200);
			// ImageIO.write(outputImage, "png", new File("uploads/" + fileName));

			FileUpload newFile = new FileUpload(fileName, file.getContentType(), file.getBytes());
			return fileUploadRepository.save(newFile);
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
		}
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	public void deleteFileAsResource(String fileName) {
		Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
		try {
			// Delete file or directory
			Files.delete(filePath);
			System.out.println("File or directory deleted successfully");
		} catch (NoSuchFileException ex) {
			System.out.printf("No such file or directory: %s\n", fileName);
		} catch (DirectoryNotEmptyException ex) {
			System.out.printf("Directory %s is not empty\n", fileName);
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	public void deleteAllFiles() throws IOException {
		final Path root = Paths.get("uploads");
		Files.walk(root).filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
	}
}