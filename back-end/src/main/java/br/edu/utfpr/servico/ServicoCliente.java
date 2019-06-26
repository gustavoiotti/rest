package br.edu.utfpr.servico;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.edu.utfpr.dto.ClienteDTO;
import br.edu.utfpr.dto.PaisDTO;
import io.micrometer.core.ipc.http.HttpSender.Response;

@RestController
public class ServicoCliente {

    private List<ClienteDTO> clientes;
    private List<PaisDTO> paises;

    public ServicoCliente() {

        paises = Stream
                .of(PaisDTO.builder().id(1).nome("Iceland").sigla("IC").codigoTelefone(33).build(),
                        PaisDTO.builder().id(2).nome("Italia").sigla("IT").codigoTelefone(22).build(),
                        PaisDTO.builder().id(3).nome("Brasil").sigla("BR").codigoTelefone(55).build()
                )
                .collect(Collectors.toList());
        
        clientes = Stream
                .of(ClienteDTO.builder().id(1).nome("João").idade(19).telefone("99777151").limiteCredito(1500.00).pais(paises.get(3)).build(),
                        ClienteDTO.builder().id(2).nome("Aldo").idade(17).telefone("84373777").limiteCredito(800.00).pais(paises.get(2)).build(),
                        ClienteDTO.builder().id(3).nome("Pedro").idade(20).telefone("99805600").limiteCredito(2000.00).pais(paises.get(1)).build())
                .collect(Collectors.toList());
        
    }

    @GetMapping("/servico/cliente")
    public ResponseEntity<List<ClienteDTO>> listar() {
        // public List<ClienteDTO> listar() {
        // return clientes;
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/servico/cliente/{id}")
    public ResponseEntity<ClienteDTO> listarPorId(@PathVariable int id) {
        Optional<ClienteDTO> clienteEncontrado = clientes.stream().filter(p -> p.getId() == id).findAny();

        return ResponseEntity.of(clienteEncontrado);
    }

    @PostMapping("/servico/cliente")
    public ResponseEntity<ClienteDTO> criar(@RequestBody ClienteDTO cliente) {

        cliente.setId(clientes.size() + 1);
        clientes.add(cliente);

        return ResponseEntity.status(201).body(cliente);
    }

    @DeleteMapping("/servico/cliente/{id}")
    public ResponseEntity excluir(@PathVariable int id) {

        if (clientes.removeIf(cliente -> cliente.getId() == id))
            return ResponseEntity.noContent().build();

        else
            return ResponseEntity.notFound().build();
    }

    @PutMapping("/servico/cliente/{id}")
    public ResponseEntity<ClienteDTO> alterar(@PathVariable int id, @RequestBody ClienteDTO cliente) {
        Optional<ClienteDTO> clienteExistente = clientes.stream().filter(p -> p.getId() == id).findAny();

        clienteExistente.ifPresent(c -> {
            try {
                c.setNome(cliente.getNome());
            } catch (NomeClienteMenor5CaracteresException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            c.setIdade(cliente.getIdade());
            c.setTelefone(cliente.getTelefone());
            c.setLimiteCredito(cliente.getLimiteCredito());
            c.setPais(paises.stream().filter(p -> p.getId() == cliente.getPais().getId()).findAny().get());
        });

        return ResponseEntity.of(clienteExistente);
    }
}