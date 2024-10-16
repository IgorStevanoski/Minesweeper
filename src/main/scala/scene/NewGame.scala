package scene

import main.MainApp
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.text.TextAlignment.Center
import scene.MainScene.fontTitle

class NewGame {
  val root = new BorderPane
  val title = new Text("Choose Difficulty")
  title.textAlignment = Center
  title.alignmentInParent = Pos.Center
  title.font = fontTitle
  val buttonBeginner     = new Button("Beginner")
  val buttonIntermediate    = new Button("Intermediate")
  val buttonExpert      = new Button("Expert")
  val buttonBack = new Button("Back")

  root.center = new VBox(10) {
    alignment = Pos.Center
    prefWidth = MainScene.sceneWidthMain
    minWidth = MainScene.sceneWidthMain
    children.addAll(title, buttonBeginner, buttonIntermediate, buttonExpert, buttonBack)
  }

}
