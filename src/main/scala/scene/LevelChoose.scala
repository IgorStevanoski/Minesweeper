package scene

import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, ComboBox}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.text.TextAlignment.Center
import scene.MainScene.fontTitle
import util.{Difficulty, FileReaderWriter}

class LevelChoose {
  val root = new BorderPane
  val title = new Text("")
  title.textAlignment = Center
  title.alignmentInParent = Pos.Center
  title.font = fontTitle
  val buttonRandom = new Button("Random level")
  val buttonPlayChosen = new Button("Play chosen level")
  val buttonBack = new Button("Back")

  // editor level combobox
  var levels: Array[String] = Array.empty[String]
  val comboBox: ComboBox[String] = new ComboBox[String](for (l <- levels) yield l) {
    //    levels(0)
  }
  var chosen: String = ""
  comboBox.onAction = _ => {
    chosen = comboBox.value.value
  }
  levels = FileReaderWriter.loadEditorLevels(Difficulty.currentDifficultyName().toLowerCase()).toArray
  comboBox.items = ObservableBuffer(levels: _*)
  chosen = levels(0)
  comboBox.promptText = chosen

//  root.top = title
  root.center = new VBox(10) {
    alignment = Pos.Center
    prefWidth = MainScene.sceneWidthMain
    minWidth = MainScene.sceneWidthMain
    children.addAll(title, buttonRandom, comboBox, buttonPlayChosen, buttonBack)
  }

}
