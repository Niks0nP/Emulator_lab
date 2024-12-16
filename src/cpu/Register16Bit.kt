package cpu

class Register16Bit {
    private var cell: Int = 0

    fun increment() {
        cell++
    }

    fun decrement() {
        cell--
    }

    fun getValue(): Int {
        return cell
    }

    fun setValue(value: Int) {
        cell = value
    }

    override fun toString(): String {
        return cell.toString()
    }
}