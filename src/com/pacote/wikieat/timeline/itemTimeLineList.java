package com.pacote.wikieat.timeline;

import android.graphics.Bitmap;



public class itemTimeLineList {
	private String nomeRestaurante;
    private String nomePrato;
    private String nomeUsuario;
    private Bitmap urlImagemRestaurante;
    private Bitmap urlImagemPrato;
    private Bitmap urlImagemUsuario;
    private String acaoDoUsuario;
	
    
    public itemTimeLineList() {
		
	}


	public itemTimeLineList(String nomeRestaurante, String nomePrato,
			String nomeUsuario, Bitmap urlImagemRestaurante,
			Bitmap urlImagemPrato, Bitmap urlImagemUsuario, String acaoDoUsuario) {
		super();
		this.nomeRestaurante = nomeRestaurante;
		this.nomePrato = nomePrato;
		this.nomeUsuario = nomeUsuario;
		this.urlImagemRestaurante = urlImagemRestaurante;
		this.urlImagemPrato = urlImagemPrato;
		this.urlImagemUsuario = urlImagemUsuario;
		this.acaoDoUsuario = acaoDoUsuario;
	}


	public Bitmap getUrlImagemUsuario() {
		return urlImagemUsuario;
	}


	public void setUrlImagemUsuario(Bitmap urlImagemUsuario) {
		this.urlImagemUsuario = urlImagemUsuario;
	}


	public String getNomeRestaurante() {
		return nomeRestaurante;
	}


	public void setNomeRestaurante(String nomeRestaurante) {
		this.nomeRestaurante = nomeRestaurante;
	}


	public String getNomePrato() {
		return nomePrato;
	}


	public void setNomePrato(String nomePrato) {
		this.nomePrato = nomePrato;
	}


	public String getNomeUsuario() {
		return nomeUsuario;
	}


	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}


	public Bitmap getUrlImagemRestaurante() {
		return urlImagemRestaurante;
	}


	public void setUrlImagemRestaurante(Bitmap urlImagemRestaurante) {
		this.urlImagemRestaurante = urlImagemRestaurante;
	}


	public String getAcaoDoUsuario() {
		return acaoDoUsuario;
	}


	public void setAcaoDoUsuario(String acaoDoUsuario) {
		this.acaoDoUsuario = acaoDoUsuario;
	}


	public Bitmap getUrlImagemPrato() {
		return urlImagemPrato;
	}


	public void setUrlImagemPrato(Bitmap urlImagemPrato) {
		this.urlImagemPrato = urlImagemPrato;
	}
	
	
	
	
    
    
    
    

    
}
