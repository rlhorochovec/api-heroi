package br.rafaelhorochovec.heroi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaelhorochovec.heroi.model.FileUpload;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, String> {

}