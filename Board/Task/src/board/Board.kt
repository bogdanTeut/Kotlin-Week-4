package board

fun <T>Array<T>.getOrNull(index: Int): T? {
   return if (index in 0..size-1) get(index) else null
}

data class Cell(val i: Int, val j: Int) {
    override fun toString()= "($i, $j)"
}

enum class Direction {
    UP, DOWN, RIGHT, LEFT;

    fun reversed() = when (this) {
        UP -> DOWN
        DOWN -> UP
        RIGHT -> LEFT
        LEFT -> RIGHT
    }
}

interface SquareBoard {
    val width: Int

    fun getCellOrNull(i: Int, j: Int): Cell?
    fun getCell(i: Int, j: Int): Cell

    fun getAllCells(): Collection<Cell>

    fun getRow(i: Int, jRange: IntProgression): List<Cell>
    fun getColumn(iRange: IntProgression, j: Int): List<Cell>

    fun Cell.getNeighbour(direction: Direction): Cell?
}

class SquareBoardImpl(override val width: Int) : SquareBoard {

    val matrix: Array<Array<Cell>>

    init {
        matrix = Array(width) { i ->
            Array(width) { j ->
                Cell(i + 1, j + 1)
            }
        }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? = matrix.getOrNull(i-1)?.let { column -> column.getOrNull(j-1)}

    override fun getCell(i: Int, j: Int): Cell = getCellOrNull(i, j) ?: throw IllegalArgumentException("Incorrect values for i and j")

    override fun getAllCells(): Collection<Cell> {
        val cellCollection = mutableListOf<Cell>()
        for (i in 0 until width) {
            for (j in 0 until width) {
                cellCollection.add(matrix[i][j])
            }
        }
        return cellCollection
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val cellCollection = mutableListOf<Cell>()

        for (j in jRange.takeWhile { it <= width }) {
            cellCollection.add(matrix[i-1][j-1])
        }

        return cellCollection
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val cellCollection = mutableListOf<Cell>()

        for (i in iRange.takeWhile { it <= width }) {
            cellCollection.add(matrix[i-1][j-1])
        }

        return cellCollection
    }

    fun findNeighbour(currentCell: Cell, direction: Direction): Cell? = when (direction) {
        Direction.UP -> matrix.getOrNull(currentCell.i-2)?.let {column -> column[currentCell.j-1]}
        Direction.DOWN -> matrix.getOrNull(currentCell.i)?.let {column -> column[currentCell.j-1]}
        Direction.LEFT -> matrix.getOrNull(currentCell.i-1)!!.getOrNull(currentCell.j-2)
        Direction.RIGHT -> matrix.getOrNull(currentCell.i-1)!!.getOrNull(currentCell.j)
    }


    override fun Cell.getNeighbour(direction: Direction): Cell? = findNeighbour(this, direction)

}

interface GameBoard<T> : SquareBoard {

    operator fun get(cell: Cell): T?
    operator fun set(cell: Cell, value: T?)

    fun filter(predicate: (T?) -> Boolean): Collection<Cell>
    fun find(predicate: (T?) -> Boolean): Cell?
    fun any(predicate: (T?) -> Boolean): Boolean
    fun all(predicate: (T?) -> Boolean): Boolean
}

class GameBoardImpl<T>(override val width: Int) : GameBoard<T> {
    private val squareBoard: SquareBoardImpl = SquareBoardImpl(width)
    private val valuesMap: MutableMap<Cell, T?> = mutableMapOf()

    init {
        for (i in 1..width) {
            for (j in 1..width) {
                valuesMap[squareBoard.matrix[i-1][j-1]] = null
            }
        }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? = squareBoard.getCellOrNull(i, j)

    override fun getCell(i: Int, j: Int): Cell = squareBoard.getCell(i, j)

    override fun getAllCells(): Collection<Cell> = squareBoard.getAllCells()

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> = squareBoard.getRow(i, jRange)

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> = squareBoard.getColumn(iRange, j)

    override fun Cell.getNeighbour(direction: Direction): Cell? = squareBoard.findNeighbour(this, direction)

    override fun get(cell: Cell): T? = valuesMap[cell]

    override fun set(cell: Cell, value: T?) {
        valuesMap[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> = valuesMap
            .filterValues { value -> value?.let { predicate(it) } ?: false  }
            .keys

    override fun find(predicate: (T?) -> Boolean): Cell? = valuesMap.filterValues { predicate(it) }.keys.first()

    override fun any(predicate: (T?) -> Boolean): Boolean = valuesMap.any { entry -> predicate(entry.value) }

    override fun all(predicate: (T?) -> Boolean): Boolean = valuesMap.all { entry -> predicate(entry.value) }
}