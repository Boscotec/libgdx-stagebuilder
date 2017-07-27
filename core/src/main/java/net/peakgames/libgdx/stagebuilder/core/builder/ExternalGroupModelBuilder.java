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
    public Actor build(BaseModel model, Group parent) {
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
        
        // For example .. <FriendsPanel > in top xml and its implementation's root group (FriendsPanel.xml file's) 
        // should both be visible=true in order to set whole group visible, if at least one of them set to false 
        // whole group will be invisible.
        group.setVisible(model.isVisible() && group.isVisible());
        
        Vector2 screenPos;
        if (model.getScreenAlignmentSupport() == null) {
            screenPos = calculateScreenPosition(model.getScreenAlignment(), model);
        } else {
            screenPos = calculateScreenPosition(model.getScreenAlignment(), model.getScreenAlignmentSupport(), model);
        }

        if (screenPos != null) {
            group.setPosition(screenPos.x, screenPos.y);
        } else {
            float positionMultiplier = resolutionHelper.getPositionMultiplier();
            group.setPosition(model.getX() * positionMultiplier, model.getY() * positionMultiplier);
        }

        setScaleProperty(model, group);
    }
}
