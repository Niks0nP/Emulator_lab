package emulator

class EmulatorCommand(private val command: Int) {
    companion object {
        private const val COMMAND_MASK = 0b1111_0000_0000_0000
        private const val ADDRESSING_MASK = 0b0000_1100_0000_0000
        private const val ADDRESS_MASK = 0b0000_0011_1111_1111
    }

    fun getCommandCode(): Int {
        return (command and COMMAND_MASK) shr 12
    }

    fun getAddressingMask(): Int {
        return (command and ADDRESSING_MASK) shr 10
    }

    fun getAddress(): Int {
        return if ((command and ADDRESS_MASK) shr 6 > 0) {
            (command and ADDRESS_MASK) shr 6
        } else
            command and ADDRESS_MASK
    }

    override fun toString(): String {
        return "Command code: ${getCommandCode()}, Addressing mode: ${getAddressingMask()}, Address: ${getAddress()}"
    }
}