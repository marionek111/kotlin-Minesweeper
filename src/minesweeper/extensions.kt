package minesweeper

fun Int.toNumericChar(): Char = (this % 10 + '0'.toInt()).toChar()
