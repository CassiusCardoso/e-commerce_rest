package com.ecommerce.gerenciamento_pedidos.Controller;

import com.ecommerce.gerenciamento_pedidos.Model.Pedido;
import com.ecommerce.gerenciamento_pedidos.Model.enums.StatusPedido;
import com.ecommerce.gerenciamento_pedidos.Service.PedidoService;
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

@RestController
@RequestMapping("/pedido")
// Swagger - Doc 22/05 15:22
@Tag(name = "Pedidos", description = "Endpoint para gerenciamento de pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Cria um pedido", description = "Cria um pedido de acordo com os dados informados pelo cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso!",
            content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    // EDIT 28/05 11:31 Mudança: Adiciona @RequestParam Long usuarioId para que o cliente passe o ID do usuário na requisição (por exemplo, POST /pedido?usuarioId=1).
    public ResponseEntity<Pedido> criaPedido(@RequestParam Long usuarioId, @RequestBody Pedido pedido) {
        try{
            Pedido pedidoCriado = pedidoService.criarPedido(usuarioId, pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCriado);

        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @Operation(summary="Busca um pedido pelo ID", description = "Retorna os detalhes do pedido especificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca feita com sucesso!",
            content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    // PathVariable para receber um valor informado pelo cliente, que no caso é um Long id
    public ResponseEntity<Pedido> buscarPedidoClientePorId(@PathVariable Long id){
        // Nesse caso o Optional<Pedido> pedidoBuscado é o correto, porque eu posso não ter um pedido com o id informado
        try{
            Pedido pedidoBuscado = pedidoService.buscarPedidoPorId(id);
            return ResponseEntity.ok(pedidoBuscado);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Lista pedidos de um usuário", description = "Retorna todos os pedidos de um usuário específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum pedido encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> buscarPedidosPorUsuario(@PathVariable Long usuarioId){
       try {
           List<Pedido> pedidos = pedidoService.buscarPedidosPorUsuario(usuarioId);
           return ResponseEntity.ok(pedidos);
       }catch (IllegalArgumentException e){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
       }catch(Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
    }

    @Operation(summary = "Atualizar status do pedido", description = "Altera o status do pedido existente, informando o id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}/status")
    // Ele exige um PathVariable, ou seja, um valor que o usuário precisa informar, que no caso é o ID do pedido, e ele retorna no RequestBody (corpo da requisição) o status atualizado
    public ResponseEntity<Pedido> atualizarStatus(@PathVariable Long id, @RequestBody StatusPedido statusPedidoAtualizado){
        try {
            Pedido pedidoAtualizadoStatus = pedidoService.atualizaStatusPedido(id, statusPedidoAtualizado);
            return ResponseEntity.ok(pedidoAtualizadoStatus);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Remove um pedido", description = "Remove um pedido com base no id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido removido com sucesso!",
            content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPedido(@PathVariable Long id){
       try{
           pedidoService.removerPedido(id);
           return ResponseEntity.noContent().build();
       }catch (IllegalArgumentException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
    }
    // EDIT 26/05 10:48 Endpoint para listar todos pedidos adicionado
    @Operation(summary = "Listar todos pedidos", description = "Retorna todos os pedidos existentes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum pedido encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos(){
        try{
            List<Pedido> pedidos = pedidoService.listarPedidos();
            return ResponseEntity.ok(pedidos);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
