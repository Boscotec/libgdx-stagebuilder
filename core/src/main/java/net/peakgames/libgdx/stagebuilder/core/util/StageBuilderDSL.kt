package net.peakgames.libgdx.stagebuilder.core.util

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper
import net.peakgames.libgdx.stagebuilder.core.builder.StageBuilder
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel
import net.peakgames.libgdx.stagebuilder.core.model.GroupModel
import net.peakgames.libgdx.stagebuilder.core.model.ImageModel
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService

class StageBuilderDSL(assets: AssetsInterface, resHelper: ResolutionHelper,
                      localization: LocalizationService) : StageBuilder(assets, resHelper, localization) {
    
    fun Group.image(name: String? = null, x: Float = 0f, y: Float = 0f, width: Float = 0f, height: Float = 0f,
                    atlas: String? = null, frame: String? = null, src: String? = null, //todo add np, and other values
                    build: (Image.() -> Unit)? = null): Image {
        
        val imageModel = ImageModel()
        setBaseModelValues(imageModel, name, x, y, width, height)
        imageModel.atlasName = atlas
        imageModel.frame = frame
        
        //todo set other values like np, src etc
        
        val image = buildFromModel<ImageModel, Image>(imageModel)
        build?.invoke(image)
        addActor(image) //add as child to group
        return image
    }
    
    fun group(name: String? = null, x: Float = 0f, y: Float = 0f, width: Float = 0f, height: Float = 0f,
              build: Group.() -> Unit): Group {
        val groupModel = GroupModel()
        setBaseModelValues(groupModel, name, x, y, width, height)
        val group = buildFromModel<GroupModel, Group>(groupModel) 
        group.build()
        return group
    }

    private fun setBaseModelValues(baseModel: BaseModel, name: String? = null, x: Float = 0f, y: Float = 0f, 
                                   width: Float = 0f, height: Float = 0f) {
        baseModel.apply { 
            this.name = name
            this.x = x
            this.y = y
            this.width = width
            this.height = height
            isVisible = true
        }
    }
    
    inline private fun <reified T: BaseModel, reified K: Actor> buildFromModel(model: T) : K {
        return builders[T::class.java]?.build(model) as? K ?: throw Exception("TODO")
    }

    fun test(): Group {
        return group(name = "root", x = 10f, width = 300f, height = 100f) {
            image (atlas = "common.atlas", frame = "androidlogo", width = 140f)
            
            for (i in 0..110) {
                image(atlas = "common.atlas", frame = "androidlogo", x = 60f + i * 5f) {
                    setOrigin(Align.center)
                    rotateBy(40f + i * 1.7f)
                }
            }

            debug()
        }
    }
}