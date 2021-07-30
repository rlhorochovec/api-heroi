package br.rafaelhorochovec.heroi.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
public class Heroi extends Auditoria {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
	@Column(length = 36, nullable = false, updatable = false)
	@Type(type = "pg-uuid")
	private UUID id;
	private String nome;
	private String nomeCivil;
	private String universo;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeCivil() {
		return nomeCivil;
	}

	public void setNomeCivil(String nomeCivil) {
		this.nomeCivil = nomeCivil;
	}

	public String getUniverso() {
		return universo;
	}

	public void setUniverso(String universo) {
		this.universo = universo;
	}
}
