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
        "LDI" to "0111",
        "IINC" to "1000",
        "CLZ" to "1001",
        "CGZ" to "1010",
        "HALT" to "1011",
        "MVS" to "1100",
        "SAS" to "1101",
        "DECA" to "1110",
        "LDS" to "1111"
    )

    private val addressingCode = mapOf(
        "&" to "00",
        "/" to "01",
        "$" to "10"
    )

    fun translateProgram(program: List<String>): List<Int> {
        return program.map { translateCommand(it) }
    }

    private fun translateCommand(commandLine: String): Int {
        val parts = commandLine.split(" ")
        val command = parts[0]
        val addressingMode = if (parts.size > 1) parts[1] else null
        val address = if (parts.size > 2) parts[2] else null

        val commandCode = commandCodes[command] ?: error("Unknown command: $command")
        val addressingCode = addressingMode?.let { addressingCode[it] } ?: "00"
        val binaryAddress = address?.toIntOrNull()?.let {
            String.format("%10s", it.toString(2)).replace(' ', '0')
        } ?: "0000000000"

        val binaryCommand = "$commandCode$addressingCode$binaryAddress"
        return binaryCommand.toInt(2)
    }
}