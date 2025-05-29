package com.ecommerce.gerenciamento_pedidos.Controller;

import com.ecommerce.gerenciamento_pedidos.Model.Usuario;
import com.ecommerce.gerenciamento_pedidos.Service.UsuarioService;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "Endpoint para gerenciamento de usuários")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    // EDIT 23/05 15:21 - 200 é para retornar quando a busca for feita com sucesso, mas não conter nenhum retorno
    // 201 retornar quando a busca foi feita com sucesso e criou um recurso
    @Operation(summary = "Cria um usuário", description = "Cria um usuário com base nas informações coletadas")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
            content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Usuário não criado!"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario){
        try{
            Usuario usuarioCriado = usuarioService.criarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // EDIT 25/05 00:03
    @Operation(summary = "Lista todos os usuários", description = "Retorna a lista de todos os usuários registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(){
        try {
            List<Usuario> usuarios = usuarioService.buscarTodosUsuarios();
            return ResponseEntity.status(HttpStatus.OK).body(usuarios);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Buscar usuário pelo nome", description = "Busca um usuário pelo nome informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário retornado com sucesso!",
            content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Nome inválido, usuário não encontrado!"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    @GetMapping("/{nome}")
    // EDIT 23/05 15:54 | Método alterado de ResponseEntity<Optional<Usuario>> para ResponseEntity<Usuario>
    public ResponseEntity<Usuario> buscarUsuarioPorNome(@PathVariable String nome){
        try{
            Optional<Usuario> usuarioOptional = usuarioService.buscarUsuarioPorNome(nome);
            return ResponseEntity.ok(usuarioOptional.get()); // EDIT 230/05 15:56 | Já sabemos que existe, pois o serviço lança exceção se não existir
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // EDIT 25/05 00:06
    @Operation(summary = "Buscar usuário pelo ID", description = "Busca um usuário pelo ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        try {
            Usuario usuarioOptional = usuarioService.buscarUsuarioPorId(id); //
            return ResponseEntity.ok(usuarioOptional);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Buscar usuário pelo e-mail", description = "Busca um usuário pelo e-mail informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@PathVariable String email){
        try{
            Usuario usuario = usuarioService.buscarUsuarioPeloEmail(email);
            return ResponseEntity.ok(usuario);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Verifica o login", description = "Verifica as credenciais do login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Dados de login inválidos (email ou senha vazios)"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<Boolean> verificaLogin(@RequestBody Map<String, String> loginData){
        try{
            // Extrair email e senha do corpo da request
            String email = loginData.get("email");
            String senha = loginData.get("senha");

            // Validação
            if(email == null || email.trim().isEmpty() || senha  == null || senha.trim().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
            }
            // Chamar  o serviço para verificar o login
            boolean loginValido = usuarioService.verificarLogin(email, senha);
            return loginValido? ResponseEntity.ok(true): ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
    @Operation(summary = "Deletar usuário", description = "Exclui um registro de usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido excluído com sucesso!",
            content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não existe no servidor"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id){
        try{
            usuarioService.deletarUsuario(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Atualizar um usuário", description = "Atualiza as informações do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@RequestBody Usuario usuario, @PathVariable Long id){
        try{
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
