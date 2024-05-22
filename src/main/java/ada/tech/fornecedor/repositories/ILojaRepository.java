package ada.tech.fornecedor.repositories;

import ada.tech.fornecedor.domain.entities.Loja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILojaRepository extends JpaRepository<Loja, Integer> {
    Loja findByCnpj(long cnpj);
}
