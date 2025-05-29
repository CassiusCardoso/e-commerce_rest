package com.ecommerce.gerenciamento_pedidos.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name= "CARRINHO")
public class Carrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name= "usuario_id")
    private Usuario usuario;

    // Criando a lista
    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinho> itens = new ArrayList<>();

    public Carrinho() {}
    public Carrinho(Usuario usuario) {
        this.usuario = usuario;
    }
    public Long getId() {
        return id;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void adicionarItem(ItemCarrinho item) {
        itens.add(item);
        item.setCarrinho(this);
    }
    // EDIT 24/05 18:11 - Alterei o retorno de void para boolean
    public boolean removerItem(Long itemId) {
        return itens.removeIf(item -> item.getId().equals(itemId));
    }

}
