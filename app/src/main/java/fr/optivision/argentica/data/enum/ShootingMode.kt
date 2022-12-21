package fr.optivision.argentica.data.enum

enum class ShootingMode {
    PROGRAM, SHUTTER_PRIORITY, APERTURE_PRIORITY, MANUAL
}

fun convertStringToShootingMode(mode: String): ShootingMode {
    return when (mode) {
        "Manuel" -> ShootingMode.MANUAL
        "Priorité à l'ouverture" -> ShootingMode.APERTURE_PRIORITY
        "Priorité à la vitesse" -> ShootingMode.SHUTTER_PRIORITY
        else -> ShootingMode.PROGRAM
    }
}