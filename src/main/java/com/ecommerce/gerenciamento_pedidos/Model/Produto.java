package com.ecommerce.gerenciamento_pedidos.Model;

import jakarta.persistence.*;

@Entity(name = "PRODUTO")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private double preco;
    private int quantidadeEstoque;

    public Produto() {}
    public Produto(String nome, String descricao, double preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    @ManyToOne // Indica quais são as instâncias de relacionamento
    @JoinColumn(name="categoria_id") // Indica como será o nome do atributo categoria na tabela Produto (como é uma chave estrangeira fica o nome_id)
    private Categoria categoria;

    // Não precisa de setId porque o ID é gerado automaticamente pelo JPA
    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public double getPreco() {
        return preco;
    }
    public void setPreco(double preco) {
        this.preco = preco;
    }
    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
    public Categoria getCategoria() {
        return categoria;
    }
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void atualizarEstoque(int quantidade){
        this.quantidadeEstoque = quantidade;
    }

}
