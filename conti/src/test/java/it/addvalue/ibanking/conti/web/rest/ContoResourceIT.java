package it.addvalue.ibanking.conti.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.addvalue.ibanking.conti.IntegrationTest;
import it.addvalue.ibanking.conti.domain.Conto;
import it.addvalue.ibanking.conti.repository.ContoRepository;
import it.addvalue.ibanking.conti.service.criteria.ContoCriteria;
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

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ABI = "AAAAAAAAAA";
    private static final String UPDATED_ABI = "BBBBBBBBBB";

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
        Conto conto = new Conto().nome(DEFAULT_NOME).iban(DEFAULT_IBAN).userName(DEFAULT_USER_NAME).abi(DEFAULT_ABI);
        return conto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conto createUpdatedEntity(EntityManager em) {
        Conto conto = new Conto().nome(UPDATED_NOME).iban(UPDATED_IBAN).userName(UPDATED_USER_NAME).abi(UPDATED_ABI);
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
        assertThat(testConto.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testConto.getAbi()).isEqualTo(DEFAULT_ABI);
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
    void checkUserNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = contoRepository.findAll().size();
        // set the field null
        conto.setUserName(null);

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
    void checkAbiIsRequired() throws Exception {
        int databaseSizeBeforeTest = contoRepository.findAll().size();
        // set the field null
        conto.setAbi(null);

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
            .andExpect(jsonPath("$.[*].iban").value(hasItem(DEFAULT_IBAN)))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME)))
            .andExpect(jsonPath("$.[*].abi").value(hasItem(DEFAULT_ABI)));
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
            .andExpect(jsonPath("$.iban").value(DEFAULT_IBAN))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME))
            .andExpect(jsonPath("$.abi").value(DEFAULT_ABI));
    }

    @Test
    @Transactional
    void getContosByIdFiltering() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        Long id = conto.getId();

        defaultContoShouldBeFound("id.equals=" + id);
        defaultContoShouldNotBeFound("id.notEquals=" + id);

        defaultContoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultContoShouldNotBeFound("id.greaterThan=" + id);

        defaultContoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultContoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllContosByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where nome equals to DEFAULT_NOME
        defaultContoShouldBeFound("nome.equals=" + DEFAULT_NOME);

        // Get all the contoList where nome equals to UPDATED_NOME
        defaultContoShouldNotBeFound("nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllContosByNomeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where nome not equals to DEFAULT_NOME
        defaultContoShouldNotBeFound("nome.notEquals=" + DEFAULT_NOME);

        // Get all the contoList where nome not equals to UPDATED_NOME
        defaultContoShouldBeFound("nome.notEquals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllContosByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where nome in DEFAULT_NOME or UPDATED_NOME
        defaultContoShouldBeFound("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME);

        // Get all the contoList where nome equals to UPDATED_NOME
        defaultContoShouldNotBeFound("nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllContosByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where nome is not null
        defaultContoShouldBeFound("nome.specified=true");

        // Get all the contoList where nome is null
        defaultContoShouldNotBeFound("nome.specified=false");
    }

    @Test
    @Transactional
    void getAllContosByNomeContainsSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where nome contains DEFAULT_NOME
        defaultContoShouldBeFound("nome.contains=" + DEFAULT_NOME);

        // Get all the contoList where nome contains UPDATED_NOME
        defaultContoShouldNotBeFound("nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllContosByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where nome does not contain DEFAULT_NOME
        defaultContoShouldNotBeFound("nome.doesNotContain=" + DEFAULT_NOME);

        // Get all the contoList where nome does not contain UPDATED_NOME
        defaultContoShouldBeFound("nome.doesNotContain=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllContosByIbanIsEqualToSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where iban equals to DEFAULT_IBAN
        defaultContoShouldBeFound("iban.equals=" + DEFAULT_IBAN);

        // Get all the contoList where iban equals to UPDATED_IBAN
        defaultContoShouldNotBeFound("iban.equals=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllContosByIbanIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where iban not equals to DEFAULT_IBAN
        defaultContoShouldNotBeFound("iban.notEquals=" + DEFAULT_IBAN);

        // Get all the contoList where iban not equals to UPDATED_IBAN
        defaultContoShouldBeFound("iban.notEquals=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllContosByIbanIsInShouldWork() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where iban in DEFAULT_IBAN or UPDATED_IBAN
        defaultContoShouldBeFound("iban.in=" + DEFAULT_IBAN + "," + UPDATED_IBAN);

        // Get all the contoList where iban equals to UPDATED_IBAN
        defaultContoShouldNotBeFound("iban.in=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllContosByIbanIsNullOrNotNull() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where iban is not null
        defaultContoShouldBeFound("iban.specified=true");

        // Get all the contoList where iban is null
        defaultContoShouldNotBeFound("iban.specified=false");
    }

    @Test
    @Transactional
    void getAllContosByIbanContainsSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where iban contains DEFAULT_IBAN
        defaultContoShouldBeFound("iban.contains=" + DEFAULT_IBAN);

        // Get all the contoList where iban contains UPDATED_IBAN
        defaultContoShouldNotBeFound("iban.contains=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllContosByIbanNotContainsSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where iban does not contain DEFAULT_IBAN
        defaultContoShouldNotBeFound("iban.doesNotContain=" + DEFAULT_IBAN);

        // Get all the contoList where iban does not contain UPDATED_IBAN
        defaultContoShouldBeFound("iban.doesNotContain=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllContosByUserNameIsEqualToSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where userName equals to DEFAULT_USER_NAME
        defaultContoShouldBeFound("userName.equals=" + DEFAULT_USER_NAME);

        // Get all the contoList where userName equals to UPDATED_USER_NAME
        defaultContoShouldNotBeFound("userName.equals=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllContosByUserNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where userName not equals to DEFAULT_USER_NAME
        defaultContoShouldNotBeFound("userName.notEquals=" + DEFAULT_USER_NAME);

        // Get all the contoList where userName not equals to UPDATED_USER_NAME
        defaultContoShouldBeFound("userName.notEquals=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllContosByUserNameIsInShouldWork() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where userName in DEFAULT_USER_NAME or UPDATED_USER_NAME
        defaultContoShouldBeFound("userName.in=" + DEFAULT_USER_NAME + "," + UPDATED_USER_NAME);

        // Get all the contoList where userName equals to UPDATED_USER_NAME
        defaultContoShouldNotBeFound("userName.in=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllContosByUserNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where userName is not null
        defaultContoShouldBeFound("userName.specified=true");

        // Get all the contoList where userName is null
        defaultContoShouldNotBeFound("userName.specified=false");
    }

    @Test
    @Transactional
    void getAllContosByUserNameContainsSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where userName contains DEFAULT_USER_NAME
        defaultContoShouldBeFound("userName.contains=" + DEFAULT_USER_NAME);

        // Get all the contoList where userName contains UPDATED_USER_NAME
        defaultContoShouldNotBeFound("userName.contains=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllContosByUserNameNotContainsSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where userName does not contain DEFAULT_USER_NAME
        defaultContoShouldNotBeFound("userName.doesNotContain=" + DEFAULT_USER_NAME);

        // Get all the contoList where userName does not contain UPDATED_USER_NAME
        defaultContoShouldBeFound("userName.doesNotContain=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllContosByAbiIsEqualToSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where abi equals to DEFAULT_ABI
        defaultContoShouldBeFound("abi.equals=" + DEFAULT_ABI);

        // Get all the contoList where abi equals to UPDATED_ABI
        defaultContoShouldNotBeFound("abi.equals=" + UPDATED_ABI);
    }

    @Test
    @Transactional
    void getAllContosByAbiIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where abi not equals to DEFAULT_ABI
        defaultContoShouldNotBeFound("abi.notEquals=" + DEFAULT_ABI);

        // Get all the contoList where abi not equals to UPDATED_ABI
        defaultContoShouldBeFound("abi.notEquals=" + UPDATED_ABI);
    }

    @Test
    @Transactional
    void getAllContosByAbiIsInShouldWork() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where abi in DEFAULT_ABI or UPDATED_ABI
        defaultContoShouldBeFound("abi.in=" + DEFAULT_ABI + "," + UPDATED_ABI);

        // Get all the contoList where abi equals to UPDATED_ABI
        defaultContoShouldNotBeFound("abi.in=" + UPDATED_ABI);
    }

    @Test
    @Transactional
    void getAllContosByAbiIsNullOrNotNull() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where abi is not null
        defaultContoShouldBeFound("abi.specified=true");

        // Get all the contoList where abi is null
        defaultContoShouldNotBeFound("abi.specified=false");
    }

    @Test
    @Transactional
    void getAllContosByAbiContainsSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where abi contains DEFAULT_ABI
        defaultContoShouldBeFound("abi.contains=" + DEFAULT_ABI);

        // Get all the contoList where abi contains UPDATED_ABI
        defaultContoShouldNotBeFound("abi.contains=" + UPDATED_ABI);
    }

    @Test
    @Transactional
    void getAllContosByAbiNotContainsSomething() throws Exception {
        // Initialize the database
        contoRepository.saveAndFlush(conto);

        // Get all the contoList where abi does not contain DEFAULT_ABI
        defaultContoShouldNotBeFound("abi.doesNotContain=" + DEFAULT_ABI);

        // Get all the contoList where abi does not contain UPDATED_ABI
        defaultContoShouldBeFound("abi.doesNotContain=" + UPDATED_ABI);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultContoShouldBeFound(String filter) throws Exception {
        restContoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].iban").value(hasItem(DEFAULT_IBAN)))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME)))
            .andExpect(jsonPath("$.[*].abi").value(hasItem(DEFAULT_ABI)));

        // Check, that the count call also returns 1
        restContoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultContoShouldNotBeFound(String filter) throws Exception {
        restContoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restContoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
        updatedConto.nome(UPDATED_NOME).iban(UPDATED_IBAN).userName(UPDATED_USER_NAME).abi(UPDATED_ABI);

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
        assertThat(testConto.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testConto.getAbi()).isEqualTo(UPDATED_ABI);
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

        partialUpdatedConto.nome(UPDATED_NOME).userName(UPDATED_USER_NAME).abi(UPDATED_ABI);

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
        assertThat(testConto.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testConto.getAbi()).isEqualTo(UPDATED_ABI);
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

        partialUpdatedConto.nome(UPDATED_NOME).iban(UPDATED_IBAN).userName(UPDATED_USER_NAME).abi(UPDATED_ABI);

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
        assertThat(testConto.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testConto.getAbi()).isEqualTo(UPDATED_ABI);
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
