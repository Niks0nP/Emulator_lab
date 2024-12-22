import emulator.ProcessEmulator

fun main() {
    val processor = ProcessEmulator(512, 512)

    val data = intArrayOf(10, 25, 7, 15, 3, 34, 22)
    processor.initializeData(data)

    // 10 ячейка сохранение максимума,
    // 12 ячейка сохранение размера массива
    // 15 ячейка указатель на элемент

    val program = listOf(
        "LOAD / 100",
        "INC",
        "STORE / 100",
        "LOAD / 1",
        "STORE / 20",
        "LOAD / 0",
        "DEC",
        "STORE / 0",
        "CEZ / 20",

        "LOAD / 100",
        "INC",
        "STORE / 100",

        "LOOP",
        "SUB / 20",
        "CLZ / 5",
        "LOAD / 0",
        "CEZ / 20",
        "LOOP",
        "STORE / 20",
        "CGZ / 5",
        "HALT / 0"
    )
    processor.initializeInstructions(program)
    processor.run()
    processor.displayState()
}

//    val instructions = listOf(
//        Instruction(0, 1, 0), // Загрузка первого числа массива в аккумулятор
//        Instruction(1, 1, 5), // Установка первого числа как максимального в 5 ячейку данных
//        Instruction(0, 1, 1), // Загрузка второго числа массива в аккумулятор
//        Instruction(3, 1, 5), // Вычитание из аккумулятора максимального числа
//        Instruction(6, 1, 1), // Проверка, остаток больше нуля
//        Instruction(1, 1, 5), // Устанавливаем новое значение в 5 ячейку данных
//        Instruction(8, 0, 0)  // Выходим из выполнения программы
//    )
//