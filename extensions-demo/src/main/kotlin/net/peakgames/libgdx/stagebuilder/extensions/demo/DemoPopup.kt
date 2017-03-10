package net.peakgames.libgdx.stagebuilder.extensions.demo

import net.peakgames.libgdx.stagebuilder.core.model.BaseModel
import net.peakgames.libgdx.stagebuilder.core.model.GroupModel
import net.peakgames.libgdx.stagebuilder.extensions.*

class DemoPopup {
    fun createPopup(name: String, closeAction: () -> Unit): GroupModel {
        return GroupModel().build {
            this.name = name
            position(200, 100)
            size(250, 350)
            origin(0.5f, 0.5f)

            image {
                source("UI.atlas", "grey_panel"); size(250, 350); np(5)
            }
            group {
                position(235, 340)
                button {
                    source("UI.atlas", "red_circle", "grey_circle")
                    onClick { closeAction() }
                }
                image {
                    touchable = BaseModel.Touchable.DISABLED
                    position(6, 6)
                    source("UI.atlas", "grey_crossWhite")
                }
            }
            group {
                position(10, 300)
                label {
                    size(150, 30)
                    fontName = "future.fnt"
                    text = "Name"
                    color = "929292"
                }
                textfield {
                    position(80, 0)
                    size(150, 30)
                    atlasName = "UI.atlas"
                    cursorImageName = "grey_box"
                    selectionImageName = "grey_box"
                    fontName = "future_thin.fnt"
                    backgroundImageName = "yellow_panel"
                    fontColor = "dfdfdf"
                    cursorOffset = 1
                    selectionOffset = 1
                    backgroundOffset = 5
                    setNinePatch(5)
                }
            }

            group {
                position(30, 200)
                checkbox {
                    size(50, 50)
                    position(10, 10)
                    text = "COOL?"
                    atlasName = "UI.atlas"
                    frameCheckboxOn = "blue_boxCheckmark"
                    frameCheckboxOff = "blue_boxCross"
                    fontName = "future.fnt"
                    fontColor = "929292"
                    fontScale = 1f
                }
            }

            group {
                position(35, 20)
                button {
                    size(40, 40)
                    source("UI.atlas", "red_button08", "red_button09")
                    onClick {
                        println("Red button pressed")
                    }
                }
                image {
                    position(10, 10)
                    size(20, 20)
                    source("UI.atlas", "grey_crossWhite")
                }
                textbutton {
                    text = "ACCEPT"
                    atlasName = "UI.atlas"
                    position(50, 0)
                    size(120, 40)
                    frameUp = "green_button08"
                    frameDown = "green_button09"
                    setNinePatch(5)
                    fontName = "future.fnt"
                    fontScale = 1f
                    onClick {
                        println("Accepted!")
                    }
                }
            }
        }
    }
}