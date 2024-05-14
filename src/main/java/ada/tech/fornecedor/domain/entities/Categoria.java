package ada.tech.fornecedor.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tb_pedido")

public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "categoria")
    private List<Produto> produtos = new ArrayList<>();

    @Size(max = 50, message = "O nome da categoria do produto não pode exceder 50 caracteres")
    @Column(length = 50)
    private String categoria;
}
