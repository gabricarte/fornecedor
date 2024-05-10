package ada.tech.fornecedor.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tb_fabricante")

public class Fabricante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany
    @JoinTable(
            name = "fabricante_produto",
            joinColumns = @JoinColumn(name = "id_fabricante"),
            inverseJoinColumns = @JoinColumn(name = "id_produto")
    )
    private List<Produto> produtos;

    @Column(length = 14, unique = true)
    private long cnpj;

    @Column(length = 50)
    private String nome;
}