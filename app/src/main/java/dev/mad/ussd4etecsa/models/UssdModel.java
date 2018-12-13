package dev.mad.ussd4etecsa.models;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

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
}

