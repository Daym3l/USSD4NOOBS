package dev.mad.ussd4etecsa.models;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.bd_config.DatabaseHelper;
import dev.mad.ussd4etecsa.models.Tables.DatUssd;

/**
 * Created by Daymel on 02/08/2017.
 */

public class UssdModel {
    DatabaseHelper dbHelper;

    public String getValor(String valor, Context context) throws SQLException {
        dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
        List<DatUssd> listValores = ussdDao.queryForEq("name", valor);
        return listValores.get(0).getValor();
    }

    public void updateSaldo(String valor, String vence, String opcion, Context context) throws SQLException {

        dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
        UpdateBuilder<DatUssd, Integer> updateBuilder = ussdDao.updateBuilder();
        valor = (valor.equals("de")) ? "0" : valor;
        vence = (vence.equals("plan.")) ? "0" : vence;
        vence = (vence.equals("hoy.")) ? "1" : vence;
        updateBuilder.where().eq("name", opcion);
        updateBuilder.updateColumnValue("valor", valor);
        updateBuilder.updateColumnValue("fechavencimiento", vence);
        updateBuilder.update();


    }
}

