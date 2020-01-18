package com.mishenka.notbasic.data.model.photo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mishenka.notbasic.data.model.photo.network.SchedResSelectItem

@Dao
interface PhotoDao {

    @Query("""
        SELECT url
        FROM sched_res
        WHERE page = :page
    """)
    suspend fun getScheduledResultsForPage(page: Int): List<SchedResSelectItem>?


    @Query("""
        SELECT MAX(page)
        FROM sched_res
    """)
    suspend fun getLastSchedResPage(): Int?


    @Insert
    suspend fun insertAllScheduledResults(values: List<SchedRes>)


    @Query("""
        DELETE
        FROM sched_res
        WHERE url = :url AND page = :page
    """)
    suspend fun deleteShedResByUrlAndPage(url: String, page: Int)


    @Query("DELETE FROM sched_res")
    suspend fun clearSchedResTable()

}