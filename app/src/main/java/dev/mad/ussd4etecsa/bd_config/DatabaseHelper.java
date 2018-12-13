package dev.mad.ussd4etecsa.bd_config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import dev.mad.ussd4etecsa.models.Tables.AuxConfig;
import dev.mad.ussd4etecsa.models.Tables.DatAmigo;
import dev.mad.ussd4etecsa.models.Tables.DatTranferencia;
import dev.mad.ussd4etecsa.models.Tables.DatUssd;

/**
 * Created by Daymel on 7/5/2017.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "USSD4ETECSA";
    private static final int DATABASE_VERSION = 6;

    private Dao<DatUssd, Integer> ussdDao = null;
    private RuntimeExceptionDao<DatUssd, Integer> ussdRuntimeDao = null;

    private Dao<AuxConfig, Integer> configDao = null;
    private RuntimeExceptionDao<AuxConfig, Integer> configRuntimeDao = null;

    private Dao<DatTranferencia, Integer> tranferenciaDao = null;
    private RuntimeExceptionDao<DatTranferencia, Integer> transfereneciaRuntimeDao = null;

    private Dao<DatAmigo, Integer> amigoIntegerDao = null;
    private RuntimeExceptionDao<DatAmigo, Integer> amigoruntimeExceptionDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * Creación de la Base de png_datos
     *
     * @param database
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            Log.i(DatabaseHelper.class.getSimpleName(), "Creando base de datos");
            TableUtils.createTable(connectionSource, DatUssd.class);
            TableUtils.createTable(connectionSource, AuxConfig.class);
            TableUtils.createTable(connectionSource, DatTranferencia.class);
            TableUtils.createTable(connectionSource, DatAmigo.class);
            RuntimeExceptionDao<DatUssd, Integer> ussdDao = getUssdRuntimeDao();
            ussdDao.create(new DatUssd("SALDO", "0.00", "0/00/0000"));
            ussdDao.create(new DatUssd("BONO", "0.00", "0/00/0000"));
            ussdDao.create(new DatUssd("VOZ", "0:00:00", "0"));
            ussdDao.create(new DatUssd("SMS", "0", "0"));
            ussdDao.create(new DatUssd("BOLSA", "0.00", "0"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualizar la Base de png_datos
     *
     * @param database
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getSimpleName(), "Actualizando base de datos");
            TableUtils.dropTable(connectionSource, DatUssd.class, true);
            TableUtils.dropTable(connectionSource, AuxConfig.class, true);
            TableUtils.dropTable(connectionSource, DatTranferencia.class, true);
            TableUtils.dropTable(connectionSource, DatAmigo.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     * @throws SQLException
     */
    public Dao<DatUssd, Integer> getUssdDao() throws SQLException {
        if (ussdDao == null) {
            ussdDao = getDao(DatUssd.class);

        }
        return ussdDao;
    }

    /**
     * @return
     */
    public RuntimeExceptionDao<DatUssd, Integer> getUssdRuntimeDao() {
        if (ussdRuntimeDao == null) {
            ussdRuntimeDao = getRuntimeExceptionDao(DatUssd.class);
        }
        return ussdRuntimeDao;
    }


    public Dao<AuxConfig, Integer> getConfigDao() throws SQLException {
        if (configDao == null) {
            configDao = getDao(AuxConfig.class);

        }
        return configDao;
    }

    public RuntimeExceptionDao<AuxConfig, Integer> getConfigRuntimeDao() throws SQLException {
        if (configRuntimeDao == null) {
            configRuntimeDao = getRuntimeExceptionDao(AuxConfig.class);
        }
        return configRuntimeDao;
    }

    public Dao<DatTranferencia, Integer> getTranferenciaDao() throws SQLException {
        if (tranferenciaDao == null) {
            tranferenciaDao = getDao(DatTranferencia.class);

        }
        return tranferenciaDao;
    }

    public RuntimeExceptionDao<DatTranferencia, Integer> getTransfereneciaRuntimeDao() throws SQLException {
        if (transfereneciaRuntimeDao == null) {
            transfereneciaRuntimeDao = getRuntimeExceptionDao(DatTranferencia.class);
        }
        return transfereneciaRuntimeDao;
    }

    public Dao<DatAmigo, Integer> getAmigoIntegerDao() throws SQLException {
       if(amigoIntegerDao ==null){
           amigoIntegerDao =getDao(DatAmigo.class);
       }
        return amigoIntegerDao;
    }

    public RuntimeExceptionDao<DatAmigo, Integer> getAmigoruntimeExceptionDao()throws SQLException {
        if(amigoruntimeExceptionDao == null){
            amigoruntimeExceptionDao = getRuntimeExceptionDao(DatAmigo.class);
        }
        return amigoruntimeExceptionDao;
    }

    /**
     *
     */
    @Override
    public void close() {
        super.close();
        ussdDao = null;
        ussdRuntimeDao = null;
    }
}
