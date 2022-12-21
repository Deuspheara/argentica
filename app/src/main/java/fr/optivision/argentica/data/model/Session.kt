package fr.optivision.argentica.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Session")
data class Session(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "sessionId") val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "placeId") val placeId: Long,
    @ColumnInfo(name = "schedule") val schedule: String,
    @ColumnInfo(name = "description") val description: String
)