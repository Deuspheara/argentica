package fr.optivision.argentica.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import fr.optivision.argentica.data.enum.ShootingMode

@Entity(tableName = "Photo")
data class Photo(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "photoId") var id: Long = 0,
    @ColumnInfo(name = "number") var number: Int = 0,
    @ColumnInfo(name = "lensId") var lensId: Long = 0,
    @ColumnInfo(name = "filmId") var filmId: Long = 0,
    @ColumnInfo(name = "sessionId") var sessionId: Long = 0,
    @ColumnInfo(name = "aperture") var aperture: String = "",
    @ColumnInfo(name = "exposureTime") var exposureTime: String = "",
    @ColumnInfo(name = "shootingMode") var shootingMode: String = ShootingMode.MANUAL.name
)