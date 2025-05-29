package com.ecommerce.gerenciamento_pedidos.Repository;

import com.ecommerce.gerenciamento_pedidos.Model.Categoria;
import com.ecommerce.gerenciamento_pedidos.Model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByNome(String nome);


}
