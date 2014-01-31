package de.najidev.mensaupb.persistence;

import android.content.*;
import android.database.sqlite.*;

import com.j256.ormlite.android.apptools.*;
import com.j256.ormlite.dao.*;
import com.j256.ormlite.support.*;
import com.j256.ormlite.table.*;

import org.slf4j.*;

import java.sql.*;

import de.najidev.mensaupb.rest.*;

/**
 * Created by ljan on 11.01.14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "mensaupb.db";
    private static final int DATABASE_VERSION = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class.getSimpleName());
    private Dao<Restaurant, Long> restaurantDao;
    private Dao<MenuContent, Long> menuContentDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            LOGGER.info("onCreate()");
            TableUtils.createTable(connectionSource, Restaurant.class);
            TableUtils.createTable(connectionSource, MenuContent.class);
            LOGGER.info("Created database.");
        } catch (SQLException e) {
            LOGGER.error("Can't create database", e);
            throw new RuntimeException(e);
        }
        LOGGER.info("onCreate() done");
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public Dao<Restaurant, Long> getRestaurantDao() throws SQLException {
        if (restaurantDao == null) {
            restaurantDao = getDao(Restaurant.class);
        }
        return restaurantDao;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
//        try {
//            LOGGER.info("onUpgrade()");
//            switch (old){
//                case 1:
//                    Dao<Account, Integer> dao = getHelper().getAccountDao();
//// change the table to add a new column named "age"
//                    dao.executeRaw("ALTER TABLE `account` ADD COLUMN age INTEGER;"); }
//
//        } catch (SQLException e) {
//            LOGGER.error("Can't drop databases", e);
//            throw new RuntimeException(e);
//        }
        LOGGER.info("onUpgrade() done");
    }

    public Dao<MenuContent, Long> getMenuContentDao() throws SQLException {
        if (menuContentDao == null) {
            menuContentDao = getDao(MenuContent.class);
        }
        return menuContentDao;
    }

}