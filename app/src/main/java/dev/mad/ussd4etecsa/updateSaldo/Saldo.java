package dev.mad.ussd4etecsa.updateSaldo;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.models.UssdModel;

public class Saldo implements ISaldo {

    Context context;
    UssdModel model;


    public Saldo(Context context) {
        this.context = context;
        this.model = new UssdModel();
    }

    @Override
    public void UpdateSaldo(List<String> valores) {
        try {
            this.model.updateSaldo(valores.get(1).replace(',', ' '), valores.get(6), "SALDO", this.context);
        } catch (SQLException e) {
            Log.e("ERROR SALDO", e.toString());
        }

    }
}
