package minesweeper

import java.awt.Point
import java.lang.Integer.max
import java.util.*
import kotlin.math.min
import kotlin.random.Random

fun main() {
    val scanner = Scanner(System.`in`)

    print("How many mines do you want on the field? > ")
    val mines = scanner.nextInt()

    val sapper = Minesweeper(9, mines)
    sapper.showBoard()
    while (!sapper.isEndGame()) {
        print("Set/unset mines marks or claim a cell as free: > ")
        val pos = Point(scanner.nextInt(), scanner.nextInt())
        when (scanner.next()) {
            "free" -> sapper.markFieldFree(pos)
            "mine" -> sapper.markFieldMine(pos)
            else -> println("Unsupported operation")
        }
        sapper.showBoard()
    }
    if (sapper.isVictory()) {
        println("Congratulations! You found all the mines!")
    } else {
        println("You stepped on a mine and failed!")
    }
}
