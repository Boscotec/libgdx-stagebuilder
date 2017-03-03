package net.peakgames.libgdx.stagebuilder.core.demo;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.util.StageBuilderDSL;

public class LayoutScreen extends DemoScreen {

    private float size;
    private float pos;
    
    public LayoutScreen(AbstractGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        size = game.getResolutionHelper().getSizeMultiplier();
        pos = game.getResolutionHelper().getPositionMultiplier();

        addVerticalGroupButtons();

        StageBuilderDSL dsl = new StageBuilderDSL(
                game.getAssetsInterface(), game.getResolutionHelper(), game.getLocalizationService());
        
        addActor(dsl.test());
    }

    private void addVerticalGroupButtons() {
        final VerticalGroup verticalGroup = (VerticalGroup) findActor("verticalGroup");
        findActor("addBtn").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                verticalGroup.addActor(createImage());
            }
        });
        findActor("removeBtn").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                verticalGroup.removeActor(verticalGroup.getChildren().random());
            }
        });
    }

    private TextureAtlas.AtlasRegion region;
    private Image createImage() {
        if (region == null) {
            region = game.getAssetsInterface()
                    .getTextureAtlas("common.atlas").findRegion("accept_button");
        }
        
        Image image = new Image(region);
        image.setWidth(image.getMinWidth() * game.getResolutionHelper().getSizeMultiplier());
        image.setHeight(image.getMinHeight() * game.getResolutionHelper().getSizeMultiplier());
        return image;
    }
}
