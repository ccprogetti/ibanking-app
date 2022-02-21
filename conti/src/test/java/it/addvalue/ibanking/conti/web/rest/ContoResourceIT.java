package it.addvalue.ibanking.conti.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.addvalue.ibanking.conti.IntegrationTest;
import it.addvalue.ibanking.conti.domain.Conto;
import it.addvalue.ibanking.conti.repository.ContoRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ContoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_IBAN = "AAAAAAAAAA";
    private static final String UPDATED_IBAN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/contos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ContoRepository contoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContoMockMvc;

    private Conto conto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conto createEntity(EntityManager em) {
        Conto conto = new Conto().nome(DEFAULT_NOME).iban(DEFAULT_IBAN);
        return conto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conto createUpdatedEntity(EntityManager em) {
        Conto conto = new Conto().nome(UPDATED_NOME).iban(UPDATED_IBAN);
        return conto;
    }

    @BeforeEach
    public void initTest() {
        conto = createEntity(em);
    }

    @Test
    @Transactional
    void createConto() throws Exception {
        int databaseSizeBeforeCreate = contoRepository.findAll().size();
        // Create the Conto
        restContoMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isCreated());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeCreate + 1);
        Conto testConto = contoList.get(contoList.size() - 1);
        assertThat(testConto.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testConto.getIban()).isEqualTo(DEFAULT_IBAN);
    }

    @Test
    @Transactional
    void createContoWithExistingId() throws Exception {
        // Create the Conto with an existing ID
        conto.setId(1L);

        int databaseSizeBeforeCreate = contoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContoMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = contoRepository.findAll().size();
        // set the field null
        conto.setNome(null);

        // Create the Conto, which fails.

        restContoMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isBadRequest());

        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContos() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList
        restContoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].iban").value(hasItem(DEFAULT_IBAN)));
    }

    @Test
    @Transactional
    void getConto() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get the conto
        restContoMockMvc
            .perform(get(ENTITY_API_URL_ID, conto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(conto.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.iban").value(DEFAULT_IBAN));
    }

    @Test
    @Transactional
    void getNonExistingConto() throws Exception {
        // Get the conto
        restContoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewConto() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        int databaseSizeBeforeUpdate = contoRepository.findAll().size();

        // Update the conto
        Conto updatedConto = contoRepository.findById(conto.getId()).get();
        // Disconnect from session so that the updates on updatedConto are not directly saved in db
        em.detach(updatedConto);
        updatedConto.nome(UPDATED_NOME).iban(UPDATED_IBAN);

        restContoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedConto.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedConto))
            )
            .andExpect(status().isOk());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
        Conto testConto = contoList.get(contoList.size() - 1);
        assertThat(testConto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testConto.getIban()).isEqualTo(UPDATED_IBAN);
    }

    @Test
    @Transactional
    void putNonExistingConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().size();
        conto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conto.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().size();
        conto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().size();
        conto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContoMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContoWithPatch() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        int databaseSizeBeforeUpdate = contoRepository.findAll().size();

        // Update the conto using partial update
        Conto partialUpdatedConto = new Conto();
        partialUpdatedConto.setId(conto.getId());

        partialUpdatedConto.nome(UPDATED_NOME);

        restContoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConto.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConto))
            )
            .andExpect(status().isOk());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
        Conto testConto = contoList.get(contoList.size() - 1);
        assertThat(testConto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testConto.getIban()).isEqualTo(DEFAULT_IBAN);
    }

    @Test
    @Transactional
    void fullUpdateContoWithPatch() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        int databaseSizeBeforeUpdate = contoRepository.findAll().size();

        // Update the conto using partial update
        Conto partialUpdatedConto = new Conto();
        partialUpdatedConto.setId(conto.getId());

        partialUpdatedConto.nome(UPDATED_NOME).iban(UPDATED_IBAN);

        restContoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConto.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConto))
            )
            .andExpect(status().isOk());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
        Conto testConto = contoList.get(contoList.size() - 1);
        assertThat(testConto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testConto.getIban()).isEqualTo(UPDATED_IBAN);
    }

    @Test
    @Transactional
    void patchNonExistingConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().size();
        conto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, conto.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().size();
        conto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().size();
        conto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(conto))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConto() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        int databaseSizeBeforeDelete = contoRepository.findAll().size();

        // Delete the conto
        restContoMockMvc
            .perform(delete(ENTITY_API_URL_ID, conto.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Conto> contoList = contoRepository.findAll();
        assertThat(contoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
