package dev.mad.ussd4etecsa.models;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.bd_config.DatabaseHelper;
import dev.mad.ussd4etecsa.models.Tables.AuxConfig;

/**
 * Created by Daymel on 23/6/2017.
 */

public class AuxConfigModel {

    DatabaseHelper dbHelper;


    /**
     * Verificar si existe una configuracion predeterminada
     *
     * @param config
     * @return
     * @throws SQLException
     */
    public boolean existeConfig(String config, Context context) throws SQLException {
        boolean flag = false;
        dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        RuntimeExceptionDao<AuxConfig, Integer> configDao = dbHelper.getConfigRuntimeDao();
        List<AuxConfig> listConfig = configDao.queryForEq("name", config);
        if (listConfig.size() > 0) {

            flag = true;
        }
        return flag;
    }

    /**
     * Actualiza la configuración
     * @param config
     * @throws SQLException
     */
    public void updateConfig(String config, String valor,Context context) throws SQLException {
        RuntimeExceptionDao<AuxConfig, Integer> configDao = dbHelper.getConfigRuntimeDao();
        if (existeConfig(config,context)) {
            updateValor(valor, config);

        } else {
            configDao.create(new AuxConfig(config, "1"));
            Log.i("Config", "Configuracion creada");
        }
    }

    /**
     * Actulizar el valor de la configuración
     * @param valor
     */
    public void updateValor(String valor, String config) throws SQLException {
        RuntimeExceptionDao<AuxConfig, Integer> configDao = dbHelper.getConfigRuntimeDao();
        UpdateBuilder<AuxConfig, Integer> updateBuilder = configDao.updateBuilder();
        updateBuilder.where().eq("name", config);
        updateBuilder.updateColumnValue("valor", valor);
        updateBuilder.update();

        Log.i("Config", "Configuracion actualizada");
    }

    /**
     * Obtener valor de la configuración
     * @param config
     * @return
     * @throws SQLException
     */
    public String getValorConfig(String config) throws SQLException {
        RuntimeExceptionDao<AuxConfig, Integer> configDao = dbHelper.getConfigRuntimeDao();
        List<AuxConfig> listConfig = configDao.queryForEq("name", config);
    return listConfig.get(0).getValor();
    }

}
