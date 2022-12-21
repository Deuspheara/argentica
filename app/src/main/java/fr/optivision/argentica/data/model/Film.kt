package fr.optivision.argentica.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Film")
data class Film(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "filmId") val id: Long = 0,
    @ColumnInfo(name = "cameraId") val cameraId: Long,
    @ColumnInfo(name = "brand") val brand: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "format") val format: String,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "iso") val iso: String,
    @ColumnInfo(name = "size") val size: Int
)
