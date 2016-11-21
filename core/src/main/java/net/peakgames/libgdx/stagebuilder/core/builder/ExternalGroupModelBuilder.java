package net.peakgames.libgdx.stagebuilder.core.builder;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.ExternalGroupModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class ExternalGroupModelBuilder extends ActorBuilder {

    private final StageBuilder stageBuilder;

    public ExternalGroupModelBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService, StageBuilder stageBuilder) {
        super(assets, resolutionHelper, localizationService);
        this.stageBuilder = stageBuilder;
    }

    @Override
    public Actor build(BaseModel model) {
        ExternalGroupModel externalGroupModel = (ExternalGroupModel) model;
        try {
            String pathName = externalGroupModel.getPathName();
            if(pathName != null) {
                pathName += "/" + externalGroupModel.getFileName();
            } else {
                pathName = externalGroupModel.getFileName();
            }
            Group group = stageBuilder.buildGroup(pathName);
            updateGroupProperties( externalGroupModel, group);
            return group;
        } catch (Exception e) {
            Gdx.app.log(TAG, "Failed to build group from external file " + externalGroupModel.getFileName());
            return null;
        }
    }

    private void updateGroupProperties( ExternalGroupModel model, Group group){
        group.setName( model.getName());
        model.setWidth( group.getWidth());
        model.setHeight( group.getHeight());
        group.setVisible(model.isVisible());
        Vector2 screenPos;
        if (model.getScreenAlignmentSupport() == null) {
            screenPos = calculateScreenPosition(model.getScreenAlignment(), model);
        }
        else {
            screenPos = calculateScreenPosition(model.getScreenAlignment(), model.getScreenAlignmentSupport(), model);
        }

        if(screenPos != null){
            group.setPosition(screenPos.x, screenPos.y);
        }else{
            group.setPosition(model.getX() * resolutionHelper.getPositionMultiplier(), model.getY() * resolutionHelper.getPositionMultiplier());
        }

        setScaleProperty(model, group);
    }
}
