package it.addvalue.ibanking.conti.service;

import it.addvalue.ibanking.conti.domain.Conto;
import it.addvalue.ibanking.conti.repository.ContoRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Conto}.
 */
@Service
@Transactional
public class ContoService {

    private final Logger log = LoggerFactory.getLogger(ContoService.class);

    private final ContoRepository contoRepository;

    public ContoService(ContoRepository contoRepository) {
        this.contoRepository = contoRepository;
    }

    /**
     * Save a conto.
     *
     * @param conto the entity to save.
     * @return the persisted entity.
     */
    public Conto save(Conto conto) {
        log.debug("Request to save Conto : {}", conto);
        return contoRepository.save(conto);
    }

    /**
     * Update a conto.
     *
     * @param conto the entity to save.
     * @return the persisted entity.
     */
    public Conto update(Conto conto) {
        log.debug("Request to save Conto : {}", conto);
        return contoRepository.save(conto);
    }

    /**
     * Partially update a conto.
     *
     * @param conto the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Conto> partialUpdate(Conto conto) {
        log.debug("Request to partially update Conto : {}", conto);

        return contoRepository
            .findById(conto.getId())
            .map(existingConto -> {
                if (conto.getNome() != null) {
                    existingConto.setNome(conto.getNome());
                }
                if (conto.getIban() != null) {
                    existingConto.setIban(conto.getIban());
                }
                if (conto.getUserName() != null) {
                    existingConto.setUserName(conto.getUserName());
                }
                if (conto.getAbi() != null) {
                    existingConto.setAbi(conto.getAbi());
                }

                return existingConto;
            })
            .map(contoRepository::save);
    }

    /**
     * Get all the contos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Conto> findAll(Pageable pageable) {
        log.debug("Request to get all Contos");
        return contoRepository.findAll(pageable);
    }

    /**
     * Get one conto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Conto> findOne(Long id) {
        log.debug("Request to get Conto : {}", id);
        return contoRepository.findById(id);
    }

    /**
     * Delete the conto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Conto : {}", id);
        contoRepository.deleteById(id);
    }
}
