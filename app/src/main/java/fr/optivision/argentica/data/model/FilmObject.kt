package fr.optivision.argentica.data.model

import fr.optivision.argentica.data.enum.AdapterType

data class FilmObject(
    val adapterType: AdapterType? = null,
    val session: Session? = null,
    val photo: Photo? = null,
    var isVisible: Boolean = false
)

