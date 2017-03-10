package net.peakgames.libgdx.stagebuilder.extensions

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.peakgames.libgdx.stagebuilder.core.model.*

fun GroupModel.build(build: GroupModel.() -> Unit): GroupModel {
    buildWithDefaults(build)
    return this
}

inline fun <T : BaseModel> GroupModel.buildModel(crossinline build: T.() -> Unit, crossinline  construct: () -> T): T {
    val instance = construct()
    instance.buildWithDefaults(build)
    children.add(instance)
    return instance
}

fun GroupModel.group(build: GroupModel.() -> Unit): GroupModel = buildModel(build, ::GroupModel)
fun GroupModel.image(build: ImageModel.() -> Unit): ImageModel = buildModel(build, ::ImageModel)
fun GroupModel.button(build: ButtonModel.() -> Unit): ButtonModel = buildModel(build, ::ButtonModel)
fun GroupModel.checkbox(build: CheckBoxModel.() -> Unit): CheckBoxModel = buildModel(build, ::CheckBoxModel)
fun GroupModel.textfield(build: TextFieldModel.() -> Unit): TextFieldModel = buildModel(build, ::TextFieldModel)
fun GroupModel.textbutton(build: TextButtonModel.() -> Unit)  : TextButtonModel {
    return buildModel(build, ::TextButtonModel).apply {
        fontScale = 1f
    }
}
fun GroupModel.label(build: LabelModel.() -> Unit)  : LabelModel {
    return buildModel(build, ::LabelModel).apply {
        fontScale = 1f
    }
}

fun GroupModel.add(baseModel: BaseModel, build: BaseModel.() -> Unit) {
    baseModel.buildWithDefaults(build)
    children.add(baseModel)
}

fun ButtonModel.source(atlas: String, frameUp: String, frameDown: String) {
    this.atlasName = atlas
    this.frameUp = frameUp
    this.frameDown = frameDown
}

fun ImageModel.source(atlas: String, frame: String) {
    this.atlasName = atlas
    this.frame = frame
}

fun ImageModel.np(value: Int) {
    isNinepatch = true
    ninepatchOffset = value
}

fun BaseModel.position(x: Int, y: Int) {
    this.x = x.toFloat()
    this.y = y.toFloat()
}

fun BaseModel.origin(x: Float, y: Float) {
    this.originX = x
    this.originY = y
}

fun BaseModel.size(width: Int, height: Int) {
    this.width = width.toFloat()
    this.height = height.toFloat()
}

fun BaseModel.onClick(action: () -> Unit) {
    clickListener = object : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            action()
        }
    }
}

fun BaseModel.onClickWithEvent(action: (event: InputEvent, x: Float, y: Float) -> Unit) {
    clickListener = object : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            action(event, x, y)
        }
    }
}

fun BaseModel.onClickActor(action: (actor: Actor) -> Unit) {
    clickListener = object : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            action(event.target)
        }
    }
}

inline fun <T : BaseModel> T.buildWithDefaults(crossinline build: T.() -> Unit) {
    isVisible = true
    scale = 1f
    scaleX = 1f
    scaleY = 1f
    touchable = BaseModel.Touchable.ENABLED
    build()
}