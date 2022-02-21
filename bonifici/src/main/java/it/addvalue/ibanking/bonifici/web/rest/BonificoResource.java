package it.addvalue.ibanking.bonifici.web.rest;

import it.addvalue.ibanking.bonifici.domain.Bonifico;
import it.addvalue.ibanking.bonifici.repository.BonificoRepository;
import it.addvalue.ibanking.bonifici.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.addvalue.ibanking.bonifici.domain.Bonifico}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BonificoResource {

    private final Logger log = LoggerFactory.getLogger(BonificoResource.class);

    private static final String ENTITY_NAME = "bonificiBonifico";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BonificoRepository bonificoRepository;

    public BonificoResource(BonificoRepository bonificoRepository) {
        this.bonificoRepository = bonificoRepository;
    }

    /**
     * {@code POST  /bonificos} : Create a new bonifico.
     *
     * @param bonifico the bonifico to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bonifico, or with status {@code 400 (Bad Request)} if the bonifico has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bonificos")
    public ResponseEntity<Bonifico> createBonifico(@Valid @RequestBody Bonifico bonifico) throws URISyntaxException {
        log.debug("REST request to save Bonifico : {}", bonifico);
        if (bonifico.getId() != null) {
            throw new BadRequestAlertException("A new bonifico cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Bonifico result = bonificoRepository.save(bonifico);
        return ResponseEntity
            .created(new URI("/api/bonificos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bonificos/:id} : Updates an existing bonifico.
     *
     * @param id the id of the bonifico to save.
     * @param bonifico the bonifico to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bonifico,
     * or with status {@code 400 (Bad Request)} if the bonifico is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bonifico couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bonificos/{id}")
    public ResponseEntity<Bonifico> updateBonifico(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Bonifico bonifico
    ) throws URISyntaxException {
        log.debug("REST request to update Bonifico : {}, {}", id, bonifico);
        if (bonifico.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bonifico.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bonificoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Bonifico result = bonificoRepository.save(bonifico);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bonifico.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bonificos/:id} : Partial updates given fields of an existing bonifico, field will ignore if it is null
     *
     * @param id the id of the bonifico to save.
     * @param bonifico the bonifico to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bonifico,
     * or with status {@code 400 (Bad Request)} if the bonifico is not valid,
     * or with status {@code 404 (Not Found)} if the bonifico is not found,
     * or with status {@code 500 (Internal Server Error)} if the bonifico couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bonificos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Bonifico> partialUpdateBonifico(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Bonifico bonifico
    ) throws URISyntaxException {
        log.debug("REST request to partial update Bonifico partially : {}, {}", id, bonifico);
        if (bonifico.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bonifico.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bonificoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Bonifico> result = bonificoRepository
            .findById(bonifico.getId())
            .map(existingBonifico -> {
                if (bonifico.getCausale() != null) {
                    existingBonifico.setCausale(bonifico.getCausale());
                }
                if (bonifico.getDestinatario() != null) {
                    existingBonifico.setDestinatario(bonifico.getDestinatario());
                }
                if (bonifico.getImporto() != null) {
                    existingBonifico.setImporto(bonifico.getImporto());
                }
                if (bonifico.getDataEsecuzione() != null) {
                    existingBonifico.setDataEsecuzione(bonifico.getDataEsecuzione());
                }
                if (bonifico.getIbanDestinatario() != null) {
                    existingBonifico.setIbanDestinatario(bonifico.getIbanDestinatario());
                }

                return existingBonifico;
            })
            .map(bonificoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bonifico.getId().toString())
        );
    }

    /**
     * {@code GET  /bonificos} : get all the bonificos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bonificos in body.
     */
    @GetMapping("/bonificos")
    public List<Bonifico> getAllBonificos() {
        log.debug("REST request to get all Bonificos");
        return bonificoRepository.findAll();
    }

    /**
     * {@code GET  /bonificos/:id} : get the "id" bonifico.
     *
     * @param id the id of the bonifico to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bonifico, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bonificos/{id}")
    public ResponseEntity<Bonifico> getBonifico(@PathVariable Long id) {
        log.debug("REST request to get Bonifico : {}", id);
        Optional<Bonifico> bonifico = bonificoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(bonifico);
    }

    /**
     * {@code DELETE  /bonificos/:id} : delete the "id" bonifico.
     *
     * @param id the id of the bonifico to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bonificos/{id}")
    public ResponseEntity<Void> deleteBonifico(@PathVariable Long id) {
        log.debug("REST request to delete Bonifico : {}", id);
        bonificoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
