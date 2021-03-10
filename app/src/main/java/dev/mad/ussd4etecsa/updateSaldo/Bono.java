package dev.mad.ussd4etecsa.updateSaldo;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

import dev.mad.ussd4etecsa.models.UssdModel;
import dev.mad.ussd4etecsa.utiles.Util;

public class Bono implements ISaldo {

    Context context;
    UssdModel model;

    public Bono(Context context) {
        this.context = context;
        this.model = new UssdModel();
    }

    @Override
    public void UpdateSaldo(List<String> valores) throws SQLException {


        if (valores.get(0).equals("Bono->vence:")) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "->");
            String valor = "";
            valores.remove(new String("->"));
            for (int i = 1; i < valores.size(); i++) {
                valor += valores.get(i) + " ";
            }

            String result = valor.replaceAll("->", " vence: ");
            this.model.updateSaldo(result, Util.getResultDate(valores.get(valores.size() - 1)), "BONO", this.context);
        } else {
            this.model.updateSaldo("0.00", "00-00-0000", "BONO", this.context);
        }


    }
}
