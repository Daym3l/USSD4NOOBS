package dev.mad.ussd4etecsa.models.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Daymel on 30/10/2017.
 */

@DatabaseTable
public class DatTranferencia {

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String numeroContacto;
    @DatabaseField
    String valorTransferido;
    @DatabaseField
    String fechaTransferencia;
    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yy");

    public DatTranferencia(String numeroContacto, String valorTransferido) {
        this.numeroContacto = numeroContacto;
        this.valorTransferido = valorTransferido;
        this.fechaTransferencia = formateador.format(new Date(System.currentTimeMillis()));;

    }

    @Override
    public String toString() {
        return "DatTranferencia{" +
                "id=" + id +
                ", numeroContacto='" + numeroContacto + '\'' +
                ", valorTransferido='" + valorTransferido + '\'' +
                ", fechaTransferencia='" + fechaTransferencia + '\'' +
                ", formateador=" + formateador +
                '}';
    }

    public DatTranferencia() {

    }

    public String getNumeroContacto() {
        return numeroContacto;
    }

    public void setNumeroContacto(String numeroContacto) {
        this.numeroContacto = numeroContacto;
    }

    public String getValorTransferido() {
        return valorTransferido;
    }

    public void setValorTransferido(String valorTransferido) {
        this.valorTransferido = valorTransferido;
    }

    public String getFechaTransferencia() {
        return fechaTransferencia;
    }

    public void setFechaTransferencia(String fechaTransferencia) {
        this.fechaTransferencia = fechaTransferencia;
    }

}
