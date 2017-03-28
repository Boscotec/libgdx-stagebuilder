package net.peakgames.libgdx.stagebuilder.extensions

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.peakgames.libgdx.stagebuilder.core.model.*

inline fun GroupModel.build(build: GroupModel.() -> Unit): GroupModel {
    buildWithDefaults(build)
    return this
}

inline fun <T : BaseModel> GroupModel.buildModel(build: T.() -> Unit, construct: () -> T): T {
    val instance = construct()
    instance.buildWithDefaults(build)
    children.add(instance)
    return instance
}

inline fun GroupModel.group(build: GroupModel.() -> Unit): GroupModel = buildModel(build, ::GroupModel)
inline fun GroupModel.image(build: ImageModel.() -> Unit): ImageModel = buildModel(build, ::ImageModel)
inline fun GroupModel.button(build: ButtonModel.() -> Unit): ButtonModel = buildModel(build, ::ButtonModel)
inline fun GroupModel.checkbox(build: CheckBoxModel.() -> Unit): CheckBoxModel = buildModel(build, ::CheckBoxModel)
inline fun GroupModel.textfield(build: TextFieldModel.() -> Unit): TextFieldModel = buildModel(build, ::TextFieldModel)
inline fun GroupModel.textbutton(build: TextButtonModel.() -> Unit)  : TextButtonModel {
    return buildModel(build, ::TextButtonModel).apply {
        fontScale = 1f
    }
}
inline fun GroupModel.label(build: LabelModel.() -> Unit)  : LabelModel {
    return buildModel(build, ::LabelModel).apply {
        fontScale = 1f
    }
}

inline fun GroupModel.add(baseModel: BaseModel, build: BaseModel.() -> Unit) {
    baseModel.buildWithDefaults(build)
    children.add(baseModel)
}

inline fun ButtonModel.source(atlas: String, frameUp: String, frameDown: String) {
    this.atlasName = atlas
    this.frameUp = frameUp
    this.frameDown = frameDown
}

inline fun TextButtonModel.font(fontName:String, fontColor:String? = null, fontScale:Float = 1f) {
    this.fontName = fontName
    this.fontColor = fontColor
    this.fontScale = fontScale
}

inline fun LabelModel.font(fontName:String, fontColor:String? = null, fontScale:Float = 1f) {
    this.fontName = fontName
    this.fontColor = fontColor
    this.fontScale = fontScale
}

inline fun ImageModel.source(atlas: String, frame: String) {
    this.atlasName = atlas
    this.frame = frame
}

inline fun ImageModel.np(value: Int) {
    isNinepatch = true
    ninepatchOffset = value
}

inline fun BaseModel.position(x: Int, y: Int) {
    this.x = x.toFloat()
    this.y = y.toFloat()
}

inline fun BaseModel.origin(x: Float, y: Float) {
    this.originX = x
    this.originY = y
}

inline fun BaseModel.size(width: Int, height: Int) {
    this.width = width.toFloat()
    this.height = height.toFloat()
}

inline fun BaseModel.onClick(crossinline action: () -> Unit) {
    clickListener = object : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            action()
        }
    }
}

inline fun BaseModel.onClickWithEvent(crossinline action: (event: InputEvent, x: Float, y: Float) -> Unit) {
    clickListener = object : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            action(event, x, y)
        }
    }
}

inline fun BaseModel.onClickActor(crossinline action: (actor: Actor) -> Unit) {
    clickListener = object : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            action(event.target)
        }
    }
}

inline fun <T : BaseModel> T.buildWithDefaults(build: T.() -> Unit) {
    isVisible = true
    scale = 1f
    scaleX = 1f
    scaleY = 1f
    touchable = BaseModel.Touchable.ENABLED
    build()
}