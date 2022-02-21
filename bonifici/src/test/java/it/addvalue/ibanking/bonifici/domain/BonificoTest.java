package it.addvalue.ibanking.bonifici.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.addvalue.ibanking.bonifici.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BonificoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bonifico.class);
        Bonifico bonifico1 = new Bonifico();
        bonifico1.setId(1L);
        Bonifico bonifico2 = new Bonifico();
        bonifico2.setId(bonifico1.getId());
        assertThat(bonifico1).isEqualTo(bonifico2);
        bonifico2.setId(2L);
        assertThat(bonifico1).isNotEqualTo(bonifico2);
        bonifico1.setId(null);
        assertThat(bonifico1).isNotEqualTo(bonifico2);
    }
}
