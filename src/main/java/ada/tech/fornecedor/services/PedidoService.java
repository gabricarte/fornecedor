package ada.tech.fornecedor.services;
import ada.tech.fornecedor.domain.dto.PedidoDto;
import ada.tech.fornecedor.domain.dto.PedidoProdutoDto;
import ada.tech.fornecedor.domain.dto.exceptions.NotFoundException;
import ada.tech.fornecedor.domain.entities.*;
import ada.tech.fornecedor.domain.mappers.PedidoMapper;
import ada.tech.fornecedor.repositories.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.json.JSONObject;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class PedidoService implements IPedidoService {

    private final IPedidoRepository repository;
    private final IProdutoRepository produtoRepository;
    private final IFornecedorRepository fornecedorRepository;
    private final IEnderecoRepository enderecoRepository;
    private final LogisticaService logisticaService;

    private final IEstoqueRepository estoqueRepository;

    @Override
    public PedidoDto criarPedido(PedidoDto pedidoDto) {
        Pedido pedido = new Pedido();
        List<PedidoProduto> pedidoProdutos = new ArrayList<>();
        pedido.setData(LocalDate.now());
        pedido.setHorario(LocalTime.now());
        double totalPedido = 0.0;
        double volumeTotalProduto = 0.0;



        Fornecedor fornecedor = fornecedorRepository.findById(pedidoDto.getFornecedor())
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        pedido.setFornecedor(fornecedor);


        Endereco endereco = enderecoRepository.findById(pedidoDto.getEndereco())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        pedido.setEndereco(endereco);


        for (PedidoProdutoDto pedidoProdutoDto : pedidoDto.getProdutos()) {
            Produto produto = produtoRepository.findById(pedidoProdutoDto.getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));


            if (pedidoProdutoDto.getQuantidade() <= 0) {
                throw new IllegalArgumentException("A quantidade do produto deve ser maior que zero");
            }


            volumeTotalProduto = produto.getVolume() * pedidoProdutoDto.getQuantidade();


            double precoTotalProduto = produto.getPreco() * pedidoProdutoDto.getQuantidade();


            totalPedido += precoTotalProduto;

            PedidoProduto pedidoProduto = new PedidoProduto();
            pedidoProduto.setPedidos(pedido);
            pedidoProduto.setProdutos(produto);
            pedidoProduto.setQuantidade(pedidoProdutoDto.getQuantidade());
            pedidoProduto.setVolumeTotal(volumeTotalProduto);


            pedidoProdutos.add(pedidoProduto);
        }

        pedido.setTotal(totalPedido);
        pedido.setVolumeTotal(volumeTotalProduto);

        Pedido pedidoSalvo = repository.save(pedido);

        for (PedidoProduto pedidoProduto : pedidoProdutos) {
            pedidoProduto.setPedidos(pedidoSalvo);
        }

        pedidoSalvo.getPedidoProduto().addAll(pedidoProdutos);
        pedidoSalvo = repository.save(pedidoSalvo);

        logisticaService.enviarDadosLogistica(pedidoSalvo);

        return PedidoMapper.toDto(pedidoSalvo);
    }

    @Override
    public List<PedidoDto> listarPedidos() {
        return repository.findAll().stream().map(PedidoMapper::toDto).toList();
    }

    @Override
    public PedidoDto listarPedido(int id) throws NotFoundException {
        return PedidoMapper.toDto(searchPedidoById(id));
    }

    @Override
    public PedidoDto atualizarPedido(int id, PedidoDto pedidoDto) throws NotFoundException {
        final Pedido pedido = repository.findById(id).orElseThrow(() -> new NotFoundException(Pedido.class, String.valueOf(id)));
        pedido.setData(pedidoDto.getData());
        pedido.setHorario(pedidoDto.getHorario());
        pedido.setTotal(pedidoDto.getTotal());
        return PedidoMapper.toDto(repository.save(pedido));
    }

    @Override
    public void deletarPedido(int id) throws NotFoundException {
        repository.deleteById(id);
    }

    private Pedido searchPedidoById(int id) throws NotFoundException {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Pedido.class, String.valueOf(id)));
    }
}
