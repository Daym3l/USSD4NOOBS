package dev.mad.ussd4etecsa.updateSaldo;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.models.UssdModel;

public class Voz implements ISaldo {

    Context context;
    UssdModel model;

    public Voz(Context context) {
        this.context = context;
        this.model = new UssdModel();
    }

    @Override
    public void UpdateSaldo(List<String> valores) {
        try {
            if (valores.get(2).equals("comprar")) {
                this.model.updateSaldo("0:00:00", "0", "VOZ", this.context);
            } else {
                this.model.updateSaldo(valores.get(3), valores.get(7), "VOZ", this.context);
            }
        } catch (SQLException e) {
            Log.e("ERROR VOZ", e.toString());
        }
    }
}
