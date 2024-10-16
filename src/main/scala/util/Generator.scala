package util

import control.field.{EditorField, GameField}
import util.Difficulty.currentDifficulty

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Difficulty {
  val Beginner = new Difficulty(9, 9, 10)
  val Intermediate = new Difficulty(16, 16, 40)
  val Expert = new Difficulty(16, 30, 99)

  var currentDifficulty: Difficulty = Beginner
  def currentDifficultyName(): String = currentDifficulty match {
    case Intermediate => "Intermediate"
    case Expert => "Expert"
    case _ => "Beginner"
  }

  def difficultyByName(name: String): Difficulty = name.toLowerCase() match {
    case "intermediate" => Intermediate
    case "expert" => Expert
    case _ => Beginner
  }
}

class Difficulty (val rows: Int, val cols: Int, val mineCnt: Int)

object Generator {

  def generateByDifficulty(d: Difficulty): Array[Array[GameField]] = {
    currentDifficulty = d
    generateMinefield(d.rows, d.cols, d.mineCnt )
  }

  // za EditorGrid
  def generateEmpty(d: Difficulty): Array[Array[EditorField]] = {
    currentDifficulty = d
    Array.fill(d.rows, d.cols)(new EditorField)
  }

  def generateMinefield(rows: Int, cols: Int, mineCnt: Int): Array[Array[GameField]] = {
    val mineCount = if (mineCnt > rows * cols) rows * cols else mineCnt

    val random = new Random()
    val list = new ListBuffer[(Int, Int)]
    for (i <- 0 until rows; j <- 0 until cols){
      list.addOne((i,j))
    }

    val matrix : Array[Array[Boolean]] = Array.fill(rows, cols)(false)

    for (_ <- 0 until mineCount) {
      val (r, c) = list.remove(random.nextInt(list.length))
      matrix(r)(c) = true
    }


    def transform: Boolean => GameField = (x: Boolean) => new GameField(x)
    val matrixFields: Array[Array[GameField]] = matrix.map(row => row.map(transform))

    matrixFields
  }

}
