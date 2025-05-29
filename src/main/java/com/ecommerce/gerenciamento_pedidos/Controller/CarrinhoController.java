package com.ecommerce.gerenciamento_pedidos.Controller;

import com.ecommerce.gerenciamento_pedidos.Model.Carrinho;
import com.ecommerce.gerenciamento_pedidos.Model.Pedido;
import com.ecommerce.gerenciamento_pedidos.Model.Produto;
import com.ecommerce.gerenciamento_pedidos.Model.Usuario;
import com.ecommerce.gerenciamento_pedidos.Service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/carrinho")
@Tag(name = "Carrinhos", description ="Endpoint para gereciamento de carrinhos de pedido")
public class CarrinhoController {
    @Autowired
    private CarrinhoService carrinhoService;

    // EDIT 26/05 09:43 Doc adicionada e rota alterada de /carrinho/{idCarrinho}/itens para a atual
    @Operation(summary = "Adicionar item ao carrinho", description = "Adiciona um item ao carrinho com base no ID do carrinho e do produto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado ao carrinho com sucesso",
                    content = @Content(schema = @Schema(implementation = Carrinho.class))),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Quantidade indisponível em estoque"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/{idCarrinho}/itens")
    public ResponseEntity<Carrinho> adicionarItemCarrinho(@PathVariable Long idCarrinho, @RequestParam Long produtoId, @RequestParam int quantidade) {
        try {
            if (quantidade <= 0) {
                return ResponseEntity.badRequest().body(null);
            }
            Carrinho carrinhoAtualizado = carrinhoService.adicionarItemAoCarrinho(idCarrinho, produtoId, quantidade);
            return new ResponseEntity<>(carrinhoAtualizado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Quantidade indisponível")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 409 Conflict
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Buscar carrinho por id", description = "Busca um carrinho com base no ID informado pelo cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Carrinho.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido (nulo)"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Carrinho> buscarCarrinhoPorId(@PathVariable Long id){
        try{
            if(id == null){
                return ResponseEntity.badRequest().body(null);
            }
            Carrinho carrinhoOpt = carrinhoService.buscarCarrinhoPorId(id);
            return ResponseEntity.ok(carrinhoOpt);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Remove item do carrinho", description = "Remove item específico do carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removido do carrinho com sucesso",
                    content = @Content(schema = @Schema(implementation = Carrinho.class))),
            @ApiResponse(responseCode = "400", description = "ID do carrinho ou item inválido (nulo)"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}/itens/{itemId}")
    public ResponseEntity<Carrinho> removerItemCarrinho(@PathVariable Long id, @PathVariable Long itemId){
        try{
            if(id == null || itemId == null){
                return ResponseEntity.badRequest().body(null);
            }
            Carrinho carrinhoAtualizado = carrinhoService.removerItemCarrinho(id, itemId);
            return ResponseEntity.ok(carrinhoAtualizado);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Calcula o total do carrinho", description = "Calcula o total do carrinho com base em todos os itens compostos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total do carrinho calculado com sucesso",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/calcula/{id}")
    public ResponseEntity<Map<String, Object>> calcularTotalCarrinho(@PathVariable Long id){
        try{
            if(id == null){
                return ResponseEntity.badRequest().body(null);
            }
           Map<String, Object> resultado = carrinhoService.calcularTotalCarrinho(id);
            return ResponseEntity.ok(resultado);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @Operation(summary = "Realiza o checkout do carrinho", description = "Processa o checkout do carrinho, criando um pedido com base no carrinho e usuário informados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checkout realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "400", description = "ID do carrinho ou usuário inválido"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/checkout/{carrinhoId}/{usuarioId}")
    public ResponseEntity<Pedido> verificarLogin(@PathVariable Long carrinhoId, @PathVariable Long usuarioId){
       try{
           if(carrinhoId == null || usuarioId == null){
               return ResponseEntity.badRequest().body(null);
           }
        Pedido pedido = carrinhoService.checkout(carrinhoId, usuarioId);
        return ResponseEntity.ok(pedido);
    }catch(IllegalArgumentException exception){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
       }catch(Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
    }

    // EDIT 26/05 09:44 Método para listar carrinhos por usuário
    @Operation(summary = "Lista carrinhos por usuário", description = "Retorna a lista de carrinhos associados a um usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de carrinhos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Carrinho.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum carrinho encontrado para o usuário"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Carrinho>> listarCarrinhosPorUsuario(@PathVariable Long usuarioId){
        try{
            List<Carrinho> carrinhos = carrinhoService.listarCarrinhosPorUsuario(usuarioId);
            return ResponseEntity.ok(carrinhos);
        }catch(IllegalArgumentException exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
