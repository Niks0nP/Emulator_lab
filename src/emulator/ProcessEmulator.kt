package emulator

import cpu.Memory
import cpu.Register16Bit
import instruction.Instruction

class ProcessEmulator(dataMemorySize: Int, instructionMemorySize: Int) {
    private val dataMemory = Memory(dataMemorySize)
    private val instructionMemory = Memory(instructionMemorySize)
    private val accumulator = Register16Bit()
    private val programCounter = Register16Bit()
    private val flags = mutableMapOf("GZ" to false, "EZ" to false, "LZ" to false)

    fun initializeData(data: IntArray) {
        for (i in data.indices) {
            dataMemory.write(i, data[i])
        }
    }

    fun initializeInstructions(instructions: List<Instruction>) {
        instructions.forEachIndexed { index, instruction ->
            val encoded = encodeInstruction(instruction)
            instructionMemory.write(index, encoded)
        }
    }

    private fun encodeInstruction(instruction: Instruction): Int {
        return (instruction.commandCode shl 12) or (instruction.addressingMode shl 10) or instruction.address
    }

    private fun decodeInstruction(value: Int): Instruction {
        val command = EmulatorCommand(value)
        return Instruction(
            commandCode = command.getCommandCode(),
            addressingMode = command.getAddressingMask(),
            address = command.getAddress()
        )
    }

    private fun updateFlags(value: Int) {
        flags["GZ"] = value > 0
        flags["EZ"] = value == 0
        flags["LZ"] = value < 0
    }

    fun run() {
        while (true) {
            val instructionCode = instructionMemory.read(programCounter.getValue())
            val instruction = decodeInstruction(instructionCode)

            when (instruction.commandCode) {
                0 -> accumulator.setValue(dataMemory.read(instruction.address)) // LOAD
                1 -> dataMemory.write(instruction.address, accumulator.getValue()) // STORE
                2 -> {
                    accumulator.setValue(accumulator.getValue() + dataMemory.read(instruction.address)) // ADD
                    updateFlags(accumulator.getValue())
                }
                3 -> {
                    val result = accumulator.getValue() - dataMemory.read(instruction.address) // SUB
                    updateFlags(result)
                    accumulator.setValue(result)
                }
                4 -> {
                    accumulator.increment() // INC
                    updateFlags(accumulator.getValue())
                }
                5 -> {
                    accumulator.decrement() // DEC
                    updateFlags(accumulator.getValue())
                }
                6 -> {
                    if (flags["GZ"] == true) { // Проверка флага GZ
                        accumulator.setValue(dataMemory.read(instruction.address))
                    } else {
                        flags["GZ"] = false // Сбрасываем флаг
                    }
                }
                7 -> {
                    if (flags["LZ"] == true) { // Проверка флага GZ
                        accumulator.setValue(dataMemory.read(instruction.address))
                    } else {
                        flags["LZ"] = false // Сбрасываем флаг
                    }
                }
                8 -> {
                    println("Program halted.") // HALT
                    return
                }
                else -> throw IllegalArgumentException("Unknown command code: ${instruction.commandCode}")
            }

            programCounter.increment()
        }
    }

    fun displayState() {
        println("Accumulator: $accumulator")
        println("ProgramCounter: $programCounter")
        println("Flags: $flags")
        dataMemory.displayMemory(0, 10)
    }
}

/**
 * Структура команды:
 *  A[0000]_B[00]C[00_0000_0000]
 *  A - биты команд,
 *  B - биты типа аадресации,
 *  C - биты данных.
 *
 * Доступные команды:
 *    LOAD  // Код 0000, Загрузка данных из памяти данных в аккумулятор.
 *    STORE // Код 0001, Сохранение значения из аккумулятора в указанную ячейку памяти данных.
 *    ADD   // Код 0010, Складывает значение из памяти данных и аккумулятор, результат сохраняется в аккумуляторе.
 *    SUB   // Код 0011, Вычитает значение из памяти данных из аккумулятора, результат сохраняется в аккумуляторе.
 *    INC   // Код 0100, Увеличивает значение аккумулятора на 1.
 *    DEC   // Код 0101, Уменьшает значение аккумулятора на 1.
 *    CGZ   // Код 0110, Меняет значение в аккумуляторе на указанное, если флаг GZ == true
 *    CLZ   // Код 0111, Меняет значение в аккумуляторе на указанное, если флаг LZ == true
 *    HALT  // Код 1000, Останавливание выполнение программы
 */