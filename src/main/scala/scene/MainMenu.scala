package scene

import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.text.TextAlignment.Center
import scene.MainScene.fontTitle
import util.FileReaderWriter

class MainMenu {
  val root = new BorderPane
  val title = new Text("Minesweeper")
  title.textAlignment = Center
  title.alignmentInParent = Pos.Center
  title.font = fontTitle
  val buttonNewGame     = new Button("New Game")
  val buttonContinue    = new Button("Continue")
  val buttonEditor      = new Button("Editor")
  val buttonLeaderboard = new Button("Leaderboard")

  root.center = new VBox(10) {
    alignment = Pos.Center
    prefWidth = MainScene.sceneWidthMain
    minWidth = MainScene.sceneWidthMain
    children.addAll(title, buttonNewGame, buttonContinue, buttonEditor, buttonLeaderboard)
  }

}
