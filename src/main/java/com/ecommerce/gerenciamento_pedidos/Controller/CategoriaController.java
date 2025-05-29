package com.ecommerce.gerenciamento_pedidos.Controller;

import com.ecommerce.gerenciamento_pedidos.Model.Categoria;
import com.ecommerce.gerenciamento_pedidos.Service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// EDIT 26/05 11:31
@RestController
@RequestMapping("/categoria")
@Tag(name = "Categoria", description = "Endpoint para gerenciamento de categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;
    @Operation(summary = "Cria categoria", description = "Cria categorias para referenciar os produtos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
                    content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "400", description = "Categoria já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@RequestBody Categoria categoria){
        try{
            Categoria categoriaCriada = categoriaService.criarCategoria(categoria);
            return ResponseEntity.ok(categoriaCriada);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Busca uma categoria pelo seu nome", description = "Faz uma busca em categorias por nomes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Categoria> buscarCategoriaPorNome(@PathVariable String nome){
        try{
            Categoria categoria = categoriaService.buscarCategoriaPorNome(nome);
            return ResponseEntity.ok(categoria);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Listar todas as categorias", description = "Retorna todas as categorias")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma categoria encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias(){
        try{
            List<Categoria> categorias = categoriaService.listarCategorias();
            return ResponseEntity.ok(categorias);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Atualiza informações da categoria", description = "Atualiza as informações de uma categoria específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "400", description = "Categoria não encontrada ou nome já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@RequestBody Categoria categoria, @PathVariable Long id){
        try{
            Categoria categoriaAtt = categoriaService.atualizarCategoria(id, categoria);
            return ResponseEntity.ok(categoriaAtt);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Excluir categoria", description = "Exclui o registro da categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCategoria(@PathVariable Long id){
        try{
            categoriaService.excluirCategoria(id);
            return ResponseEntity.ok().build();
        }catch(IllegalArgumentException e ){
            return ResponseEntity.badRequest().build();
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    // EDIT 26/05 11:31
    @Operation(summary = "Busca uma categoria pelo ID", description = "Retorna os detalhes da categoria especificada pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoriaPorId(@PathVariable Long id){
        try{
            Categoria categoria = categoriaService.buscarCategoriaPorId(id);
            return ResponseEntity.ok(categoria);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
