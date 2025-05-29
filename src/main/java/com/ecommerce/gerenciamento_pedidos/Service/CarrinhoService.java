package com.ecommerce.gerenciamento_pedidos.Service;

import com.ecommerce.gerenciamento_pedidos.Model.*;
import com.ecommerce.gerenciamento_pedidos.Model.enums.StatusPedido;
import com.ecommerce.gerenciamento_pedidos.Repository.CarrinhoRepository;
import com.ecommerce.gerenciamento_pedidos.Repository.PedidoRepository;
import com.ecommerce.gerenciamento_pedidos.Repository.ProdutoRepository;
import com.ecommerce.gerenciamento_pedidos.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.events.Event;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CarrinhoService {

    // EDIT 22/05 - 11:06 - Adicionei o pedidoService para processar o pedido adequadamente (ex: validar estoque, salvar itens)
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private CarrinhoRepository carrinhoRepository;

    // EDIT 24/05 21:14 - Adicionado para persistir o pedido no banco
    @Autowired
    private PedidoRepository pedidoRepository;

    // EDIT 24/05 13:37
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Recupera um carrinho pelo id
    public Carrinho buscarCarrinhoPorId(Long id) {
        //EDIT 24/05 17:52 Utilizando o orElseThrow em vez de utilizar Optional
        // EDIT 24/05 17:52 - Em vez de fazer a lógica para verificar o id null aqui, eu vou fazer no controller
        return carrinhoRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Carrinho com ID: " + id + " não existe."));
    }
    // Adicionar um item ao carrinho
    // EDIT 21/05 - 12:28: Adicionar uma verificação se caso o carrinho já existir apenas atualizar a quantidade
    public Carrinho adicionarItemAoCarrinho(Long carrinhoId, Long produtoId, int quantidade) {
        // Buscar o carrinho
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não existe: " + carrinhoId + " ID."));

        // Buscar o produto e validar
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não existe: " + produtoId + " ID."));
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new IllegalArgumentException("Quantidade indisponível em estoque.");
        }

        // Criar o item
        double preco = produto.getPreco();
        ItemCarrinho item = new ItemCarrinho(quantidade, preco, produto);

        // Adicionar o item ao carrinho
        carrinho.adicionarItem(item);

        // Atualizar o estoque
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);

        // Salvar o carrinho
        return carrinhoRepository.save(carrinho);
    }

    // Remover um item do carrinho
    public Carrinho removerItemCarrinho(Long carrinhoId, Long itemId) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho com ID: " + carrinhoId + " não encontrado."));
        // EDIT 24/05 18:14 - verificação do id adicionada
        if (itemId == null) {
            throw new IllegalArgumentException("ID do item não pode ser nulo.");
        }
        // EDIT 24/05 18:14 - Adicionei uma verificação para encontrar o item e mexer na quantidade do estoque antes de remover
        // Encontrar o item antes de remover
        ItemCarrinho itemParaRemover = carrinho.getItens().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item com ID: " + itemId + " não existe."));
        // EDIT 24/05 18:14
        // Remover o item
        boolean itemRemovido = carrinho.removerItem(itemId);
        if (!itemRemovido) {
            throw new IllegalArgumentException("Item com ID: " + itemId + " não existe.");
        }
        // EDIT 24/05 18:14
        // Atualizar o estoque do produto
        Produto produto = itemParaRemover.getProduto();
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + itemParaRemover.getQuantidade());
        produtoRepository.save(produto);

        return carrinhoRepository.save(carrinho);
    }

    // Calcular o total
    // EDIT 24/05 20:43 - Alterei a estrutura do método para retornar uma resposta mais detalhada para o usuário, exibindo além de somente o valor total, mas com informações como: ID, produtos etc
    public Map<String, Object> calcularTotalCarrinho(Long carrinhoId){
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId).
                orElseThrow(() -> new IllegalArgumentException("Carrinho não existe"));
        double total = carrinho.getItens().stream().mapToDouble(ItemCarrinho::calcularTotal).sum();
        List<Map<String, Object>> itensDetalhados = carrinho.getItens().stream().map(item ->{
            Map<String, Object> itemDetalhado = new HashMap<>();
            itemDetalhado.put("id", item.getId());
            itemDetalhado.put("nome", item.getProduto().getNome());
            itemDetalhado.put("quantidade", item.getQuantidade());
            itemDetalhado.put("preco", item.getProduto().getPreco());
            return itemDetalhado;
        }).toList();
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("total", total);
        resposta.put("itens", itensDetalhados);
        return resposta;

    }

    // Checkout
    public Pedido checkout(Long carrinhoId, Long usuarioId) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não existe"));

        // EDIT 26/05 09:29 Validação de carrinho vazio
        if(carrinho.getItens().isEmpty()){
            throw new IllegalArgumentException("Carrinho está vazio");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não existe"));
        if (!carrinho.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Carrinho não pertence ao usuário informado.");
        }

        Map<String, Object> totalMap = calcularTotalCarrinho(carrinhoId);
        double total = (double) totalMap.get("total");
        LocalDateTime hora = LocalDateTime.now();

        Pedido pedido = new Pedido();
        pedido.setMomentoPedido(hora);
        pedido.setUsuario(usuario);
        pedido.setStatus(StatusPedido.PAGAMENTO_PENDENTE);
        pedido.setTotal(total);

        // Transferir itens do carrinho para o pedido
        List<ItemCarrinho> itensCarrinho = new ArrayList<>(carrinho.getItens());
        for (ItemCarrinho itemCarrinho : itensCarrinho) {
            // EDIT 09:31 - Revalidando o estoque
            Produto produto = itemCarrinho.getProduto();
            if(produto.getQuantidadeEstoque() < itemCarrinho.getQuantidade()){
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            ItemPedido itemPedido = new ItemPedido(itemCarrinho.getQuantidade(), itemCarrinho.getPrecoUnitario(), itemCarrinho.getProduto());
            pedido.adicionarItem(itemPedido);
        }

        // Limpar o carrinho após transferir os itens
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        // Persistir o pedido
        return pedidoRepository.save(pedido);
    }

    // EDIT 26/05 09:23 - Retorna todos os carrinhos por usuários
    public List<Carrinho> listarCarrinhosPorUsuario(Long usuarioId) {
        List<Carrinho>  carrinhos = carrinhoRepository.findByUsuarioId(usuarioId);
        if(usuarioId == null){
            throw new IllegalArgumentException("ID: " + usuarioId + " não existe. Busca por carrinhos cancelada.");
        }
        if(carrinhos.isEmpty()){
            throw new IllegalArgumentException("Nenhum carrinho encontrado para o usuário ID: " + usuarioId);
        }
        return carrinhos;
    }
}
