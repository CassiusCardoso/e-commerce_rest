package com.ecommerce.gerenciamento_pedidos.Service;

import com.ecommerce.gerenciamento_pedidos.Model.Usuario;
import com.ecommerce.gerenciamento_pedidos.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // EDIT 22/05 - 11:50 | BCrypt adicionado via PasswordEncoder
    @Autowired
    private PasswordEncoder passwordEncoder;
    // Criar um usuário
    public Usuario criarUsuario(Usuario usuario){
        // EDIT 22/05 - Lógica alterada. Retirei a verificação do usuario para criar

        if(usuarioRepository.findByEmail(usuario.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email já cadastrado: " + usuario.getEmail());
        }
        if(usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null){
            throw new IllegalArgumentException("Os campos (Nomel, email e senha) devem ser preenchidos");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario usuarioCriado = usuarioRepository.save(usuario);
        // Criando um usuário
        return usuarioCriado;
    }

    // Verificar o login (senha e email)
    // EDIT 22/05 - 11:38 | Alterei o retorno de Optional<Usuario> para boolean
    public boolean verificarLogin(String email, String senha){
        if(email == null || !email.contains("@") || senha == null || senha.length() < 6){
            throw new IllegalArgumentException("Email ou senha inválidos");
        }
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        if(usuarioExistente.isPresent() && passwordEncoder.matches(senha, usuarioExistente.get().getSenha()) ){
            return true;
        }
            return false;
        
    }

    // EDIT 25/05 00:08
    public Usuario buscarUsuarioPorId(Long id) {
        Usuario usuarioExistente = usuarioRepository.findById(id).
                orElseThrow(() ->  new IllegalArgumentException("Usuario com ID: " + id + " não existe."));
        return usuarioExistente;
    }
    // Buscar o usuário pelo nome
    public Optional<Usuario> buscarUsuarioPorNome(String nome){
        Optional<Usuario> usuarioExistente = usuarioRepository.findByNome(nome);
        if(usuarioExistente.isEmpty()){
            throw new IllegalArgumentException("Usuário com nome: " +nome+ " não existe.");
        }
        return usuarioExistente;
    }

    // EDIT 24/05 23:51 Buscar todos usuários
    public List<Usuario> buscarTodosUsuarios(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios;
    }

    // Buscar o usuário pelo email com optional
    // EDIT 25/05 00:24 Alterei o retorno de Optional<Usuario> para Usuario e mudei de Optional para orElseThrow
    public Usuario buscarUsuarioPeloEmail(String email){
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com email: " + email + " não encontrado."));
    }

    // Atualizar um usuário
    // EDIT 23/05 - 15:50 | Método atualizado para retornar public Usuario em vez de Optional<Usuario>
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado){
        Usuario usuario = usuarioRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Usuário não existe."));
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        if(usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()){
            usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }
        // Persistir (salvar) as alterações no banco de dados. Antes não tinha isso
        return usuarioRepository.save(usuario);
    }

    // Deletar o usuário pelo o id
    public void deletarUsuario(Long id){
        if(!usuarioRepository.existsById(id)){
            throw new RuntimeException("Não é possível encontrar, e, portanto deletar o usuário com ID " + id);
        }
        usuarioRepository.deleteById(id);
    }


}
