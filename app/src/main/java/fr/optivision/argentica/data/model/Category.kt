package fr.optivision.argentica.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class Category(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "categoryId") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String
)