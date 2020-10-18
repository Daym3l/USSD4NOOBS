package dev.mad.ussd4etecsa.updateSaldo;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.models.UssdModel;

public class Sms implements ISaldo {

    Context context;
    UssdModel model;

    public Sms(Context context) {
        this.context = context;
        this.model = new UssdModel();
    }

    @Override
    public void UpdateSaldo(List<String> valores) throws SQLException {
        if (valores.get(2).equals("comprar")) {
            this.model.updateSaldo("0", "0", "SMS", this.context);
        } else {
            this.model.updateSaldo(valores.get(3), valores.get(7), "SMS", this.context);
        }
    }
}
