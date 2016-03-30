package com.pacote.wikieat.cardapio;

import android.graphics.Bitmap;

public class ItemListViewCardapio {
	private String id;
	private String nome;
	private String preco;
	private String descricao;
	private Bitmap urlImagem;
	
	
	public ItemListViewCardapio() {
		
	}


	public ItemListViewCardapio(String id,String nome, String preco, String descricao,
			Bitmap urlImagem) {
		this.id = id;
		this.nome = nome;
		this.preco = preco;
		this.descricao = descricao;
		this.urlImagem = urlImagem;
	}


	public String getNome() {
		return nome;
	}
	
	


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getPreco() {
		return preco;
	}


	public void setPreco(String preco) {
		this.preco = preco;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public Bitmap getUrlImagem() {
		return urlImagem;
	}


	public void setUrlImagem(Bitmap urlImagem) {
		this.urlImagem = urlImagem;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
	
	
	
	
}
