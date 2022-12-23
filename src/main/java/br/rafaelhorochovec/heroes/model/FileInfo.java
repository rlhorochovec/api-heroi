package br.rafaelhorochovec.heroes.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileInfo {

	private String name;
	private String url;

	public FileInfo(String fileName, String fileUrl) {
		this.name = fileName;
		this.url = fileUrl;
	}
}
