package fr.optivision.argentica.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.optivision.argentica.data.database.dao.*
import fr.optivision.argentica.data.model.*
import fr.optivision.argentica.data.model.association.FilmSessionCrossRef
import fr.optivision.argentica.data.model.association.SessionCategoryCrossRef

@Database(
    entities = [Session::class, Photo::class, Camera::class, Film::class, Lens::class, Place::class, Category::class, SessionCategoryCrossRef::class, WitnessPhoto::class, FilmSessionCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class ArgenticaDatabase : RoomDatabase() {

    abstract fun filmDao(): FilmDao
    abstract fun lensDao(): LensDao
    abstract fun placeDao(): PlaceDao
    abstract fun photoDao(): PhotoDao
    abstract fun cameraDao(): CameraDao
    abstract fun sessionDao(): SessionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun witnessPhotoDao(): WitnessPhotoDao
    abstract fun sessionCategoryCrossRefDao(): SessionCategoryCrossRefDao
    abstract fun filmSessionCrossRefDao(): FilmSessionCrossRefDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: ArgenticaDatabase

        fun init(context: Context) {
            INSTANCE = Room.databaseBuilder(context.applicationContext, ArgenticaDatabase::class.java, "database.db").build()
        }

        fun getInstance(): ArgenticaDatabase {
            return INSTANCE
        }
    }
}