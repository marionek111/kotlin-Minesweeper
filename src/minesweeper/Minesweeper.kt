package minesweeper

import java.awt.Point
import kotlin.math.min
import kotlin.random.Random

class Minesweeper(size: Int, mines: Int) {
    private var board = Board(size, mines)

    fun showBoard() = board.show()

    fun markFieldFree(pos: Point) = board.markFieldFree(Point(pos.x - 1, pos.y - 1))

    fun markFieldMine(pos: Point) = board.markFieldMine(Point(pos.x - 1, pos.y - 1))

    fun isEndGame() = board.isAllMinesMarked() || Field.showMines

    fun isVictory() = board.isAllMinesMarked()

    private class Board(
            private val size: Int,
            private val mines: Int
    ) {
        private val board = Array(size) { Array(size) { Field() } }
        private var firstPosition: Point? = null

        fun markFieldMine(pos: Point) = board[pos.y][pos.x].toggleMarkMine()

        fun markFieldFree(pos: Point) {
            if (firstPosition == null) {
                firstPosition = pos
                initBoard()
            }
            val field = board[pos.y][pos.x]
            if (field.isMine) {
                Field.showMines = true
                return
            } else if (!field.isMarkedFree) {
                field.isMarkedFree = true
                if (field.minesAround == 0) {
                    markAllFieldsAroundFree(pos)
                }
            }
        }

        private fun markAllFieldsAroundFree(pos: Point) {
            val yRage = Integer.max(0, pos.y - 1)..min(pos.y + 1, board.lastIndex)
            val xRage = Integer.max(0, pos.x - 1)..min(pos.x + 1, board[pos.y].lastIndex)
            for (y in yRage) {
                for (x in xRage) {
                    val field = board[y][x]
                    if (!field.isMarkedFree) {
                        field.isMarkedFree = true
                        if (field.minesAround == 0) {
                            markAllFieldsAroundFree(Point(x, y))
                        }
                    }
                }
            }
        }

        fun isAllMinesMarked(): Boolean {
            var countMarked = 0
            for (y in board.indices) {
                for (x in board[y].indices) {
                    val field = board[y][x]
                    if (field.isMine xor field.isMarkedMine) {
                        return false
                    } else if (field.isMarkedMine) {
                        countMarked++
                    }
                }
            }
            return countMarked == mines
        }

        fun show() {
            println()
            print(" |")
            repeat(size) { print(it + 1) }
            println("|")
            print("-|")
            repeat(size) { print("-") }
            println("|")
            for (y in board.indices) {
                print("${y + 1}|")
                for (x in board[y].indices) {
                    print(board[y][x])
                }
                println("|")
            }
            print("-|")
            repeat(size) { print("-") }
            println("|")
        }

        private fun initBoard() {
            initMines()
            initNumberOfMines()
        }

        private fun initMines() {
            val fp = firstPosition!!
            val yRage = Integer.max(0, fp.y - 1)..min(fp.y + 1, board.lastIndex)
            val xRage = Integer.max(0, fp.x - 1)..min(fp.x + 1, board[fp.y].lastIndex)

            var filedMines = 0
            while (filedMines < mines) {
                val pos = Point(Random.nextInt(size), Random.nextInt(size))
                val inInvalidRage = pos.y in yRage && pos.x in xRage
                if (!board[pos.y][pos.x].isMine && !inInvalidRage) {
                    board[pos.y][pos.x].isMine = true
                    filedMines++
                }
            }
        }

        private fun initNumberOfMines() {
            for (y in board.indices) {
                for (x in board[y].indices) {
                    board[y][x].minesAround = numberOfMinesAround(Point(x, y))
                }
            }
        }

        private fun numberOfMinesAround(pos: Point): Int {
            val yRage = Integer.max(0, pos.y - 1)..min(pos.y + 1, board.lastIndex)
            val xRage = Integer.max(0, pos.x - 1)..min(pos.x + 1, board[pos.y].lastIndex)
            var mines = 0
            for (y in yRage) {
                for (x in xRage) {
                    if (board[y][x].isMine) {
                        mines++
                    }
                }
            }
            if (board[pos.y][pos.x].isMine) {
                mines--
            }
            return mines
        }
    }

    class Field {
        var isMarkedFree = false
            set(value) {
                field = value
                if (value) {
                    isMarkedMine = false
                }
            }
        var isMarkedMine = false
        var isMine = false
        var minesAround = 0

        companion object {
            var showMines = false
        }

        object Symbol {
            const val unexplored = '.'
            const val free = '/'
            const val mine = 'X'
            const val markedMine = '*'
        }

        fun toggleMarkMine() {
            isMarkedMine = !isMarkedMine
        }

        override fun toString(): String {
            return when {
                isMine && showMines -> Symbol.mine
                !isMarkedFree && isMarkedMine -> Symbol.markedMine
                isMarkedFree && !isMine -> if (minesAround == 0) Symbol.free else minesAround.toNumericChar()
                else -> Symbol.unexplored
            }.toString()
        }
    }
}
