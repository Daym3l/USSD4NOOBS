package dev.mad.ussd4etecsa.models;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.bd_config.DatabaseHelper;
import dev.mad.ussd4etecsa.models.Tables.DatAmigo;

/**
 * Created by Daymel on 26/6/2018.
 */

public class AmigosModel {
    DatabaseHelper dbHelper;
    Context mcontext;

    public AmigosModel(Context context){
        this.dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        this.mcontext = context;
    }

    public void insertAmigo(DatAmigo amigo) throws SQLException {

        RuntimeExceptionDao<DatAmigo, Integer> datPlanAmigoses = this.dbHelper.getAmigoruntimeExceptionDao();
        datPlanAmigoses.create(amigo);
        Log.i("AMIGO", "amigo insertado");
    }

    /**
     * @return
     * @throws SQLException
     */
    public List<DatAmigo> getAmigos() throws SQLException {
        RuntimeExceptionDao<DatAmigo, Integer> amigosIntegerRuntimeExceptionDao = this.dbHelper.getAmigoruntimeExceptionDao();
        QueryBuilder<DatAmigo, Integer> builder = amigosIntegerRuntimeExceptionDao.queryBuilder();
        builder.orderBy("id", false);
        List<DatAmigo> listTranferencia = amigosIntegerRuntimeExceptionDao.query(builder.prepare());

        return listTranferencia;
    }
    public void delAmigo(int id) throws SQLException {
        RuntimeExceptionDao<DatAmigo, Integer> amigosIntegerRuntimeExceptionDao = this.dbHelper.getAmigoruntimeExceptionDao();
        DeleteBuilder<DatAmigo, Integer> deleteBuilder = amigosIntegerRuntimeExceptionDao.deleteBuilder();
        deleteBuilder.where().eq("id",id);
        deleteBuilder.delete();
    }
}
