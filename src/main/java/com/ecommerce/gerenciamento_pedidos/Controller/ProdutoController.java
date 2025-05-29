package com.ecommerce.gerenciamento_pedidos.Controller;

import com.ecommerce.gerenciamento_pedidos.Model.Produto;
import com.ecommerce.gerenciamento_pedidos.Service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// 24/05 12:30
@RestController
@RequestMapping("/produto")
@Tag(name = "Produtos", description = "Endpoint para gerenciamento de produtos")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    // EDIT 26/05 10:13 Doc adicionada
    @Operation(summary = "Criar um produto", description = "Cria um pedido com as informações fornecidas pelo usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "400", description = "Produto já existe ou dados inválidos (preço/estoque negativos)"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody Produto produto){
        try{
            Produto produtoCriado = produtoService.criarProduto(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(produto);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(produto);
        }
    }

    // EDIT 26/05 10:14 - Doc adicionada
    @Operation(summary = "Excluir um produto", description = "Exclui um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id){
        try{
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 26/05 10:14 - Doc adicionada
    @Operation(summary = "Atualizar informações de um produto", description = "Atualiza as informações de um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "400", description = "Produto não encontrado ou dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@RequestBody Produto produto, @PathVariable Long id){
        try{
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // EDIT 26/05 10:16 - Doc adicionada
    @Operation(summary = "Buscar produto por nome", description = "Busca um produto com base no nome informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Produto> buscarProdutoPorNome(@PathVariable String nome){
        try{
           Optional<Produto> produtoExistente = produtoService.buscarProdutoPorNome(nome);
            return ResponseEntity.ok(produtoExistente.get());
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 26/05 10:14 - Doc adicionada
    @Operation(summary = "Buscar todos os produtos", description = "Busca todos os produtos existentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
     public ResponseEntity<List<Produto>> buscarTodosProdutos(){
        try{
           List<Produto> produtosOpt = produtoService.buscarTodos();
            return ResponseEntity.ok(produtosOpt);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // EDIT 26/05 10:07
    @Operation(summary = "Buscar produto por ID", description = "Busca um produto com base no ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id){
        try {
            Produto produto = produtoService.buscarProdutoPorId(id);
            return ResponseEntity.ok(produto);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
