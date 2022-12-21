package fr.optivision.argentica.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import fr.optivision.argentica.data.model.association.FilmSessionCrossRef

data class FilmObjects(
    @Embedded val film: Film,
    @Relation(
        parentColumn = "filmId",
        entityColumn = "sessionId",
        associateBy = Junction(FilmSessionCrossRef::class)
    )
    val sessions: List<Session>,
    @Relation(parentColumn = "filmId", entityColumn = "filmId")
    val photos: List<Photo>
)
