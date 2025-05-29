package com.ecommerce.gerenciamento_pedidos.Service;

import com.ecommerce.gerenciamento_pedidos.Model.Pedido;
import com.ecommerce.gerenciamento_pedidos.Model.Usuario;
import com.ecommerce.gerenciamento_pedidos.Model.enums.StatusPedido;
import com.ecommerce.gerenciamento_pedidos.Repository.PedidoRepository;
import com.ecommerce.gerenciamento_pedidos.Repository.UsuarioRepository;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // EDIT 28/05 11:24 Adicionado usuarioRepository para persistir um usuário e associar ao pedido
    @Autowired
    private UsuarioRepository usuarioRepository;

    // EDIT 22/05 15h - Adicionado RabbitTemplate para se comunicar com o RabbitMQConfig
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.pedido-criado}")
    private String pedidoCriadoExchange;

    @Value("${spring.rabbitmq.routingkey.pedido-criado}")
    private String pedidoCriadoRoutingKey;

    @Value("${spring.rabbitmq.queue.pedido-criado}")
    private String pedidoCriadoQueue;

    // EDIT 28/05 11:25 Atualize o método criarPedido para buscar o Usuario pelo usuarioId e associá-lo ao Pedido antes de salvá-lo.
    public Pedido criarPedido(Long usuarioId, Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido está vazio.");
        }

        // Busca o usuário pelo ID
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        // Associa o usuário ao pedido
        pedido.setUsuario(usuario);
        pedido.setMomentoPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PAGAMENTO_CONFIRMADO);

        // Validação de estoque
        for (var item : pedido.getItens()) {
            var produto = item.getProduto();
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
        }

        // Calcula o total do pedido
        pedido.setTotal(pedido.calcularTotal());

        // Salva o pedido
        Pedido salvo = pedidoRepository.save(pedido);
        rabbitTemplate.convertAndSend(pedidoCriadoExchange, pedidoCriadoRoutingKey, salvo.getId());
        return salvo;
    }

    // Atualizar o status do pedido com base no StatusPedido
    public Pedido atualizaStatusPedido(Long id, StatusPedido status){
        // EDIT 22/05 - 11:26 | Retirei o Optional<Pedido>
        Pedido pedido = pedidoRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o ID: " + id));
        // EDIT 26/05 10:45
        // Validação para status nulo
        if(status == null){
            throw new IllegalArgumentException("Status não pode ser nulo");
        }
        // EDIT 22/05 - 11:26 | Validações para a transição do status do pedido
        if(pedido.getStatus() == null){
            throw new IllegalArgumentException("O atual status do pedido é vazio e não pode ser atualizado.");
        }
        if(pedido.getStatus() == StatusPedido.ENTREGUE && status != StatusPedido.PAGAMENTO_CANCELADO){
           throw new IllegalArgumentException("Pedido entregue não pode mudar para outro status além de PAGAMENTO_CANCELADO.");
        }
        pedido.setStatus(status);
        return pedidoRepository.save(pedido);
    }

    // Recupera um pedido específico
    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido com o ID: " + id + " não encontrado."));
    }

    // Deletar um pedido específico
    public void removerPedido(Long id){
        Optional<Pedido> pedidoOptional = pedidoRepository.findById(id);
        if(pedidoOptional.isEmpty()){
            throw new IllegalArgumentException("Pedido com o ID: " + id + "e, portanto não pode ser deletado");
        }
        pedidoRepository.deleteById(id);
        System.out.println("Pedido excluído com sucesso: " + id + " ID");
    }

    // Listar pedidos de um usuário
    public List<Pedido> buscarPedidosPorUsuario(Long usuarioId){
        // EDIT 22/05 - 11:31 O certo é List<Pedido> mesmo, e não Optional<Pedido>, porque senão iria retornar somente um pedido do usuário, e como um usuário pode ter vários pedidos
        List<Pedido> pedidosUsuario = pedidoRepository.findByUsuarioId(usuarioId);
        if(pedidosUsuario.isEmpty()){
            throw new IllegalArgumentException("Sem pedidos para o usuário ID: " + usuarioId);
        }
        return pedidosUsuario;
    }

    // EDIT 26/05 10:39
    public List<Pedido> listarPedidos(){
        List<Pedido> pedidos = pedidoRepository.findAll();
        if(pedidos.isEmpty()){
            throw new IllegalArgumentException("Nenhum pedido encontrado.");
        }
        return pedidos;
    }
}
