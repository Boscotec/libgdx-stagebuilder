package net.peakgames.libgdx.stagebuilder.extensions

import net.peakgames.libgdx.stagebuilder.core.model.GroupModel
import org.junit.Assert.*
import org.junit.Test

class StageBuilderDslTest {

    @Test fun `empty group`() {
        val group = GroupModel().build {}
        assertEquals(0, group.children.size)
    }

    @Test fun `complex hierarchy`() {
        val group = GroupModel().build {
            group {
                button {  }
                for (i in 1..5) {
                    image { }
                }
                textbutton {  }
            }
            group {
                label { }
                checkbox {  }
                group {
                    image {  }
                    group {
                        label {  }
                        textbutton {  }
                        image {  }
                    }
                }
            }
        }
        //println(group)
        assertEquals(2, group.children.size)
        assertEquals(7, (group.children[0] as GroupModel).children.size)
        assertEquals(3, (group.children[1] as GroupModel).children.size)
    }
}