package om.condorserg.materialdesign.db

import androidx.paging.PagingSource
import androidx.room.*
import com.condor.splashbox.data.photo.Photo
import com.condor.splashbox.db.PhotosContract

@Dao
interface PhotosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPhotos(photos: List<Photo>)

    @Query("SELECT * FROM ${PhotosContract.PHOTOS_TABLE_NAME}")
    fun getPhotos(): PagingSource<Int, Photo>

    @Query("DELETE FROM ${PhotosContract.PHOTOS_TABLE_NAME}")
    suspend fun clearAll()

    @Transaction
    suspend fun refresh(photos: List<Photo>) {
        clearAll()
        insertAllPhotos(photos)
    }

}