package me.martichou.be.grateful.persistance.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.martichou.be.grateful.data.model.Notes
import me.martichou.be.grateful.persistance.dao.NotesDao
import me.martichou.be.grateful.utils.stringToDate
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

@Database(entities = [Notes::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Return the instance of the db
         */
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        /**
         * Build the database.
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "notes")
                    .addMigrations(MIGRATION_1_2).build()
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val r = UtilsDb().getResults(database)

                for (i in 0 until r.length()) {
                    val jsonobject = r.getJSONObject(i)

                    Timber.d("OBJECT $i")

                    val dateToSearch = jsonobject.getString("dateToSearch")
                    val date = (stringToDate(dateToSearch)?.time)?.plus(jsonobject.getInt("id")).toString()
                    Timber.d("Result ${jsonobject.getInt("id")} = $date")

                    database.query("UPDATE notes SET date = $date WHERE id=${jsonobject.getInt("id")}", null).moveToNext()

                    Timber.d("Migration for $i done.")
                }
            }
        }
    }
}

class UtilsDb {
    fun getResults(database: SupportSQLiteDatabase): JSONArray {
        val queryString = "SELECT * FROM notes"
        val cursor = database.query(queryString, null)
        val resultSet = JSONArray()

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val totalColumn = cursor.columnCount
            val rowObject = JSONObject()
            for (i in 0 until totalColumn) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i))
                        } else {
                            rowObject.put(cursor.getColumnName(i), "")
                        }
                    } catch (e: Exception) {
                        Timber.d(e)
                    }
                }
            }
            resultSet.put(rowObject)
            cursor.moveToNext()
        }
        cursor.close()
        Timber.d(resultSet.toString())
        return resultSet
    }

    fun getResults(database: AppDatabase): JSONArray {
        val queryString = "SELECT * FROM notes"
        val cursor = database.query(queryString, null)
        val resultSet = JSONArray()

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val totalColumn = cursor.columnCount
            val rowObject = JSONObject()
            for (i in 0 until totalColumn) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i))
                        } else {
                            rowObject.put(cursor.getColumnName(i), "")
                        }
                    } catch (e: Exception) {
                        Timber.d(e)
                    }
                }
            }
            resultSet.put(rowObject)
            cursor.moveToNext()
        }
        cursor.close()
        Timber.d(resultSet.toString())
        return resultSet
    }
}