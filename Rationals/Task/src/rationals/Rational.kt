package rationals

import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.math.BigInteger.ZERO

infix fun Long.divBy(denominator: Long) = Rational(this, denominator)
infix fun Int.divBy(denominator: Int) = Rational(this, denominator)
infix fun BigInteger.divBy(denominator: BigInteger) = Rational(this, denominator)

infix operator fun Rational.plus(addendum: Rational): Rational =
    Rational(numerator * addendum.denominator + denominator * addendum.numerator, denominator * addendum.denominator)

infix operator fun Rational.minus(addendum: Rational): Rational = plus(-addendum)

infix operator fun Rational.times(addendum: Rational): Rational =
        Rational(numerator * addendum.numerator, denominator * addendum.denominator)

infix operator fun Rational.div(addendum: Rational): Rational =
        Rational(numerator * addendum.denominator, denominator * addendum.numerator)

operator fun Rational.unaryMinus() : Rational = Rational(-numerator, denominator)

operator fun Rational.compareTo(other: Rational): Int = compareTo(other)

fun String.toRational(): Rational {
    fun String.toBigIntegerOrFail() = toBigIntegerOrNull()
            ?: throw IllegalArgumentException("Expecting rational in the form of 'numerator/denominator' or 'numerator' but was: '${this@toRational}' ")
            
    if (!this.contains("/")) {
        return Rational(toBigIntegerOrFail(), BigInteger.ONE)
    }
    val (numeratorAsString, denominatorAsString) = this.split("/")
    return Rational(numeratorAsString.toBigIntegerOrFail(), denominatorAsString.toBigIntegerOrFail())
}


class Rational(numerator: BigInteger, denominator: BigInteger) : Comparable<Rational>{

    val numerator: BigInteger
    val denominator: BigInteger

    init {
        require(denominator != ZERO) {"Denominator must be non-zero"}
        val gcd = numerator.gcd(denominator)
        val sign = denominator.signum().toBigInteger()
        this.numerator = numerator / gcd * sign
        this.denominator = denominator / gcd * sign
    }

    constructor(numerator: Long, denominator: Long): this(numerator.toBigInteger(), denominator.toBigInteger())
    constructor(numerator: Int, denominator: Int): this(numerator.toBigInteger(), denominator.toBigInteger())

    override fun compareTo(other: Rational): Int = (numerator * other.denominator).compareTo(denominator * other.numerator)

    override fun toString(): String {
        return if (denominator == BigInteger.valueOf(1)) "$numerator"
                else "$numerator/$denominator"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }

}


fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}