package fr.optivision.argentica.utils

import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.enum.ShootingMode
import fr.optivision.argentica.data.model.*
import fr.optivision.argentica.data.model.association.FilmSessionCrossRef
import fr.optivision.argentica.data.model.association.SessionCategoryCrossRef
import fr.optivision.argentica.utils.base64.Base64Camera
import fr.optivision.argentica.utils.base64.Base64IUT
import fr.optivision.argentica.utils.base64.Base64Lens

object RoomAddData {

    suspend fun addData() {
        val locations = listOf("Bourg-en-Bresse", "Lyon", "Paris")
        val sessions = listOf("IUT Informatique", "Confluence", "La tour Eiffel")
        val schedules = listOf("20 fév. 2022", "15 juin 2021", "16 déc. 2022")
        val descriptions = listOf("Exemple de description 1", "Exemple de description super longue qui décrit la séances photos", "Description 3")
        val photosAperture = listOf("F/1.2", "F/11", "F/2.5")
        val photosIso = listOf("40", "640", "4000")
        val photosExposureTime = listOf("10s", "15s", "30s")
        val photosShootingMode = listOf(ShootingMode.MANUAL, ShootingMode.PROGRAM, ShootingMode.SHUTTER_PRIORITY)
        val cameras = listOf("Canon 4000d", "Panasonic 2000", "Sony X 6")
        val films = listOf("Kodak", "Washi", "Cinestill")
        val lens = listOf("Objectif 50-200mm", "Objectif 75-300m", "Objectif 5-20mm")
        val categories = listOf("Nature", "Paysage", "Animaux", "Homme", "Monument", "Musique")

        val sessionsId = mutableListOf<Long>()
        val categoryId = mutableListOf<Long>()
        val filmsId = mutableListOf<Long>()
        val lensesId = mutableListOf<Long>()

        val unclassified = ArgenticaDatabase.getInstance().sessionDao().insertSession(
            Session(
                title = "Non classé",
                placeId = 0,
                schedule = "",
                description = ""
            )
        )

        for (i in locations.indices) {
            val placeId = ArgenticaDatabase.getInstance().placeDao().insertPlace(
                Place(
                    name = locations[i],
                    latitude = 45.0,
                    longitude = 5.0
                )
            )

            val sessionId = ArgenticaDatabase.getInstance().sessionDao().insertSession(
                Session(
                    title = sessions[i],
                    placeId = placeId,
                    schedule = schedules[i],
                    description = descriptions[i]
                )
            )

            for (k in 0..(0..5).random()) {
                ArgenticaDatabase.getInstance().witnessPhotoDao().insertWitnessPhoto(
                    WitnessPhoto(
                        sessionId = sessionId,
                        uri = Base64IUT.iut
                    )
                )
            }

            sessionsId.add(sessionId)

            val cameraId = ArgenticaDatabase.getInstance().cameraDao().insertCamera(
                Camera(
                    name = "Appareil photo $i",
                    brand = cameras[i],
                    type = "Numérique",
                    buyPrice = (150..800).random().toFloat(),
                    weight = (300..800).random().toFloat(),
                    uri = Base64Camera.camera
                )
            )

            filmsId.add(
                ArgenticaDatabase.getInstance().filmDao().insertFilm(
                    Film(
                        cameraId = cameraId,
                        brand = films[i],
                        name = "Pellicule $i",
                        type = "Argentic",
                        format = "110 35mm",
                        color = "Color",
                        iso = photosIso[i],
                        size = (15..50).random()
                    )
                )
            )

            lensesId.add(
                ArgenticaDatabase.getInstance().lensDao().insertLens(
                    Lens(name = lens[i], uri = Base64Lens.lens)
                )
            )
        }

        for (i in filmsId.indices) {
            for (j in 0..(1..5).random()) {
                val randomSession = sessionsId[sessionsId.indices.random()]

                ArgenticaDatabase.getInstance().photoDao().addPhoto(
                    Photo(
                        number = j + 1,
                        lensId = lensesId[i],
                        filmId = filmsId[i],
                        sessionId = randomSession,
                        aperture = photosAperture[i],
                        exposureTime = photosExposureTime[i],
                        shootingMode = photosShootingMode[i].name
                    )
                )

                ArgenticaDatabase.getInstance().filmSessionCrossRefDao().insertFilmSessionCrossRef(
                    FilmSessionCrossRef(filmsId[i], randomSession)
                )
            }

            for (j in 0..2) {
                ArgenticaDatabase.getInstance().photoDao().addPhoto(
                    Photo(
                        number = j + 6,
                        lensId = lensesId[i],
                        filmId = filmsId[filmsId.indices.random()],
                        sessionId = unclassified,
                        aperture = photosAperture[i],
                        exposureTime = photosExposureTime[i],
                        shootingMode = photosShootingMode[i].name
                    )
                )

                ArgenticaDatabase.getInstance().filmSessionCrossRefDao().insertFilmSessionCrossRef(
                    FilmSessionCrossRef(filmsId[i], unclassified)
                )
            }
        }

        for (i in categories.indices) {
            categoryId.add(
                ArgenticaDatabase.getInstance().categoryDao().insertCategory(
                    Category(name = categories[i])
                )
            )
        }

        for (id in sessionsId) {
            for (z in 0..(1..3).random()) {
                ArgenticaDatabase.getInstance().sessionCategoryCrossRefDao().insertSessionCategoryCrossRef(
                    SessionCategoryCrossRef(id, categoryId[(categories.indices).random()])
                )
            }
        }
    }
}