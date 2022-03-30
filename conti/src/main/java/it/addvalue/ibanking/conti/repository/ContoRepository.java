package it.addvalue.ibanking.conti.repository;

import it.addvalue.ibanking.conti.domain.Conto;

import java.util.List;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Conto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContoRepository extends JpaRepository<Conto, Long> {
}
