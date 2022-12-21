package fr.optivision.argentica.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WitnessPhoto")
data class WitnessPhoto(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "witnessPhotoId") val id: Long = 0,
    @ColumnInfo(name = "sessionId") var sessionId: Long = 0,
    @ColumnInfo(name = "uri") val uri: String
)
