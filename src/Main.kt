import emulator.ProcessEmulator
import java.io.File

fun main() {
    val processor = ProcessEmulator(512, 512)

    val file = File("input.txt")
    val lines = file.readLines()
    
    val data = lines[0].split(" ").map { it.toInt() }.toIntArray()
    processor.initializeData(data)

    val program = lines.drop(1)
    processor.initializeInstructions(program)
    processor.run()
    processor.displayState()
}