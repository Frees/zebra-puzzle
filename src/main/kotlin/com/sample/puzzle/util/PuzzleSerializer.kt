package com.sample.puzzle.util

import com.sample.puzzle.model.PuzzleInput
import com.sample.puzzle.model.PuzzleResult
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PuzzleSerializer {

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    fun readPuzzleInput(inputStream: InputStream): PuzzleInput {
        val jsonText = inputStream.bufferedReader().readText()
        return readPuzzleInput(jsonText)
    }

    fun readPuzzleInput(jsonText: String): PuzzleInput {
        return json.decodeFromString<PuzzleInput>(jsonText)
    }

    fun writePuzzleResult(result: PuzzleResult, outputStream: OutputStream) {
        val jsonText = writePuzzleResult(result)
        outputStream.bufferedWriter().use { it.write(jsonText) }
    }

    fun writePuzzleResult(result: PuzzleResult): String {
        return json.encodeToString(result)
    }
}