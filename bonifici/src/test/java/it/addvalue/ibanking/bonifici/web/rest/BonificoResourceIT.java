package it.addvalue.ibanking.bonifici.web.rest;

import static it.addvalue.ibanking.bonifici.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.addvalue.ibanking.bonifici.IntegrationTest;
import it.addvalue.ibanking.bonifici.domain.Bonifico;
import it.addvalue.ibanking.bonifici.repository.BonificoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link BonificoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BonificoResourceIT {

    private static final String DEFAULT_CAUSALE = "AAAAAAAAAA";
    private static final String UPDATED_CAUSALE = "BBBBBBBBBB";

    private static final String DEFAULT_DESTINATARIO = "AAAAAAAAAA";
    private static final String UPDATED_DESTINATARIO = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_IMPORTO = new BigDecimal(1);
    private static final BigDecimal UPDATED_IMPORTO = new BigDecimal(2);

    private static final LocalDate DEFAULT_DATA_ESECUZIONE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_ESECUZIONE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_IBAN_DESTINATARIO = "AAAAAAAAAA";
    private static final String UPDATED_IBAN_DESTINATARIO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bonificos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BonificoRepository bonificoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBonificoMockMvc;

    private Bonifico bonifico;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bonifico createEntity(EntityManager em) {
        Bonifico bonifico = new Bonifico()
            .causale(DEFAULT_CAUSALE)
            .destinatario(DEFAULT_DESTINATARIO)
            .importo(DEFAULT_IMPORTO)
            .dataEsecuzione(DEFAULT_DATA_ESECUZIONE)
            .ibanDestinatario(DEFAULT_IBAN_DESTINATARIO);
        return bonifico;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bonifico createUpdatedEntity(EntityManager em) {
        Bonifico bonifico = new Bonifico()
            .causale(UPDATED_CAUSALE)
            .destinatario(UPDATED_DESTINATARIO)
            .importo(UPDATED_IMPORTO)
            .dataEsecuzione(UPDATED_DATA_ESECUZIONE)
            .ibanDestinatario(UPDATED_IBAN_DESTINATARIO);
        return bonifico;
    }

    @BeforeEach
    public void initTest() {
        bonifico = createEntity(em);
    }

    @Test
    @Transactional
    void createBonifico() throws Exception {
        int databaseSizeBeforeCreate = bonificoRepository.findAll().size();
        // Create the Bonifico
        restBonificoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isCreated());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeCreate + 1);
        Bonifico testBonifico = bonificoList.get(bonificoList.size() - 1);
        assertThat(testBonifico.getCausale()).isEqualTo(DEFAULT_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(DEFAULT_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(DEFAULT_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(DEFAULT_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(DEFAULT_IBAN_DESTINATARIO);
    }

    @Test
    @Transactional
    void createBonificoWithExistingId() throws Exception {
        // Create the Bonifico with an existing ID
        bonifico.setId(1L);

        int databaseSizeBeforeCreate = bonificoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBonificoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCausaleIsRequired() throws Exception {
        int databaseSizeBeforeTest = bonificoRepository.findAll().size();
        // set the field null
        bonifico.setCausale(null);

        // Create the Bonifico, which fails.

        restBonificoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isBadRequest());

        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDestinatarioIsRequired() throws Exception {
        int databaseSizeBeforeTest = bonificoRepository.findAll().size();
        // set the field null
        bonifico.setDestinatario(null);

        // Create the Bonifico, which fails.

        restBonificoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isBadRequest());

        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBonificos() throws Exception {
        // Initialize the database
        bonificoRepository.saveAndFlush(bonifico);

        // Get all the bonificoList
        restBonificoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bonifico.getId().intValue())))
            .andExpect(jsonPath("$.[*].causale").value(hasItem(DEFAULT_CAUSALE)))
            .andExpect(jsonPath("$.[*].destinatario").value(hasItem(DEFAULT_DESTINATARIO)))
            .andExpect(jsonPath("$.[*].importo").value(hasItem(sameNumber(DEFAULT_IMPORTO))))
            .andExpect(jsonPath("$.[*].dataEsecuzione").value(hasItem(DEFAULT_DATA_ESECUZIONE.toString())))
            .andExpect(jsonPath("$.[*].ibanDestinatario").value(hasItem(DEFAULT_IBAN_DESTINATARIO)));
    }

    @Test
    @Transactional
    void getBonifico() throws Exception {
        // Initialize the database
        bonificoRepository.saveAndFlush(bonifico);

        // Get the bonifico
        restBonificoMockMvc
            .perform(get(ENTITY_API_URL_ID, bonifico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bonifico.getId().intValue()))
            .andExpect(jsonPath("$.causale").value(DEFAULT_CAUSALE))
            .andExpect(jsonPath("$.destinatario").value(DEFAULT_DESTINATARIO))
            .andExpect(jsonPath("$.importo").value(sameNumber(DEFAULT_IMPORTO)))
            .andExpect(jsonPath("$.dataEsecuzione").value(DEFAULT_DATA_ESECUZIONE.toString()))
            .andExpect(jsonPath("$.ibanDestinatario").value(DEFAULT_IBAN_DESTINATARIO));
    }

    @Test
    @Transactional
    void getNonExistingBonifico() throws Exception {
        // Get the bonifico
        restBonificoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBonifico() throws Exception {
        // Initialize the database
        bonificoRepository.saveAndFlush(bonifico);

        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();

        // Update the bonifico
        Bonifico updatedBonifico = bonificoRepository.findById(bonifico.getId()).get();
        // Disconnect from session so that the updates on updatedBonifico are not directly saved in db
        em.detach(updatedBonifico);
        updatedBonifico
            .causale(UPDATED_CAUSALE)
            .destinatario(UPDATED_DESTINATARIO)
            .importo(UPDATED_IMPORTO)
            .dataEsecuzione(UPDATED_DATA_ESECUZIONE)
            .ibanDestinatario(UPDATED_IBAN_DESTINATARIO);

        restBonificoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBonifico.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBonifico))
            )
            .andExpect(status().isOk());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
        Bonifico testBonifico = bonificoList.get(bonificoList.size() - 1);
        assertThat(testBonifico.getCausale()).isEqualTo(UPDATED_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(UPDATED_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(UPDATED_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(UPDATED_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(UPDATED_IBAN_DESTINATARIO);
    }

    @Test
    @Transactional
    void putNonExistingBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();
        bonifico.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBonificoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bonifico.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();
        bonifico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBonificoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();
        bonifico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBonificoMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBonificoWithPatch() throws Exception {
        // Initialize the database
        bonificoRepository.saveAndFlush(bonifico);

        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();

        // Update the bonifico using partial update
        Bonifico partialUpdatedBonifico = new Bonifico();
        partialUpdatedBonifico.setId(bonifico.getId());

        partialUpdatedBonifico.causale(UPDATED_CAUSALE).ibanDestinatario(UPDATED_IBAN_DESTINATARIO);

        restBonificoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBonifico.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBonifico))
            )
            .andExpect(status().isOk());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
        Bonifico testBonifico = bonificoList.get(bonificoList.size() - 1);
        assertThat(testBonifico.getCausale()).isEqualTo(UPDATED_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(DEFAULT_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(DEFAULT_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(DEFAULT_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(UPDATED_IBAN_DESTINATARIO);
    }

    @Test
    @Transactional
    void fullUpdateBonificoWithPatch() throws Exception {
        // Initialize the database
        bonificoRepository.saveAndFlush(bonifico);

        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();

        // Update the bonifico using partial update
        Bonifico partialUpdatedBonifico = new Bonifico();
        partialUpdatedBonifico.setId(bonifico.getId());

        partialUpdatedBonifico
            .causale(UPDATED_CAUSALE)
            .destinatario(UPDATED_DESTINATARIO)
            .importo(UPDATED_IMPORTO)
            .dataEsecuzione(UPDATED_DATA_ESECUZIONE)
            .ibanDestinatario(UPDATED_IBAN_DESTINATARIO);

        restBonificoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBonifico.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBonifico))
            )
            .andExpect(status().isOk());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
        Bonifico testBonifico = bonificoList.get(bonificoList.size() - 1);
        assertThat(testBonifico.getCausale()).isEqualTo(UPDATED_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(UPDATED_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(UPDATED_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(UPDATED_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(UPDATED_IBAN_DESTINATARIO);
    }

    @Test
    @Transactional
    void patchNonExistingBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();
        bonifico.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBonificoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bonifico.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();
        bonifico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBonificoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().size();
        bonifico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBonificoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bonifico))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBonifico() throws Exception {
        // Initialize the database
        bonificoRepository.saveAndFlush(bonifico);

        int databaseSizeBeforeDelete = bonificoRepository.findAll().size();

        // Delete the bonifico
        restBonificoMockMvc
            .perform(delete(ENTITY_API_URL_ID, bonifico.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bonifico> bonificoList = bonificoRepository.findAll();
        assertThat(bonificoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
