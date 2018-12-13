package dev.mad.ussd4etecsa.models.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Daymel on 21/5/2017.
 */
@DatabaseTable
public class DatUssd {

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String name;
    @DatabaseField
    String valor;
    @DatabaseField
    String fechavencimiento;
    @DatabaseField
    String fecha;

    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yy");
    public DatUssd(){

    }

    public DatUssd(String name, String valor, String fechavencimiento) {
        this.name = name;
        this.valor = valor;
        this.fechavencimiento = fechavencimiento;
        this.fecha = formateador.format(new Date(System.currentTimeMillis()));
    }

    @Override
    public String toString() {
        return "DatUssd{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", valor='" + valor + '\'' +
                ", fechavencimiento='" + fechavencimiento + '\'' +
                ", fecha=" + fecha +
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

    public String getFechavencimiento() {
        return fechavencimiento;
    }

    public void setFechavencimiento(String fechavencimiento) {
        this.fechavencimiento = fechavencimiento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
