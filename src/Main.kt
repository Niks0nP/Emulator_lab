import emulator.ProcessEmulator
import instruction.Instruction

fun main() {
    val processor = ProcessEmulator(512, 512)

    val data = intArrayOf(10, 25, 7, 15, 3)
    processor.initializeData(data)

    val instructions = listOf(
        Instruction(0, 1, 0), // Загрузка первого числа массива в аккумулятор
        Instruction(1, 1, 5), // Установка первого числа как максимального в 5 ячейку данных
        Instruction(0, 1, 1), // Загрузка второго числа массива в аккумулятор
        Instruction(3, 1, 5), // Вычитание из аккумулятора максимального числа
        Instruction(6, 1, 1), // Проверка, остаток больше нуля
        Instruction(1, 1, 5), // Устанавливаем новое значение в 5 ячейку данных
        Instruction(8, 0, 0)  // Выходим из выполнения программы
    )

    processor.initializeInstructions(instructions)
    processor.run()
    processor.displayState()
}