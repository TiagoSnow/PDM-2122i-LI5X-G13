package pt.isel.pdm.chess4android.history

import androidx.room.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class HistoryDataAccess {

    /**
     * The data type that represents data stored in the "history_quote" table of the DB
     */
    @Entity(tableName = "history_puzzle")
    data class PuzzleEntity(
        @PrimaryKey val id: String,     //date
        val name: String,
        val pgn: String,
        val solution: String,
        val status: String,
        val timestamp: Date = Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))
    ) {
        fun isTodayPuzzle(): Boolean =
            timestamp.toInstant().compareTo(Instant.now().truncatedTo(ChronoUnit.DAYS)) == 0
    }



    /**
     * Contains converters used by the ROOM ORM to map between Kotlin types and MySQL types
     */
    class Converters {
        @TypeConverter
        fun fromTimestamp(value: Long) = Date(value)

        @TypeConverter
        fun dateToTimestamp(date: Date) = date.time

    }

    /**
     * The abstraction containing the supported data access operations. The actual implementation is
     * provided by the Room compiler. We can have as many DAOs has our design mandates.
     */
    @Dao
    interface HistoryPuzzleDao {
        @Insert
        fun insert(puzzle: PuzzleEntity)

        @Delete
        fun delete(puzzle: PuzzleEntity)

        @Query("SELECT * FROM history_puzzle ORDER BY id DESC LIMIT 100")
        fun getAll(): List<PuzzleEntity>

        @Query("SELECT * FROM history_puzzle ORDER BY id DESC LIMIT :count")
        fun getLast(count: Int): List<PuzzleEntity>
    }


    /**
     * The abstraction that represents the DB itself. It is also used as a DAO factory: one factory
     * method per DAO.
     */
    @Database(entities = [PuzzleEntity::class], version = 1)
    @TypeConverters(Converters::class)
    abstract class HistoryDatabase: RoomDatabase() {
        abstract fun getHistoryPuzzleDao(): HistoryPuzzleDao
    }
}