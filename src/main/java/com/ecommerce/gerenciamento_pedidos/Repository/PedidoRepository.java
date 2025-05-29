package com.ecommerce.gerenciamento_pedidos.Repository;

import com.ecommerce.gerenciamento_pedidos.Model.Pedido;
import com.ecommerce.gerenciamento_pedidos.Model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<StatusPedido> findByStatus(StatusPedido status); // Retorna o pedido de acordo com o status
    // EDIT 22/05 - 11:18 - Consulta adicionada para o método criarPedido no PedidoService
    Optional<Pedido> findByUsuarioIdAndMomentoPedido(Long usuarioId, Date momentoPedido);
    List<Pedido> findByUsuarioId(Long usuarioId);


}
