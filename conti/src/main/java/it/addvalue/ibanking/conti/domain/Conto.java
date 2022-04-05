package it.addvalue.ibanking.conti.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Conto.
 */
@Entity
@Table(name = "conto")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Conto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Size(min = 6)
    @Column(name = "iban")
    private String iban;

    @NotNull
    @Column(name = "user_name", nullable = false)
    private String userName;

    @NotNull
    @Column(name = "abi", nullable = false)
    private String abi;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Conto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Conto nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIban() {
        return this.iban;
    }

    public Conto iban(String iban) {
        this.setIban(iban);
        return this;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getUserName() {
        return this.userName;
    }

    public Conto userName(String userName) {
        this.setUserName(userName);
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAbi() {
        return this.abi;
    }

    public Conto abi(String abi) {
        this.setAbi(abi);
        return this;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Conto)) {
            return false;
        }
        return id != null && id.equals(((Conto) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Conto{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", iban='" + getIban() + "'" +
            ", userName='" + getUserName() + "'" +
            ", abi='" + getAbi() + "'" +
            "}";
    }
}
