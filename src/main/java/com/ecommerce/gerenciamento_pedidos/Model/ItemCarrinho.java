package com.ecommerce.gerenciamento_pedidos.Model;

import jakarta.persistence.*;

@Entity(name="ITEM_CARRINHO")
public class ItemCarrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantidade;
    private double precoUnitario;

    @ManyToOne
    @JoinColumn(name="carrinho_id")
    private Carrinho carrinho;

    @ManyToOne
    @JoinColumn(name="produto_id")
    private Produto produto;

    public ItemCarrinho() {}
    public ItemCarrinho(int quantidade, double precoUnitario, Produto produto) {
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
        this.produto = produto;
    }
    public Long getId() {
        return id;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    public double getPrecoUnitario() {
        return precoUnitario;
    }
    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
    public Carrinho getCarrinho() {
        return carrinho;
    }
    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }
    public Produto getProduto() {
        return produto;
    }
    public void setProduto(Produto produto) {
        this.produto = produto;
    }
    public double calcularTotal() {
        return quantidade * precoUnitario;
    }
}
