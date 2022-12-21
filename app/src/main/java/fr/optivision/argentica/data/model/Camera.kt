package fr.optivision.argentica.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Camera")
data class Camera(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "cameraId") val id: Long = 0,
    @ColumnInfo(name = "brand") val brand: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "buyPrice") val buyPrice: Float,
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "uri") val uri: String
)