package instruction

data class Instruction(
    val commandCode: Int,
    val addressingMode: Int,
    val address: Int
)
