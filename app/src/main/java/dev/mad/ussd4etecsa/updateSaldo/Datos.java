package dev.mad.ussd4etecsa.updateSaldo;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.models.UssdModel;

public class Datos implements ISaldo {

    Context context;
    UssdModel model;

    public Datos(Context context) {
        this.context = context;
        this.model = new UssdModel();
    }

    @Override
    public void UpdateSaldo(List<String> valores) throws SQLException {

        if (valores.get(1).equals("Activa.")) {
            if (valores.get(4).equals("adquirir")) {
                this.model.updateSaldo("0 MB", "0", "BOLSA", this.context);
                return;
            }
            if (valores.size() == 8)
                if ((valores.get(4).equals("KB")) || (valores.get(4).equals("MB")) || (valores.get(4).equals("GB"))) {
                    this.model.updateSaldo(valores.get(3) + " " + valores.get(4), valores.get(6), "BOLSA", this.context);
                    return;
                }
            if (valores.get(8).equals("LTE)")) {
                String texto = valores.get(3) + " " + valores.get(4) + " " + valores.get(5) + " " + valores.get(6) + " " + valores.get(7) + " " + valores.get(8);
                this.model.updateSaldo(texto, valores.get(10), "BOLSA", this.context);
            }

        } else {
            if (valores.get(5).equals("adquirir")) {
                this.model.updateSaldo("0 MB", "0", "BOLSA", this.context);
            }
            if (valores.size() == 9)
                if ((valores.get(5).equals("KB")) || (valores.get(5).equals("MB")) || (valores.get(5).equals("GB"))) {
                    this.model.updateSaldo(valores.get(4) + " " + valores.get(5), valores.get(7), "BOLSA", this.context);
                    return;
                }
            if (valores.get(9).equals("LTE)")) {
                String texto = valores.get(4) + " " + valores.get(5) + " " + valores.get(6) + " " + valores.get(7) + " " + valores.get(8) + " " + valores.get(9);
                this.model.updateSaldo(texto, valores.get(11), "BOLSA", this.context);
                return;
            }

        }
    }
}
