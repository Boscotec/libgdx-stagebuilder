package net.peakgames.libgdx.stagebuilder.extensions.demo

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import net.peakgames.libgdx.stagebuilder.core.AbstractGame
import net.peakgames.libgdx.stagebuilder.core.AbstractScreen
import net.peakgames.libgdx.stagebuilder.core.model.GroupModel
import net.peakgames.libgdx.stagebuilder.extensions.*

class DemoScreen(game: AbstractGame) : AbstractScreen(game) {

    override fun unloadAssets() {}

    override fun show() {
        super.show()
        val groupModel = GroupModel()
        groupModel.build {
            name = "main_group"
            image {
                source("UI.atlas", "yellow_panel")
                np(5)
                type = "background"
            }
            textbutton {
                text = "Open Popup"
                position(100, 20)
                size(180, 40)
                source("UI.atlas", "green_button08", "green_button09")
                setNinePatch(5)
                fontName = "future.fnt"
                onClick {
                    val model = DemoPopup().createPopup("demo_popup", {
                        val popup = root.findActor<Group>("demo_popup")
                        popup.addAction(Actions.sequence(
                                Actions.scaleTo(0f, 0f, 1f, Interpolation.bounceOut),
                                Actions.run {
                                    popup.remove()
                                }
                        ))
                    })
                    val popup = stageBuilder.buildGroup(model)
                    popup.setScale(0f)
                    popup.addAction(Actions.scaleTo(1f, 1f, 1f, Interpolation.bounceOut))
                    root.findActor<Group>("main_group").addActor(popup)
                }
            }
            for (i in 1..5) {
                val model = DemoPopup().createPopup("demo_popup-$i", {
                    root.findActor<Group>("demo_popup-$i").remove()
                })
                add(model) {
                    model.apply {
                        position(400 + (i * 20), 10 + (i*10))
                    }
                }
            }
        }
        root.clearChildren()
        root.addActor(stageBuilder.buildGroup(groupModel))

    }

    override fun onStageReloaded() {}
}