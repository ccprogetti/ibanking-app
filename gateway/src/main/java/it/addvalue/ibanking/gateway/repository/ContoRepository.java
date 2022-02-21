package it.addvalue.ibanking.gateway.repository;

import it.addvalue.ibanking.gateway.domain.Conto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Conto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContoRepository extends ReactiveCrudRepository<Conto, Long>, ContoRepositoryInternal {
    @Override
    <S extends Conto> Mono<S> save(S entity);

    @Override
    Flux<Conto> findAll();

    @Override
    Mono<Conto> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ContoRepositoryInternal {
    <S extends Conto> Mono<S> save(S entity);

    Flux<Conto> findAllBy(Pageable pageable);

    Flux<Conto> findAll();

    Mono<Conto> findById(Long id);

    Flux<Conto> findAllBy(Pageable pageable, Criteria criteria);
}
