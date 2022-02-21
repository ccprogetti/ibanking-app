package it.addvalue.ibanking.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BonificoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("causale", table, columnPrefix + "_causale"));
        columns.add(Column.aliased("destinatario", table, columnPrefix + "_destinatario"));
        columns.add(Column.aliased("importo", table, columnPrefix + "_importo"));
        columns.add(Column.aliased("data_esecuzione", table, columnPrefix + "_data_esecuzione"));
        columns.add(Column.aliased("iban_destinatario", table, columnPrefix + "_iban_destinatario"));

        return columns;
    }
}
