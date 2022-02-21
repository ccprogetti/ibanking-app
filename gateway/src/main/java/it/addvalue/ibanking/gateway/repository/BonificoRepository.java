package it.addvalue.ibanking.gateway.repository;

import it.addvalue.ibanking.gateway.domain.Bonifico;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Bonifico entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BonificoRepository extends ReactiveCrudRepository<Bonifico, Long>, BonificoRepositoryInternal {
    @Override
    <S extends Bonifico> Mono<S> save(S entity);

    @Override
    Flux<Bonifico> findAll();

    @Override
    Mono<Bonifico> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BonificoRepositoryInternal {
    <S extends Bonifico> Mono<S> save(S entity);

    Flux<Bonifico> findAllBy(Pageable pageable);

    Flux<Bonifico> findAll();

    Mono<Bonifico> findById(Long id);

    Flux<Bonifico> findAllBy(Pageable pageable, Criteria criteria);
}
