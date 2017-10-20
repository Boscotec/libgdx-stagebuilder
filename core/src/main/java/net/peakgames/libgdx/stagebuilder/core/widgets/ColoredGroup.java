package net.peakgames.libgdx.stagebuilder.core.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import net.peakgames.libgdx.stagebuilder.core.model.GroupModel;

public class ColoredGroup extends Group{

    private static Texture texture;
    public Color color;

    public ColoredGroup(GroupModel groupModel) {
        if (texture == null) createTexture();

        try {
            color = Color.valueOf(groupModel.getColor());
        } catch (NumberFormatException ignored) {}
    }

    private static void createTexture () {
        Pixmap pixmap = new Pixmap(3, 3, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillRectangle(0, 0, 3, 3);
        texture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        if (color != null) {
            batch.setColor(color);
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
        super.draw(batch, parentAlpha);
    }
}