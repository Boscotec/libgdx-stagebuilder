package net.peakgames.libgdx.stagebuilder.extensions.demo

import net.peakgames.libgdx.stagebuilder.core.model.GroupModel
import net.peakgames.libgdx.stagebuilder.core.model.ImageModel

fun GroupModel.build(build: GroupModel.() -> Unit) {
    build()
}

fun GroupModel.group(build: GroupModel.() -> Unit) {
    val group = GroupModel()
    group.isVisible = true // TODO set defaults
    group.build()
    children.add(group)
}

fun GroupModel.image(build: ImageModel.() -> Unit) {
    val image = ImageModel()
    image.isVisible = true // TODO set defaults
    image.build()
    children.add(image)
}