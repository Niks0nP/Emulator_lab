package emulator

import cpu.Memory
import cpu.Register16Bit
import instruction.Instruction

class ProcessEmulator(dataMemorySize: Int, instructionMemorySize: Int) {
    private val dataMemory = Memory(dataMemorySize)
    private val instructionMemory = Memory(instructionMemorySize)
    private val accumulator = Register16Bit()
    private val programCounter = Register16Bit()
    private val R1 = Register16Bit() // регистр указателя
    private val R2 = Register16Bit() // регистр максимального элемента
    private val R3 = Register16Bit() // регистр размера массива
    private val flags = mutableMapOf("GZ" to false, "EZ" to false, "LZ" to false)

    fun initializeData(data: IntArray) {
        for (i in data.indices) {
            dataMemory.write(i, data[i])
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
                0 -> { // LD Rx
                    val register = getRegisterByCode(instruction.address)
                    accumulator.setValue(register.getValue()) // LDS STR3
                    updateFlags(register.getValue())
                }
                1 -> { // ST Rx
                    val register = getRegisterByCode(instruction.address)
                    register.setValue(dataMemory.read(R1.getValue()))
                    updateFlags(register.getValue())
                }
                2 -> { // ADD Rx
                    val register = getRegisterByCode(instruction.address)
                    accumulator.setValue(accumulator.getValue() + register.getValue())
                    updateFlags(accumulator.getValue())
                }
                3 -> { // SUB Rx
                    val register = getRegisterByCode(instruction.address)
                    val result = accumulator.getValue() - register.getValue()
                    updateFlags(result)
                    accumulator.setValue(result)
                }
                4 -> { // INC Rx
                    val register = getRegisterByCode(instruction.address)
                    register.increment()
                    updateFlags(register.getValue())
                }
                5 -> { // DEC Rx
                    val register = getRegisterByCode(instruction.address)
                    register.decrement()
                    updateFlags(register.getValue())
                }
                6 -> {
                    if (flags["EZ"] == true) { // CEZ
                        programCounter.setValue(instruction.address)
                        continue
                    }
                }
                7 -> {
//                    val register = getRegisterByCode(instruction.address)
//                    accumulator.setValue(dataMemory.read(register.getValue()))// LDI STAC R1
//                    updateFlags(accumulator.getValue())
                }
                8 -> {
//                    R1.increment() // INCR1
                }
                9 -> {
                    if (flags["LZ"] == true) { // CLZ
                        programCounter.setValue(instruction.address)
                        continue
                    }
                }
                10 -> {
                    if (flags["GZ"] == true) { // CGZ
                        programCounter.setValue(instruction.address)
                        continue
                    }
                }
                11 -> {
                    println("Program halted.") // HALT
                    return
                }
                12 -> {
//                    val register = getRegisterByCode(instruction.address)
//                    register.setValue(dataMemory.read(R1.getValue())) // LOADR2
                }
                13 -> {
//                    R3.setValue(dataMemory.read(instruction.address)) // LDR3
                }
                14 -> {
//                    R3.decrement() //DECR3
//                    updateFlags(R3.getValue())
                }
                15 -> {
//                    val register = getRegisterByCode(instruction.address)
//                    accumulator.setValue(register.getValue()) // LDS STR3
//                    updateFlags(register.getValue())
                }
                else -> throw IllegalArgumentException("Unknown command code: ${instruction.commandCode}")
            }

            programCounter.increment()
        }
    }

    fun displayState() {
        println("Accumulator: $accumulator")
        println("ProgramCounter: $programCounter")
        println("MaxValue: $R2")
        println("Flags: $flags")
        dataMemory.displayMemory(0, dataMemory.read(0))
    }

    private fun getRegisterByCode(code: Int): Register16Bit {
        return when (code) {
            0 -> accumulator
            1 -> R1
            2 -> R2
            3 -> R3
            else -> throw IllegalArgumentException("Invalid register code: $code")
        }
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