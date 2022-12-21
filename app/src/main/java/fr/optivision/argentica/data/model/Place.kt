package fr.optivision.argentica.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Place")
data class Place(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "placeId") var id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)