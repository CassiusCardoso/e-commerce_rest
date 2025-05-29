package com.ecommerce.gerenciamento_pedidos.Repository;

import com.ecommerce.gerenciamento_pedidos.Model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    List<Carrinho> findByUsuarioId (Long usuarioId);
}
