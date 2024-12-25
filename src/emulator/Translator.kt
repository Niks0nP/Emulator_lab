package emulator

import cpu.Register16Bit

class Translator {
    private val commandCodes = mapOf(
        "LD" to "0000",
        "ST" to "0001",
        "ADD" to "0010",
        "SUB" to "0011",
        "INC" to "0100",
        "DEC" to "0101",
        "CEZ" to "0110",
        "STAC" to "0111",
        "INCR1" to "1000",
        "CLZ" to "1001",
        "CGZ" to "1010",
        "HALT" to "1011",
        "LOAD" to "1100",
        "LDR3" to "1101",
        "DECR3" to "1110",
        "STR" to "1111"
    )

    private val addressingCode = mapOf(
        "&" to "00",
        "/" to "01",
        "$" to "10"
    )
    private val registers = mapOf(
        "R1" to "0001",
        "R2" to "0010",
        "R3" to "0011",
        "ACC" to "0000"
    )

    private val labels = mutableMapOf<String, Int>()
    private val translatorInstructions = mutableListOf<String>()

    fun translateProgram(program: List<String>): List<Int> {
        program.forEachIndexed { index, line ->
            val trimmedLine = line.trim()
            if (trimmedLine.endsWith(":")) {
                val label = trimmedLine.removeSuffix(":")
                labels[label] = translatorInstructions.size
            } else {
                translatorInstructions.add(trimmedLine)
            }
        }
        return translatorInstructions.map { line ->
            translateCommand(line, labels)
        }
    }

    private fun translateCommand(commandLine: String, labels: Map<String, Int>): Int {
        val parts = commandLine.split(" ").filter { it.isNotBlank() }
        val command = parts[0]
        val commandCode = commandCodes[command] ?: error("Unknown command: $command")

        val addressingMode = if (parts.size > 1 && parts[1].startsWith("/")) {
            addressingCode["/"]
        } else if (parts.size > 1 && parts[1].startsWith("&")) {
            addressingCode["&"]
        } else if (parts.size > 1 && parts[1].startsWith("$")) {
            addressingCode["$"]
        } else {
            "00" // По умолчанию прямая адресация
        }

        val argument = if (parts.size > 1) parts[1] else null
        val registerCode = argument?.let { registers[it] }
        val address = argument?.let {
            if (it in labels) {
                labels[it]!!.toString(2).padStart(10, '0') // Метка
            } else if(registerCode != null) {
                "${registerCode.padStart(4, '0')}000000"
            }
            else {
                it.toIntOrNull()?.toString(2)?.padStart(10, '0') // Числовое значение
            }
        } ?: "0000000000"

        val binaryAddress = "$commandCode$addressingMode$address"
        return binaryAddress.toInt(2)
    }
}