package com.ecommerce.gerenciamento_pedidos.Service;

import com.ecommerce.gerenciamento_pedidos.Model.Categoria;
import com.ecommerce.gerenciamento_pedidos.Repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Criar uma categoria
    public Categoria criarCategoria(Categoria categoria) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findByNome(categoria.getNome());
        if(categoriaExistente.isPresent()) {
            throw new IllegalArgumentException("Categoria já existe: " + categoria.getNome());
        }
        return categoriaRepository.save(categoria);
    }

    // Retornar a categoria pelo nome
    // EDIT 22/05 11:39 | O parâmetro correto é String nome e não Categoria categoria porque eu quero buscar pelo nome e não pelo objeto Categoria
    // EDIT 22/05 11:42 | O retorno foi alterado de Categoria para Optional<Categoria> e eu mexi na lógica
    public Categoria buscarCategoriaPorNome(String nome) {
        // EDIT 22/05 11:42 | Agora não precisa mais de tantas linhas, para verificar se a categoria existe, eu mudo o retorno para Optional, pois pode ou não reteornar o a categoria e coloco só um return pelo nome dela
        return categoriaRepository.findByNome(nome).orElseThrow(() -> new IllegalArgumentException("Categoria com o nome: " + nome + " não existe."));
    }

    // Buscar todas as categorias
    public List<Categoria> listarCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();
        if (categorias.isEmpty()) {
            throw new IllegalArgumentException("Categoria inexistente");
        }
        return categorias;
    }

    // Atualizar categoria
    public Categoria atualizarCategoria(Long id, Categoria categoria) {
        // EDIT 26/05 Validação para categorias duplicadas
        Categoria categoriaAtual = categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoria com ID: " + id + " não existe."));
        Optional<Categoria> categoriaComMesmoNome = categoriaRepository.findByNome(categoria.getNome());
        if(categoriaComMesmoNome.isPresent() && !categoriaComMesmoNome.get().getId().equals(id)) {
            throw new IllegalArgumentException("Categoria já existe com o nome: " + categoria.getNome());
        }
        categoriaAtual.setNome(categoria.getNome());
        categoriaRepository.save(categoriaAtual);
        return categoriaAtual;
    }

    // EDIT 22/05 11:40 - Método deletar categoria adicionado
    public void excluirCategoria(Long id) {
        if(!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("A categoria com esse id não existe: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    // EDIT 26/05 11:22
    public Categoria buscarCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoria com ID: " + id + " não existe."));
        return categoria;
    }
}
