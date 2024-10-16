package util

import scala.collection.mutable.ListBuffer

object Sequencer {
  // (left/right click, row, col)
  val sequence = new ListBuffer[(String, String, String)]()

  def loadSequence(filename: String) = FileReaderWriter.loadSequenceRes(filename)

  def addToSequence(move: (String, String, String)): Unit = sequence.addOne(move)

  def resetSequence() = sequence.clear()
}
