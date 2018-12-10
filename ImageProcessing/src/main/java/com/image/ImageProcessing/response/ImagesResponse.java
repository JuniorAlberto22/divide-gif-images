package com.image.ImageProcessing.response;

public class ImagesResponse {

	private String nomeGif;
	private String caminhoGif;
	private byte[][] imagesBytes;
	
	public ImagesResponse() {
		this.imagesBytes = new byte[0][];
	}

	public String getNomeGif() {
		return nomeGif;
	}

	public void setNomeGif(String nomeGif) {
		this.nomeGif = nomeGif;
	}

	public String getCaminhoGif() {
		return caminhoGif;
	}

	public void setCaminhoGif(String caminhoGif) {
		this.caminhoGif = caminhoGif;
	}

	public byte[][] getImagesBytes() {
		return imagesBytes;
	}

	public void setImagesBytes(byte[][] imagesBytes) {
		this.imagesBytes = imagesBytes;
	}

}
