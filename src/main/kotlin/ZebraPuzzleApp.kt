package com.sample

import com.sample.puzzle.solving.PuzzleSolver
import com.sample.puzzle.util.PuzzleSerializer
import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar zebra-puzzle.jar <input-file> [<output-file>]")
        return
    }

    val inputFile = args[0]
    val output = args.getOrNull(1)?.let { File(it).outputStream() } ?: System.out

    val puzzleInput = PuzzleSerializer.readPuzzleInput(File(inputFile).readText())

    val solver = PuzzleSolver(puzzleInput)
    val result = solver.solve()

    PuzzleSerializer.writePuzzleResult(result, output)
}
