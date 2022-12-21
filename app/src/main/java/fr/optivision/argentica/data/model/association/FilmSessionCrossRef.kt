package fr.optivision.argentica.data.model.association

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["filmId", "sessionId"])
data class FilmSessionCrossRef(
    val filmId: Long,
    @ColumnInfo(index = true) val sessionId: Long,
)