package it.addvalue.ibanking.conti.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.addvalue.ibanking.conti.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Conto.class);
        Conto conto1 = new Conto();
        conto1.setId(1L);
        Conto conto2 = new Conto();
        conto2.setId(conto1.getId());
        assertThat(conto1).isEqualTo(conto2);
        conto2.setId(2L);
        assertThat(conto1).isNotEqualTo(conto2);
        conto1.setId(null);
        assertThat(conto1).isNotEqualTo(conto2);
    }
}
