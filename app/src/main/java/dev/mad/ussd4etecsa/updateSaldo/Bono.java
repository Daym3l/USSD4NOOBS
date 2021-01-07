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

        if (valores.size() > 4 && valores.get(4).equals("bonos")) {
            this.model.updateSaldo("00:00:00", "0-00-00", "BONO", this.context);
        }

        if (valores.get(0).equals("Bono:Min.")) {
            String valor = valores.get(1);
            valor += " " + valores.get(5);
            this.model.updateSaldo(valor, valores.get(3), "BONO", this.context);
        }

        if (valores.size() == 10) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    String valor = valores.get(3);
                    valor += " " + valores.get(7);
                    valor += " " + val;
                    this.model.updateSaldo(valor, valores.get(9), "BONO", this.context);

                }
            }
        }
        if (valores.size() == 10 && valores.get(0).equals("Bono->vence:")) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "->");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                if (bonos.equals("Bono")) {
                    String valor = Util.getResultText(valores.get(2));
                    valor += " + " + Util.getResultText(valores.get(4)) + " MIN + " + Util.getResultText(valores.get(6)) + " SMS";
                    valor += " + " + valores.get(8) + " MB de navegación nacional";
                    this.model.updateSaldo(valor, Util.getResultDate(valores.get(9)), "BONO", this.context);

                }
            }
        }
        if (valores.size() == 6 && valores.get(0).equals("Bono->vence:")) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "->");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                if (bonos.equals("Bono")) {
                    String valor = Util.getResultText(valores.get(1));
                    valor += " + " + Util.getResultText(valores.get(3)) + " MIN + " + Util.getResultText(valores.get(5)) + " SMS";
                    this.model.updateSaldo(valor, Util.getResultDate(valores.get(1)), "BONO", this.context);

                }
            }
        }
        if (valores.size() == 5 && valores.get(0).equals("Bono->vence:")) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "->");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                if (bonos.equals("Bono")) {
                    String valor = Util.getResultText(valores.get(1));
                    valor += " + " + valores.get(3) + " MB de navegación nacional";
                    this.model.updateSaldo(valor, Util.getResultDate(valores.get(1)), "BONO", this.context);

                }
            }
        }
        if (valores.size() == 9 && valores.get(0).equals("Bono->vence:")) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "->");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                if (bonos.equals("Bono")) {
                    String valor = Util.getResultText(valores.get(1));
                    valor += " + " + Util.getResultText(valores.get(3)) + " MIN + " + Util.getResultText(valores.get(5)) + " SMS";
                    valor += " + " + valores.get(7) + " MB de navegación nacional";
                    this.model.updateSaldo(valor, Util.getResultDate(valores.get(1)), "BONO", this.context);

                }
            }
        }
        if (valores.size() == 12 && valores.get(0).equals("Bono->vence:")) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "->");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                if (bonos.equals("Bono")) {
                    String valor = Util.getResultText(valores.get(1));
                    valor += " + " + Util.getResultText(valores.get(6)) + " MIN + " + Util.getResultText(valores.get(8)) + " SMS";
                    valor += " + " + valores.get(3) + " " + Util.getResultText(valores.get(4));
                    valor += " + " + valores.get(10) + " MB de navegación nacional";
                    this.model.updateSaldo(valor, Util.getResultDate(valores.get(1)), "BONO", this.context);

                }
            }
        }
        if (valores.size() == 7) {
            String bono = valores.get(0);
            String venceData = valores.get(2);
            String[] vence = venceData.split("\\.");
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    String valor = "$" + val;
                    valor += " " + valores.get(3);
                    valor += " " + valores.get(4);
                    this.model.updateSaldo(valor, vence[0], "BONO", this.context);

                }
            }
        }
        if (valores.size() == 14) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    String valor = "$" + val;
                    valor += " " + valores.get(3) + " MIN " + valores.get(7) + " SMS";
                    valor += " " + valores.get(10) + " " + valores.get(11);
                    this.model.updateSaldo(valor, valores.get(5), "BONO", this.context);

                }
            }
        }
        if (valores.size() == 11) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    String valor = "$" + val;
                    valor += " " + valores.get(4) + " SMS";
                    valor += " " + valores.get(7) + " " + valores.get(8);
                    this.model.updateSaldo(valor, valores.get(2), "BONO", this.context);

                }
            }
        }

        if (valores.size() == 3) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    this.model.updateSaldo(val, valores.get(2), "BONO", this.context);

                }
            }
        }
        if (valores.size() == 5 && valores.get(0).equals("Bono:Datos.cu")) {
            String valor = valores.get(1);
            valor += valores.get(2);
            this.model.updateSaldo(valor, valores.get(4), "BONO", this.context);
        }
        if (valores.size() == 4 && valores.get(0).equals("Bono->vence:")) {
            String valor = "0.00 + " + valores.get(2) + " MB de navegación nacional";
            this.model.updateSaldo(valor, Util.getResultDate(valores.get(3)), "BONO", this.context);
        }


    }
}
