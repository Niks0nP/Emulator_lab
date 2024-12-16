package cpu

// Класс памяти
class Memory(size: Int) {
    private val memory = IntArray(size)

    fun read(address: Int): Int {
        return memory[address]
    }

    fun write(address: Int, value: Int) {
        memory[address] = value
    }

    fun displayMemory(start: Int, end: Int) {
        println("Memory: ")
        for (i in start..end) {
            println("Address $i: ${memory[i]}")
        }
    }
}