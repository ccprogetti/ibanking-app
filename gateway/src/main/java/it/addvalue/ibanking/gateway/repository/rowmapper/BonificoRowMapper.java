package it.addvalue.ibanking.gateway.repository.rowmapper;

import io.r2dbc.spi.Row;
import it.addvalue.ibanking.gateway.domain.Bonifico;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Bonifico}, with proper type conversions.
 */
@Service
public class BonificoRowMapper implements BiFunction<Row, String, Bonifico> {

    private final ColumnConverter converter;

    public BonificoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Bonifico} stored in the database.
     */
    @Override
    public Bonifico apply(Row row, String prefix) {
        Bonifico entity = new Bonifico();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCausale(converter.fromRow(row, prefix + "_causale", String.class));
        entity.setDestinatario(converter.fromRow(row, prefix + "_destinatario", String.class));
        entity.setImporto(converter.fromRow(row, prefix + "_importo", BigDecimal.class));
        entity.setDataEsecuzione(converter.fromRow(row, prefix + "_data_esecuzione", LocalDate.class));
        entity.setIbanDestinatario(converter.fromRow(row, prefix + "_iban_destinatario", String.class));
        return entity;
    }
}
