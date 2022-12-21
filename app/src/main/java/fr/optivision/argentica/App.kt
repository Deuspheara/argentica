package fr.optivision.argentica

import android.app.Application
import fr.optivision.argentica.data.database.ArgenticaDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ArgenticaDatabase.init(this)
    }
}
