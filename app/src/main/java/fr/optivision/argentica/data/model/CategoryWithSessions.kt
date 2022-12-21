package fr.optivision.argentica.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import fr.optivision.argentica.data.model.association.SessionCategoryCrossRef

data class CategoryWithSessions(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "sessionId",
        associateBy = Junction(SessionCategoryCrossRef::class)
    )
    val sessions: List<Session>
)
