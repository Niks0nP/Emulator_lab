package emulator

import cpu.Memory
import cpu.Register16Bit
import instruction.Instruction

class ProcessEmulator(dataMemorySize: Int, instructionMemorySize: Int) {
    private val dataMemory = Memory(dataMemorySize)
    private val instructionMemory = Memory(instructionMemorySize)
    private val accumulator = Register16Bit()
    private val programCounter = Register16Bit()
    private val indicatorRegister = Register16Bit()
    private val flags = mutableMapOf("GZ" to false, "EZ" to false, "LZ" to false)

    fun initializeData(data: IntArray) {
        dataMemory.write(0, data.size)
        for (i in data.indices) {
            dataMemory.write(i + 1, data[i])
        }
    }

    fun initializeInstructions(program: List<String>) {
        val translator = Translator()
        val instructions = translator.translateProgram(program)

        instructions.forEachIndexed { index, instruction ->
            instructionMemory.write(index, instruction)
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
                0 -> {
                    accumulator.setValue(dataMemory.read(instruction.address)) // LOAD
                    updateFlags(dataMemory.read(instruction.address))
                }
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
                    if (flags["EZ"] == true) { // Проверка флага EZ
                        programCounter.setValue(instruction.address - 1)
                    }
                }
                7 -> {
                    accumulator.setValue(dataMemory.read(dataMemory.read(100)))// LOOP
                }
                8 -> {
                    if (flags["LZ"] == true) {
                        programCounter.setValue(instruction.address - 1)
                    }
                }
                9 -> {
                    if (flags["GZ"] == true) {
                        programCounter.setValue(instruction.address - 1)
                    }
                }
                10 -> {
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
        dataMemory.displayMemory(0, 20)
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