package com.ecommerce.gerenciamento_pedidos.Model;

import com.ecommerce.gerenciamento_pedidos.Model.enums.StatusPedido;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity(name="PEDIDO")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double total;
    private LocalDateTime momentoPedido;

    @ManyToOne(fetch = FetchType.EAGER) // EDIT 28/05 10:52 Adicione fetch = FetchType.EAGER
    @JoinColumn(name="usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @OneToMany(mappedBy="pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {}
    public Pedido(double total, LocalDateTime momentoPedido, Usuario usuario, StatusPedido status) {
        this.total = total;
        this.momentoPedido = momentoPedido;
        this.usuario = usuario;
        this.status = status;
    }
    public Long getId() {
        return id;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public LocalDateTime getMomentoPedido() {
        return momentoPedido;
    }
    public void setMomentoPedido(LocalDateTime momentoPedido) {
        this.momentoPedido = momentoPedido;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public StatusPedido getStatus() {
        return status;
    }
    public void setStatus(StatusPedido status) {
        this.status = status;
    }
    public List<ItemPedido> getItens(){
        return itens;
    }
    public void atualizarStatus(StatusPedido status){
        this.status = status;
    }
    public double calcularTotal(){return itens.stream().mapToDouble(ItemPedido::calcularTotal).sum();
    }
    public void adicionarItem(ItemPedido item){
        itens.add(item);
        item.setPedido(this); // ItemCarrinho
    }

}

