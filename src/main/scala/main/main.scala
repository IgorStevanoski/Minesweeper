package main

import control.field.GameField
import control.grid.Grid
import scalafx.application.JFXApp3
import scene.MainScene
import scene.MainScene.{sceneHeightEditor, sceneHeightLeaderBoard, sceneHeightMain, sceneWidthEditor, sceneWidthLeaderBoard, sceneWidthMain}
import util.{Difficulty, Generator}


object MainApp extends JFXApp3 {
//  var grid = new Grid(Generator.generateByDifficulty(Difficulty.Intermediate))
//  def stageWidth(): Int = (grid.coLCnt + 2) * GameField.size
//  def stageHeight(): Int = (grid.rowCnt + 2) * GameField.size + 200

//  def stageWidth(): Int = (Difficulty.currentDifficulty.cols + 2) * GameField.size
//  def stageHeight(): Int = (Difficulty.currentDifficulty.rows + 2) * GameField.size + 200

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title.value = "Minesweeper"
      width =  MainScene.sceneWidthMain
      height = MainScene.sceneHeightMain
      scene = new MainScene
    }
  }

  def stageWidth(): Int = (Difficulty.currentDifficulty.cols + 2) * GameField.size
  def stageHeight(): Int = (Difficulty.currentDifficulty.rows + 2) * GameField.size + 200

  val ConstGame = 0
  val ConstMainMenu = 1
  val ConstEditor =  2
  val ConstLeaderBoard = 3
  def setStageBounds(ind: Int = ConstGame) = {
    val size : (Int, Int) = ind match {
      case 1 => (sceneWidthMain, sceneHeightMain)
      case 2 => (sceneWidthEditor, sceneHeightEditor)
      case 3 => (sceneWidthLeaderBoard, sceneHeightLeaderBoard)
      case _ => (stageWidth(), stageHeight())
    }
    stage.width = size._1
    stage.height = size._2
  }
}
