package it.addvalue.ibanking.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import it.addvalue.ibanking.gateway.IntegrationTest;
import it.addvalue.ibanking.gateway.domain.Conto;
import it.addvalue.ibanking.gateway.repository.ContoRepository;
import it.addvalue.ibanking.gateway.repository.EntityManager;
import java.time.Duration;
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
 * Integration tests for the {@link ContoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Conto.class).block();
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
        conto = createEntity(em);
    }

    @Test
    void createConto() throws Exception {
        int databaseSizeBeforeCreate = contoRepository.findAll().collectList().block().size();
        // Create the Conto
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeCreate + 1);
        Conto testConto = contoList.get(contoList.size() - 1);
        assertThat(testConto.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testConto.getIban()).isEqualTo(DEFAULT_IBAN);
    }

    @Test
    void createContoWithExistingId() throws Exception {
        // Create the Conto with an existing ID
        conto.setId(1L);

        int databaseSizeBeforeCreate = contoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = contoRepository.findAll().collectList().block().size();
        // set the field null
        conto.setNome(null);

        // Create the Conto, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllContosAsStream() {
        // Initialize the database
        contoRepository.save(conto).block();

        List<Conto> contoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Conto.class)
            .getResponseBody()
            .filter(conto::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(contoList).isNotNull();
        assertThat(contoList).hasSize(1);
        Conto testConto = contoList.get(0);
        assertThat(testConto.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testConto.getIban()).isEqualTo(DEFAULT_IBAN);
    }

    @Test
    void getAllContos() {
        // Initialize the database
        contoRepository.save(conto).block();

        // Get all the contoList
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
            .value(hasItem(conto.getId().intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME))
            .jsonPath("$.[*].iban")
            .value(hasItem(DEFAULT_IBAN));
    }

    @Test
    void getConto() {
        // Initialize the database
        contoRepository.save(conto).block();

        // Get the conto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, conto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(conto.getId().intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME))
            .jsonPath("$.iban")
            .value(is(DEFAULT_IBAN));
    }

    @Test
    void getNonExistingConto() {
        // Get the conto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewConto() throws Exception {
        // Initialize the database
        contoRepository.save(conto).block();

        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();

        // Update the conto
        Conto updatedConto = contoRepository.findById(conto.getId()).block();
        updatedConto.nome(UPDATED_NOME).iban(UPDATED_IBAN);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedConto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedConto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
        Conto testConto = contoList.get(contoList.size() - 1);
        assertThat(testConto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testConto.getIban()).isEqualTo(UPDATED_IBAN);
    }

    @Test
    void putNonExistingConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();
        conto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, conto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();
        conto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();
        conto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateContoWithPatch() throws Exception {
        // Initialize the database
        contoRepository.save(conto).block();

        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();

        // Update the conto using partial update
        Conto partialUpdatedConto = new Conto();
        partialUpdatedConto.setId(conto.getId());

        partialUpdatedConto.nome(UPDATED_NOME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedConto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedConto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
        Conto testConto = contoList.get(contoList.size() - 1);
        assertThat(testConto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testConto.getIban()).isEqualTo(DEFAULT_IBAN);
    }

    @Test
    void fullUpdateContoWithPatch() throws Exception {
        // Initialize the database
        contoRepository.save(conto).block();

        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();

        // Update the conto using partial update
        Conto partialUpdatedConto = new Conto();
        partialUpdatedConto.setId(conto.getId());

        partialUpdatedConto.nome(UPDATED_NOME).iban(UPDATED_IBAN);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedConto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedConto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
        Conto testConto = contoList.get(contoList.size() - 1);
        assertThat(testConto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testConto.getIban()).isEqualTo(UPDATED_IBAN);
    }

    @Test
    void patchNonExistingConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();
        conto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, conto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();
        conto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamConto() throws Exception {
        int databaseSizeBeforeUpdate = contoRepository.findAll().collectList().block().size();
        conto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Conto in the database
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteConto() {
        // Initialize the database
        contoRepository.save(conto).block();

        int databaseSizeBeforeDelete = contoRepository.findAll().collectList().block().size();

        // Delete the conto
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, conto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Conto> contoList = contoRepository.findAll().collectList().block();
        assertThat(contoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
