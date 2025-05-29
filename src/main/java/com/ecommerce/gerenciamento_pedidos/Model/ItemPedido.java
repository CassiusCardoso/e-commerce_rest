package com.ecommerce.gerenciamento_pedidos.Model;

import jakarta.persistence.*;

@Entity(name="ITEM_PEDIDO")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantidade;
    private double precoUnitario;

    @ManyToOne
    @JoinColumn(name="pedido_id")
    private Pedido pedido;

    // EDIT 24/05 21:39 Adicionado para associar o produto
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    public ItemPedido() {}

    public ItemPedido(int quantidade, double precoUnitario, Produto produto) {
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.produto = produto;
    }
    public Long getId() {
        return id;
    }
    public double getPrecoUnitario() {
        return precoUnitario;
    }
    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    public Pedido getPedido() {
        return pedido;
    }
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    // EDIT 24/05 21:40 GET e SET de produto
    public Produto getProduto() {
        return produto;
    }
    public void SetProduto(Produto produto) {
        this.produto = produto;
    }
    public double calcularTotal() {
        return quantidade * precoUnitario;
    }
}
