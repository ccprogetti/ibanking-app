package it.addvalue.ibanking.gateway.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Bonifico.
 */
@Table("bonifico")
public class Bonifico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    @Column("causale")
    private String causale;

    @NotNull(message = "must not be null")
    @Size(min = 10)
    @Column("destinatario")
    private String destinatario;

    @Column("importo")
    private BigDecimal importo;

    @Column("data_esecuzione")
    private LocalDate dataEsecuzione;

    @Column("iban_destinatario")
    private String ibanDestinatario;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Bonifico id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCausale() {
        return this.causale;
    }

    public Bonifico causale(String causale) {
        this.setCausale(causale);
        return this;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public String getDestinatario() {
        return this.destinatario;
    }

    public Bonifico destinatario(String destinatario) {
        this.setDestinatario(destinatario);
        return this;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public BigDecimal getImporto() {
        return this.importo;
    }

    public Bonifico importo(BigDecimal importo) {
        this.setImporto(importo);
        return this;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo != null ? importo.stripTrailingZeros() : null;
    }

    public LocalDate getDataEsecuzione() {
        return this.dataEsecuzione;
    }

    public Bonifico dataEsecuzione(LocalDate dataEsecuzione) {
        this.setDataEsecuzione(dataEsecuzione);
        return this;
    }

    public void setDataEsecuzione(LocalDate dataEsecuzione) {
        this.dataEsecuzione = dataEsecuzione;
    }

    public String getIbanDestinatario() {
        return this.ibanDestinatario;
    }

    public Bonifico ibanDestinatario(String ibanDestinatario) {
        this.setIbanDestinatario(ibanDestinatario);
        return this;
    }

    public void setIbanDestinatario(String ibanDestinatario) {
        this.ibanDestinatario = ibanDestinatario;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bonifico)) {
            return false;
        }
        return id != null && id.equals(((Bonifico) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bonifico{" +
            "id=" + getId() +
            ", causale='" + getCausale() + "'" +
            ", destinatario='" + getDestinatario() + "'" +
            ", importo=" + getImporto() +
            ", dataEsecuzione='" + getDataEsecuzione() + "'" +
            ", ibanDestinatario='" + getIbanDestinatario() + "'" +
            "}";
    }
}
