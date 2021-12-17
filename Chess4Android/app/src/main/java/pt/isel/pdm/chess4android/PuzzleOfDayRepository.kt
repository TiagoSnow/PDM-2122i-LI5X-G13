package pt.isel.pdm.chess4android

import android.content.Context
import android.util.Log
import pt.isel.pdm.chess4android.history.HistoryDataAccess.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


private fun PuzzleEntity.toPuzzleInfoDTO() = PuzzleInfoDTO(
    game = Game(pgn),
    puzzle = Puzzle(name, convertStringToArray(solution)),
    date = id,
    status = status
)

class PuzzleOfDayRepository(
    private val puzzleOfDayService: DailyPuzzleService,
    private val historyPuzzleDao: HistoryPuzzleDao
) {

    fun asyncDelete(callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback, asyncAction = {
            historyPuzzleDao.delete(
                PuzzleEntity(
                    id = "2021-12-11",
                    name = "2q3bF",
                    pgn = "e4 c5 f4 d6 Nf3 Nf6 e5 dxe5 fxe5 Ng4 Bb5+ Bd7 Bc4 Nc6 e6 Bxe6 Bxe6 fxe6 O-O Qd6 Nc3 O-O-O Ne4 Qc7 Nxc5 Nd4 Nxe6 Nxf3+ Rxf3 Qxh2+ Kf1 Rd6",
                    solution = "[f3c3,c8b8,d1g4]",
                    status = "Não Resolvido"
                )
            )
        })
    }

    //TO TEST
    fun asyncGetAll(callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback, asyncAction = {
            val list = historyPuzzleDao.getAll()
            for (puzzle in list) {
                Log.v(APP_TAG, puzzle.id + ": " + puzzle.status)
            }
        })
    }

    fun asyncUpdate(puzzleInfoDTO: PuzzleInfoDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback, asyncAction = {
            val puzzleEntity = historyPuzzleDao.getById(puzzleInfoDTO.date)
            historyPuzzleDao.delete(puzzleEntity)
            puzzleEntity.status = "Resolvido"
            historyPuzzleDao.insert(puzzleEntity)
        })
    }

    /**
     * Asynchronously gets the puzzle of day, either from the local DB, if available, or from
     * the remote API.
     *
     * @param mustSaveToDB  indicates if the operation is only considered successful if all its
     * steps, including saving to the local DB, succeed. If false, the operation is considered
     * successful regardless of the success of saving the puzzle in the local DB (the last step).
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD
     *
     * Using a boolean to distinguish between both options is a questionable design decision.
     */
    fun fetchPuzzleOfDay(mustSaveToDB: Boolean = false, callback: (Result<PuzzleInfoDTO>) -> Unit) {
        asyncMaybeGetPuzzleOfDayFromDB {
            it.onSuccess { entity ->
                if (entity?.isTodayPuzzle() == true) {
                    Log.v(
                        pt.isel.pdm.chess4android.history.APP_TAG,
                        "Thread ${Thread.currentThread().name}: Getting today puzzle from local DB"
                    )
                    callback(Result.success(entity.toPuzzleInfoDTO()))
                } else {
                    asyncGetPuzzleOfDayFromAPI { apiResult ->
                        Log.v(
                            pt.isel.pdm.chess4android.history.APP_TAG,
                            "Thread ${Thread.currentThread().name}: Getting today puzzle from API"
                        )
                        apiResult.onSuccess { puzzleInfoDTO ->
                            asyncSaveToDB(puzzleInfoDTO) { saveToDBResult ->
                                Log.v(
                                    pt.isel.pdm.chess4android.history.APP_TAG,
                                    "Thread ${Thread.currentThread().name}: Trying to save into DB"
                                )
                                saveToDBResult.onSuccess { callback(Result.success(puzzleInfoDTO)) }
                                saveToDBResult.onFailure {
                                    Log.v(
                                        pt.isel.pdm.chess4android.history.APP_TAG,
                                        "Thread ${Thread.currentThread().name}: Failure to save into DB"
                                    )
                                    callback(
                                        if (mustSaveToDB) Result.failure(it) else Result.success(
                                            puzzleInfoDTO
                                        )
                                    )
                                }
                            }
                        }
                        apiResult.onFailure {
                            callback(apiResult)
                        }

                    }
                }
            }
        }
    }

    /**
     * Asynchronously saves the puzzle of the day to the local DB.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncSaveToDB(dto: PuzzleInfoDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            historyPuzzleDao.insert(
                PuzzleEntity(
                    id = dto.date,
                    name = dto.puzzle.id,
                    pgn = dto.game.pgn,
                    solution = convertArrayToString(dto.puzzle.solution),
                    status = dto.status
                )
            )
        }
    }

    /**
     * Asynchronously gets the puzzle of the day from the remote API.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncGetPuzzleOfDayFromAPI(callback: (Result<PuzzleInfoDTO>) -> Unit) {
        puzzleOfDayService.getPuzzle()
            .enqueue(object : Callback<PuzzleInfo> {
                override fun onResponse(
                    call: Call<PuzzleInfo>,
                    response: Response<PuzzleInfo>
                ) {
                    val puzzle = response.body()
                    val result: Result<PuzzleInfoDTO> =
                        if (puzzle != null && response.isSuccessful) {
                            val puzzleInfoDTO = PuzzleInfoDTO(
                                game = puzzle.game,
                                puzzle = puzzle.puzzle,
                                date = LocalDate.now().toString(),
                                status = "Não Resolvido"

                            )
                            Result.success(puzzleInfoDTO)
                        } else {
                            Result.failure(ServiceUnavailable())
                        }
                    callback(result)
                }

                override fun onFailure(call: Call<PuzzleInfo>, t: Throwable) {
                    Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: onFailure ")
                    callback(Result.failure(ServiceUnavailable(cause = t)))
                }
            })
    }

    /**
     * Asynchronously gets the puzzle of the day from the local DB, if available.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncMaybeGetPuzzleOfDayFromDB(callback: (Result<PuzzleEntity?>) -> Unit) {
        callbackAfterAsync(callback, asyncAction = {
            historyPuzzleDao.getLast(1).firstOrNull()
        })
    }

}