package com.ecommerce.gerenciamento_pedidos.Service;

import com.ecommerce.gerenciamento_pedidos.Model.Produto;
import com.ecommerce.gerenciamento_pedidos.Repository.ProdutoRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;

    // Criar um produto
    public Produto criarProduto(Produto produto){
        // EDIT 22/05 11:04 - Removi o Optional e coloquei essa validação
        if(produtoRepository.findByNome(produto.getNome()).isPresent()){
            throw new IllegalArgumentException("Produto já existe com o nome: " + produto.getNome());
        }
        // EDIT 22/05 11:04 - Adicionei validação do preço para não ser negativo e do estoque também
        if(produto.getPreco() < 0 || produto.getQuantidadeEstoque() < 0){
            throw new IllegalArgumentException("Preço e quantidade em estoque devem ser não negativos:");
        }
        return produtoRepository.save(produto);
    }

    // Buscar todos produtos
    public List<Produto> buscarTodos(){
        return produtoRepository.findAll();
    }

    // Buscar produto por nome
    public Optional<Produto> buscarProdutoPorNome(String nome){
        Optional<Produto> produtoExistente = produtoRepository.findByNome(nome);
        if(produtoExistente.isEmpty()){
            throw new IllegalArgumentException("Produto com o nome: " + nome + " não existe.");
        }
        return produtoExistente;
    }


    // Deletar produto
    public void deletarProduto(Long id){
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        if(produtoExistente.isEmpty()){
            throw new IllegalArgumentException("O produto que você tentou excluir não existe");
        }
        produtoRepository.deleteById(id);
    }

    // Atualizar produto
    // EDIT 26/05 10:04 - Modifiquei a lógica para instanciar um Produto produto em vez de Optional<Produto>
    public Produto atualizarProduto(Long id, Produto novoProduto){
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Produto com ID: " + id + " não existe."));

        if(produtoRepository.findByNome(novoProduto.getNome()).isPresent() && !produto.getNome().equals(novoProduto.getNome())){
            throw new IllegalArgumentException("Produto já existe com o nome: " + novoProduto.getNome());
        }
        if(produto.getPreco() < novoProduto.getPreco()){
            throw new IllegalArgumentException("Produto está com preço negativo");
        }
        produto.setNome(novoProduto.getNome());
        produto.setDescricao(novoProduto.getDescricao());
        produto.setPreco(novoProduto.getPreco());
        produto.setQuantidadeEstoque(novoProduto.getQuantidadeEstoque());
        return produtoRepository.save(produto);
    }

    public Produto buscarProdutoPorId(Long id){
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Produto com ID: " + id + " não existe."));
        if(id == null){
            throw new IllegalArgumentException("Produto com ID: " + id + " não existe.");
        }
        return produto;
   }
}
