package dev.mad.ussd4etecsa.updateSaldo;

import java.sql.SQLException;
import java.util.List;

public interface ISaldo {

    void UpdateSaldo(List<String> cadena) throws SQLException;
}
