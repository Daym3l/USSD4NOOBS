package dev.mad.ussd4etecsa.models.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Daymel on 26/6/2018.
 */
@DatabaseTable
public class DatAmigo {

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String numeroContacto;

    public DatAmigo(){

    };
    public DatAmigo(String numeroContacto){
        this.numeroContacto = numeroContacto;
    }

    public int getId() {
        return id;
    }

    public String getNumeroContacto() {
        return numeroContacto;
    }

    @Override
    public String toString() {
        return numeroContacto;
    }
}
