package util

import control.counter.{ClickCounter, HintCounter}
import control.field.{EditorField, GameField}
import control.Timer
import control.grid.Grid
import scene.GameScene

import java.io.{File, PrintWriter}
import scala.Console.print
import scala.collection.mutable.ListBuffer
import scala.io.Source

object FileReaderWriter {

  private val editor_dir = "src/main/resources/editor"
  private val files_dir = "src/main/resources/files"

  /** Citanje fajla sa imenom filename iz foldera resources/files. */
  def readfRes(filename: String): String = {
    readf( files_dir + "/" + filename)
  }

  /** Citanje fajla sa imenom filename iz foldera resources/editor. */
  def readfResEditor(filename: String): String = {
    readf( editor_dir + "/" + Difficulty.currentDifficultyName().toLowerCase() + "/" + filename)
  }

  def readf(filename: String): String = {
    val matrix = new StringBuilder()
    try {
      val source = Source.fromFile(filename)
      for (line <- source.getLines()) {
        if (line(0) != '!'){
          matrix.append(line)
          matrix.append("\n")
        } else {
          val data = line.split(",")
          Timer.setTime(data(0).substring(1).toInt)
          ClickCounter.setCnt(data(1).toInt)
          HintCounter.setCnt(data(2).toInt)
          GameScene.gameLoaded = true
        }
      }
      source.close()
    } catch {
      case e: Exception => println(s"An error occurred: ${e.getMessage}")
    }

    // mora substring do lenght =- 1 da se ne bi citao poslednji \n
    matrix.toString().substring(0, matrix.toString().length - 1)
  }

  /** Za cuvanje nivoa u editoru.*/
  def saveLevelRes(filename: String, matrix: Array[Array[EditorField]], difficulty: String): Unit = {
    saveLevel("src/main/resources/editor/" + difficulty + "/" + filename, matrix)
  }

  def saveLevel(filename: String, matrix: Array[Array[EditorField]]): Unit = {
    val lines = new StringBuilder("")
    val matrixTrans = matrix.transpose
    for ((arr, ind) <- matrixTrans.zipWithIndex) {
      for (elem <- arr) {
        lines.append(if (elem.mine) "#" else "-")
      }
      if (ind < matrixTrans.length - 1) lines.append("\n")
    }

    new PrintWriter(filename) {
      write(lines.toString()); close
    }
  }

  def saveGameRes(filename: String, matrix: Array[Array[GameField]]): Unit = {
    saveGame("src/main/resources/files/" + filename, matrix)
  }

  // Za cuvanje trenutnog stanja igre
  def saveGame(filename: String, matrix: Array[Array[GameField]]): Unit = {
    val lines = new StringBuilder("")
    for ((arr, ind) <- matrix.zipWithIndex) {
      for (elem <- arr) {

        (elem.hasMine, elem.clicked, elem.flagged) match {
          case (false, true, _) => lines.append(Grid.charClicked)
          case (true, _, false) => lines.append(Grid.charMine)
          case (true, _, true) => lines.append(Grid.charMineFlagged)
          case (false, false, true) => lines.append(Grid.charEmptyFlagged)
          case (false, false, false) => lines.append(Grid.charEmpty)
          case _ => lines.append(Grid.charEmpty)
        }
      }
//      if (ind < matrix.length - 1) lines.append("\n")
      lines.append("\n")
    }

    // poslendji red pocinje sa !
    lines.append("!" + Timer.time + "," + ClickCounter.count + "," + HintCounter.count)

    new PrintWriter(filename) {
      write(lines.toString());
      close
    }
  }

  def readfLeaderboard(filename: String): Array[Array[String]] = {
    val leaderboard = ListBuffer[Array[String]]()
    try {
      val source = Source.fromFile(filename)
      if (source.isEmpty) return leaderboard.toArray
      for (line <- source.getLines()) {
        leaderboard.addOne(line.split(","))
      }
      source.close()
    } catch {
      case e: Exception => println(s"Error: ${e.getMessage}")
    }
    leaderboard.toArray
  }

  def insertIntoLeaderboard(filename: String, data: Array[String]): Unit = {
    val leaderboard = ListBuffer[Array[String]]()
    leaderboard.addAll(readfLeaderboard(filename))
    leaderboard.addOne(data)
    updateLeaderboard(filename, leaderboard.toArray)
  }

  def updateLeaderboard(filename: String, leaderboard: Array[Array[String]]): Unit = {
    val lines = new StringBuilder("")
    for ((data, ind) <- leaderboard.zipWithIndex) {
      lines.append(
        data(0) + "," +
        data(1) + "," +
        data(2) + "," +
        data(3) + "," +
        data(4) + "," +
        data(5)
      )
      if (ind < leaderboard.length - 1) lines.append("\n")
    }

    new PrintWriter(filename) {
      write(lines.toString());
      close
    }
  }

  def loadSequenceRes(filename: String): Unit = {
    loadSequence("src/main/resources/files/" + filename)
  }

  def loadSequence(filename: String): Unit = {
    try {
      Sequencer.resetSequence()

      val source = Source.fromFile(filename)
      for (line <- source.getLines()) {
        // line: L(x,123)
        val click = line(0)
        val arr = line.split(",")
        val row = arr(0).substring(2) //preskacu se tip poteza i (
        val col = arr(1).substring(0, arr(1).length - 1)
        Sequencer.addToSequence((click.toString, row, col))
      }
      source.close()
    } catch {
      case e: Exception => println(s"An error occurred: ${e.getMessage}")
    }
  }

  def loadEditorLevels(difficulty: String): List[String] = {
    val file = new File(editor_dir + "/" + difficulty)
    file.listFiles.filter(_.isFile).map(_.getName).toList
  }
}
