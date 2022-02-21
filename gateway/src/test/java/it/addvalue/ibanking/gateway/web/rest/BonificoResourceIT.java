package it.addvalue.ibanking.gateway.web.rest;

import static it.addvalue.ibanking.gateway.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import it.addvalue.ibanking.gateway.IntegrationTest;
import it.addvalue.ibanking.gateway.domain.Bonifico;
import it.addvalue.ibanking.gateway.repository.BonificoRepository;
import it.addvalue.ibanking.gateway.repository.EntityManager;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link BonificoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Bonifico.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        bonifico = createEntity(em);
    }

    @Test
    void createBonifico() throws Exception {
        int databaseSizeBeforeCreate = bonificoRepository.findAll().collectList().block().size();
        // Create the Bonifico
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeCreate + 1);
        Bonifico testBonifico = bonificoList.get(bonificoList.size() - 1);
        assertThat(testBonifico.getCausale()).isEqualTo(DEFAULT_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(DEFAULT_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(DEFAULT_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(DEFAULT_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(DEFAULT_IBAN_DESTINATARIO);
    }

    @Test
    void createBonificoWithExistingId() throws Exception {
        // Create the Bonifico with an existing ID
        bonifico.setId(1L);

        int databaseSizeBeforeCreate = bonificoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCausaleIsRequired() throws Exception {
        int databaseSizeBeforeTest = bonificoRepository.findAll().collectList().block().size();
        // set the field null
        bonifico.setCausale(null);

        // Create the Bonifico, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDestinatarioIsRequired() throws Exception {
        int databaseSizeBeforeTest = bonificoRepository.findAll().collectList().block().size();
        // set the field null
        bonifico.setDestinatario(null);

        // Create the Bonifico, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllBonificosAsStream() {
        // Initialize the database
        bonificoRepository.save(bonifico).block();

        List<Bonifico> bonificoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Bonifico.class)
            .getResponseBody()
            .filter(bonifico::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(bonificoList).isNotNull();
        assertThat(bonificoList).hasSize(1);
        Bonifico testBonifico = bonificoList.get(0);
        assertThat(testBonifico.getCausale()).isEqualTo(DEFAULT_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(DEFAULT_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(DEFAULT_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(DEFAULT_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(DEFAULT_IBAN_DESTINATARIO);
    }

    @Test
    void getAllBonificos() {
        // Initialize the database
        bonificoRepository.save(bonifico).block();

        // Get all the bonificoList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(bonifico.getId().intValue()))
            .jsonPath("$.[*].causale")
            .value(hasItem(DEFAULT_CAUSALE))
            .jsonPath("$.[*].destinatario")
            .value(hasItem(DEFAULT_DESTINATARIO))
            .jsonPath("$.[*].importo")
            .value(hasItem(sameNumber(DEFAULT_IMPORTO)))
            .jsonPath("$.[*].dataEsecuzione")
            .value(hasItem(DEFAULT_DATA_ESECUZIONE.toString()))
            .jsonPath("$.[*].ibanDestinatario")
            .value(hasItem(DEFAULT_IBAN_DESTINATARIO));
    }

    @Test
    void getBonifico() {
        // Initialize the database
        bonificoRepository.save(bonifico).block();

        // Get the bonifico
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, bonifico.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(bonifico.getId().intValue()))
            .jsonPath("$.causale")
            .value(is(DEFAULT_CAUSALE))
            .jsonPath("$.destinatario")
            .value(is(DEFAULT_DESTINATARIO))
            .jsonPath("$.importo")
            .value(is(sameNumber(DEFAULT_IMPORTO)))
            .jsonPath("$.dataEsecuzione")
            .value(is(DEFAULT_DATA_ESECUZIONE.toString()))
            .jsonPath("$.ibanDestinatario")
            .value(is(DEFAULT_IBAN_DESTINATARIO));
    }

    @Test
    void getNonExistingBonifico() {
        // Get the bonifico
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewBonifico() throws Exception {
        // Initialize the database
        bonificoRepository.save(bonifico).block();

        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();

        // Update the bonifico
        Bonifico updatedBonifico = bonificoRepository.findById(bonifico.getId()).block();
        updatedBonifico
            .causale(UPDATED_CAUSALE)
            .destinatario(UPDATED_DESTINATARIO)
            .importo(UPDATED_IMPORTO)
            .dataEsecuzione(UPDATED_DATA_ESECUZIONE)
            .ibanDestinatario(UPDATED_IBAN_DESTINATARIO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBonifico.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedBonifico))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
        Bonifico testBonifico = bonificoList.get(bonificoList.size() - 1);
        assertThat(testBonifico.getCausale()).isEqualTo(UPDATED_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(UPDATED_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(UPDATED_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(UPDATED_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(UPDATED_IBAN_DESTINATARIO);
    }

    @Test
    void putNonExistingBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();
        bonifico.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bonifico.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();
        bonifico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();
        bonifico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBonificoWithPatch() throws Exception {
        // Initialize the database
        bonificoRepository.save(bonifico).block();

        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();

        // Update the bonifico using partial update
        Bonifico partialUpdatedBonifico = new Bonifico();
        partialUpdatedBonifico.setId(bonifico.getId());

        partialUpdatedBonifico.causale(UPDATED_CAUSALE).ibanDestinatario(UPDATED_IBAN_DESTINATARIO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBonifico.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBonifico))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
        Bonifico testBonifico = bonificoList.get(bonificoList.size() - 1);
        assertThat(testBonifico.getCausale()).isEqualTo(UPDATED_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(DEFAULT_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(DEFAULT_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(DEFAULT_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(UPDATED_IBAN_DESTINATARIO);
    }

    @Test
    void fullUpdateBonificoWithPatch() throws Exception {
        // Initialize the database
        bonificoRepository.save(bonifico).block();

        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();

        // Update the bonifico using partial update
        Bonifico partialUpdatedBonifico = new Bonifico();
        partialUpdatedBonifico.setId(bonifico.getId());

        partialUpdatedBonifico
            .causale(UPDATED_CAUSALE)
            .destinatario(UPDATED_DESTINATARIO)
            .importo(UPDATED_IMPORTO)
            .dataEsecuzione(UPDATED_DATA_ESECUZIONE)
            .ibanDestinatario(UPDATED_IBAN_DESTINATARIO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBonifico.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBonifico))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
        Bonifico testBonifico = bonificoList.get(bonificoList.size() - 1);
        assertThat(testBonifico.getCausale()).isEqualTo(UPDATED_CAUSALE);
        assertThat(testBonifico.getDestinatario()).isEqualTo(UPDATED_DESTINATARIO);
        assertThat(testBonifico.getImporto()).isEqualByComparingTo(UPDATED_IMPORTO);
        assertThat(testBonifico.getDataEsecuzione()).isEqualTo(UPDATED_DATA_ESECUZIONE);
        assertThat(testBonifico.getIbanDestinatario()).isEqualTo(UPDATED_IBAN_DESTINATARIO);
    }

    @Test
    void patchNonExistingBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();
        bonifico.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, bonifico.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();
        bonifico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBonifico() throws Exception {
        int databaseSizeBeforeUpdate = bonificoRepository.findAll().collectList().block().size();
        bonifico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bonifico))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bonifico in the database
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBonifico() {
        // Initialize the database
        bonificoRepository.save(bonifico).block();

        int databaseSizeBeforeDelete = bonificoRepository.findAll().collectList().block().size();

        // Delete the bonifico
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, bonifico.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Bonifico> bonificoList = bonificoRepository.findAll().collectList().block();
        assertThat(bonificoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
