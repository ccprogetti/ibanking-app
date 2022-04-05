package it.addvalue.ibanking.conti.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link it.addvalue.ibanking.conti.domain.Conto} entity. This class is used
 * in {@link it.addvalue.ibanking.conti.web.rest.ContoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /contos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ContoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter iban;

    private StringFilter userName;

    private StringFilter abi;

    private Boolean distinct;

    public ContoCriteria() {}

    public ContoCriteria(ContoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nome = other.nome == null ? null : other.nome.copy();
        this.iban = other.iban == null ? null : other.iban.copy();
        this.userName = other.userName == null ? null : other.userName.copy();
        this.abi = other.abi == null ? null : other.abi.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ContoCriteria copy() {
        return new ContoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNome() {
        return nome;
    }

    public StringFilter nome() {
        if (nome == null) {
            nome = new StringFilter();
        }
        return nome;
    }

    public void setNome(StringFilter nome) {
        this.nome = nome;
    }

    public StringFilter getIban() {
        return iban;
    }

    public StringFilter iban() {
        if (iban == null) {
            iban = new StringFilter();
        }
        return iban;
    }

    public void setIban(StringFilter iban) {
        this.iban = iban;
    }

    public StringFilter getUserName() {
        return userName;
    }

    public StringFilter userName() {
        if (userName == null) {
            userName = new StringFilter();
        }
        return userName;
    }

    public void setUserName(StringFilter userName) {
        this.userName = userName;
    }

    public StringFilter getAbi() {
        return abi;
    }

    public StringFilter abi() {
        if (abi == null) {
            abi = new StringFilter();
        }
        return abi;
    }

    public void setAbi(StringFilter abi) {
        this.abi = abi;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ContoCriteria that = (ContoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(iban, that.iban) &&
            Objects.equals(userName, that.userName) &&
            Objects.equals(abi, that.abi) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, iban, userName, abi, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nome != null ? "nome=" + nome + ", " : "") +
            (iban != null ? "iban=" + iban + ", " : "") +
            (userName != null ? "userName=" + userName + ", " : "") +
            (abi != null ? "abi=" + abi + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
