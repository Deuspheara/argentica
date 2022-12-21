package fr.optivision.argentica.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import fr.optivision.argentica.data.model.association.SessionCategoryCrossRef

data class SessionObjects(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "categoryId",
        associateBy = Junction(SessionCategoryCrossRef::class)
    )
    val categories: List<Category>,
    @Relation(parentColumn = "placeId", entityColumn = "placeId")
    val place: Place?,
    @Relation(parentColumn = "sessionId", entityColumn = "sessionId")
    val witnessPhoto: List<WitnessPhoto>
)
