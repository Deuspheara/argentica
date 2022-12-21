package fr.optivision.argentica.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Lens")
data class Lens(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "lensId") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "uri") val uri: String
)
