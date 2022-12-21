package fr.optivision.argentica.data.model.association

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["sessionId", "categoryId"])
data class SessionCategoryCrossRef(
    val sessionId: Long,
    @ColumnInfo(index = true) val categoryId: Long
)