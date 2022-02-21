package it.addvalue.ibanking.bonifici.repository;

import it.addvalue.ibanking.bonifici.domain.Bonifico;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Bonifico entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BonificoRepository extends JpaRepository<Bonifico, Long> {}
