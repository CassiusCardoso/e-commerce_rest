package com.ecommerce.gerenciamento_pedidos.Repository;

import com.ecommerce.gerenciamento_pedidos.Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String nome);

    String nome(String nome);
}
