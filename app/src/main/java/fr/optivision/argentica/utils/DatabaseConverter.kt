package fr.optivision.argentica.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import fr.optivision.argentica.ui.activity.main.MainActivity
import java.io.File


object DatabaseConverter {
    //livedata progressbar
    private var progress: MutableLiveData<Int> = MutableLiveData()

    fun getProgress(): LiveData<Int> {
        return progress
    }

    fun setProgress(progress: Int) {
        this.progress.postValue(progress)
    }


    fun backupDatabase(activity: AppCompatActivity?, auth: FirebaseAuth, storage : FirebaseStorage) {
        progress.value = 0
        if(auth.currentUser == null) {
           Toast.makeText(activity, "Vous devez être connecté pour sauvegarder vos données", Toast.LENGTH_SHORT).show()
            return
        }
        if(activity == null) {
            return
        }

        val uri = DBFileProvider().getDatabaseURI(activity, "database.db")
        if (uri != null) {
            val fileRef = storage.reference.child("backup/${auth.currentUser?.uid}/database.db")
            fileRef.putFile(uri).addOnSuccessListener {
                progress.postValue(50)
                val uriSecond = DBFileProvider().getDatabaseURI(activity, "database.db-shm")
                if (uriSecond != null) {
                    val fileRef = storage.reference.child("backup/${auth.currentUser?.uid}/database.db-shm")
                    fileRef.putFile(uriSecond).addOnSuccessListener {
                        progress.postValue(75)
                        val uriThird = DBFileProvider().getDatabaseURI(activity, "database.db-wal")
                        if (uriThird != null) {
                            val fileRef = storage.reference.child("backup/${auth.currentUser?.uid}/database.db-wal")
                            fileRef.putFile(uriThird).addOnSuccessListener {
                                progress.postValue(100)
                                Toast.makeText(activity, "Sauvegarde effectuée", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(activity, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(activity, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(activity, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
            }
        }




    }

    fun changeDatabase(context: Context, auth: FirebaseAuth, storage : FirebaseStorage, onClick: () -> Unit) {
        progress.postValue(0)
        if(auth.currentUser == null) {
            Toast.makeText(context, "Vous devez être connecté pour restaurer vos données", Toast.LENGTH_SHORT).show()
            return
        }
        val fileRef = storage.reference.child("backup/${auth.currentUser?.uid}/database.db")
        val localFile = File.createTempFile("database", "db")
        fileRef.getFile(localFile).addOnSuccessListener {
            progress.postValue(50)
            val uri = DBFileProvider().getDatabasePath(context, "database.db")
            if (uri != null) {
                //replace local bd with the new one
                localFile.copyTo(File(uri), true)

            }

            val fileRefSecond = storage.reference.child("backup/${auth.currentUser?.uid}/database.db-shm")
            val localFileSecond = File.createTempFile("database", "db-shm")
            fileRefSecond.getFile(localFileSecond).addOnSuccessListener {
                progress.postValue(75)
                val uri = DBFileProvider().getDatabasePath(context, "database.db-shm")
                if (uri != null) {
                    //replace local bd with the new one
                    localFileSecond.copyTo(File(uri), true)

                }
                val fileRefThird = storage.reference.child("backup/${auth.currentUser?.uid}/database.db-wal")
                val localFileThird = File.createTempFile("database", "db-wal")
                fileRefThird.getFile(localFileThird).addOnSuccessListener {
                    progress.postValue(100)
                    Toast.makeText(context, "Restauration effectuée", Toast.LENGTH_SHORT).show()
                    val uri = DBFileProvider().getDatabasePath(context, "database.db-wal")
                    if (uri != null) {
                        //replace local bd with the new one
                        localFileThird.copyTo(File(uri), true)

                    }
                    onClick()

                }.addOnFailureListener {
                    Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {
                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
            }


        }.addOnFailureListener {
            Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
        }





    }

}

class DBFileProvider : FileProvider() {

    fun getDatabaseURI(c: Context, dbName: String?): Uri? {
        val file: File = c.getDatabasePath(dbName)
        return getFileUri(c, file)
    }

    private fun getFileUri(context: Context, file: File): Uri? {
        return getUriForFile(context, "fr.optivision.argentica.provider", file)
    }

    fun getDatabasePath(c: Context, dbName: String?): String? {
        val file: File = c.getDatabasePath(dbName)
        return file.absolutePath
    }

}