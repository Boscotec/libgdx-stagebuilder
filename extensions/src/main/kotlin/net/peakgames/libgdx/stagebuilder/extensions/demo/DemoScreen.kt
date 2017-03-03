package net.peakgames.libgdx.stagebuilder.extensions.demo

import net.peakgames.libgdx.stagebuilder.core.AbstractGame
import net.peakgames.libgdx.stagebuilder.core.AbstractScreen
import net.peakgames.libgdx.stagebuilder.core.model.GroupModel

class DemoScreen(game: AbstractGame) : AbstractScreen(game) {

    override fun unloadAssets() {
    }


    override fun render(delta: Float) {
        super.render(delta)
        root.clearChildren()

        val groupModel = GroupModel()
        groupModel.build {
            name = "root_group"
            image {
                name = "logo_image 2"
                atlasName = "common.atlas"
                frame = "androidlogo"
                x = 300f
                y = 100f
                width = 300f
                height = 300f
            }
            for (i in 1..10) {
                image {
                    name = "logo_image-$i"
                    atlasName = "common.atlas"
                    frame = "androidlogo"
                    x = i * 10 + 100f
                    y = i * 10 + 100f
                    width = 100f
                    height = 100f
                }
            }
        }
        val group = stageBuilder.buildGroup(groupModel)
        root.addActor(group)

    }

    override fun onStageReloaded() {

    }

}