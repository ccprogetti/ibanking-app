package it.addvalue.ibanking.gateway.web.rest;

import it.addvalue.ibanking.gateway.domain.Conto;
import it.addvalue.ibanking.gateway.repository.ContoRepository;
import it.addvalue.ibanking.gateway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link it.addvalue.ibanking.gateway.domain.Conto}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ContoResource {

    private final Logger log = LoggerFactory.getLogger(ContoResource.class);

    private static final String ENTITY_NAME = "conto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContoRepository contoRepository;

    public ContoResource(ContoRepository contoRepository) {
        this.contoRepository = contoRepository;
    }

    /**
     * {@code POST  /contos} : Create a new conto.
     *
     * @param conto the conto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conto, or with status {@code 400 (Bad Request)} if the conto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/contos")
    public Mono<ResponseEntity<Conto>> createConto(@Valid @RequestBody Conto conto) throws URISyntaxException {
        log.debug("REST request to save Conto : {}", conto);
        if (conto.getId() != null) {
            throw new BadRequestAlertException("A new conto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return contoRepository
            .save(conto)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/contos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /contos/:id} : Updates an existing conto.
     *
     * @param id the id of the conto to save.
     * @param conto the conto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conto,
     * or with status {@code 400 (Bad Request)} if the conto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/contos/{id}")
    public Mono<ResponseEntity<Conto>> updateConto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Conto conto
    ) throws URISyntaxException {
        log.debug("REST request to update Conto : {}, {}", id, conto);
        if (conto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return contoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return contoRepository
                    .save(conto)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /contos/:id} : Partial updates given fields of an existing conto, field will ignore if it is null
     *
     * @param id the id of the conto to save.
     * @param conto the conto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conto,
     * or with status {@code 400 (Bad Request)} if the conto is not valid,
     * or with status {@code 404 (Not Found)} if the conto is not found,
     * or with status {@code 500 (Internal Server Error)} if the conto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/contos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Conto>> partialUpdateConto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Conto conto
    ) throws URISyntaxException {
        log.debug("REST request to partial update Conto partially : {}, {}", id, conto);
        if (conto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return contoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Conto> result = contoRepository
                    .findById(conto.getId())
                    .map(existingConto -> {
                        if (conto.getNome() != null) {
                            existingConto.setNome(conto.getNome());
                        }
                        if (conto.getIban() != null) {
                            existingConto.setIban(conto.getIban());
                        }

                        return existingConto;
                    })
                    .flatMap(contoRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /contos} : get all the contos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contos in body.
     */
    @GetMapping("/contos")
    public Mono<List<Conto>> getAllContos() {
        log.debug("REST request to get all Contos");
        return contoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /contos} : get all the contos as a stream.
     * @return the {@link Flux} of contos.
     */
    @GetMapping(value = "/contos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Conto> getAllContosAsStream() {
        log.debug("REST request to get all Contos as a stream");
        return contoRepository.findAll();
    }

    /**
     * {@code GET  /contos/:id} : get the "id" conto.
     *
     * @param id the id of the conto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/contos/{id}")
    public Mono<ResponseEntity<Conto>> getConto(@PathVariable Long id) {
        log.debug("REST request to get Conto : {}", id);
        Mono<Conto> conto = contoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(conto);
    }

    /**
     * {@code DELETE  /contos/:id} : delete the "id" conto.
     *
     * @param id the id of the conto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/contos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteConto(@PathVariable Long id) {
        log.debug("REST request to delete Conto : {}", id);
        return contoRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
