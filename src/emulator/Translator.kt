package emulator

class Translator {
    private val commandCodes = mapOf(
        "LOAD" to "0000",
        "STORE" to "0001",
        "ADD" to "0010",
        "SUB" to "0011",
        "INC" to "0100",
        "DEC" to "0101",
        "CEZ" to "0110",
        "STR1" to "0111",
        "INCR1" to "1000",
        "CLZ" to "1001",
        "CGZ" to "1010",
        "HALT" to "1011",
        "LDR2" to "1100",
        "LDR3" to "1101",
        "DECR3" to "1110",
        "STR3" to "1111"
    )

    private val addressingCode = mapOf(
        "&" to "00",
        "/" to "01",
        "$" to "10"
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

        val address = if (parts.size > 1) parts[1].removePrefix("/").removePrefix("&").removePrefix("$") else null
        val binaryAddress = address?.let {
            if (it in labels) {
                String.format("%10s", labels[it]!!.toString(2)).replace(' ', '0') // Адрес по метке
            } else {
                it.toIntOrNull()?.let { num ->
                    String.format("%10s", num.toString(2)).replace(' ', '0') // Числовой адрес
                } ?: error("Invalid address: $it")
            }
        } ?: "0000000000"

        val binaryCommand = "$commandCode$addressingMode$binaryAddress"
        return binaryCommand.toInt(2)
    }
}