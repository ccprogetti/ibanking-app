package it.addvalue.ibanking.gateway.repository.rowmapper;

import io.r2dbc.spi.Row;
import it.addvalue.ibanking.gateway.domain.Conto;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Conto}, with proper type conversions.
 */
@Service
public class ContoRowMapper implements BiFunction<Row, String, Conto> {

    private final ColumnConverter converter;

    public ContoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Conto} stored in the database.
     */
    @Override
    public Conto apply(Row row, String prefix) {
        Conto entity = new Conto();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setIban(converter.fromRow(row, prefix + "_iban", String.class));
        return entity;
    }
}
