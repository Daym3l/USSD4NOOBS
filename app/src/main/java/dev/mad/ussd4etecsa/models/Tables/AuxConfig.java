package dev.mad.ussd4etecsa.models.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Daymel on 19/6/2017.
 */
@DatabaseTable
public class AuxConfig {

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String name;
    @DatabaseField
    String valor;

    public AuxConfig() {
    }

    public AuxConfig(String name, String valor) {

        this.name = name;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "AuxConfig{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
