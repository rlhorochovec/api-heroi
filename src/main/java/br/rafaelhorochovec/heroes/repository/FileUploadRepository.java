package br.rafaelhorochovec.heroes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaelhorochovec.heroes.model.FileUpload;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, String> {

}