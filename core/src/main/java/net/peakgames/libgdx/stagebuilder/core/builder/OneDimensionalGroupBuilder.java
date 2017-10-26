package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.OneDimensionGroupModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import net.peakgames.libgdx.stagebuilder.core.widgets.HorizontalGroupProxy;
import net.peakgames.libgdx.stagebuilder.core.widgets.OneDimensionGroupInterface;
import net.peakgames.libgdx.stagebuilder.core.widgets.VerticalGroupProxy;

import java.util.List;
import java.util.Map;

public class OneDimensionalGroupBuilder extends ActorBuilder {

    private final Map<Class<? extends BaseModel>, ActorBuilder> builders;

    public OneDimensionalGroupBuilder(Map<Class<? extends BaseModel>, ActorBuilder> builders, AssetsInterface assets,
                                      ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
        this.builders = builders;
    }

    @Override
    public Actor build(BaseModel model, Group parent) {
        OneDimensionGroupModel groupModel = (OneDimensionGroupModel) model;
        groupModel.normalize(resolutionHelper);
        
        OneDimensionGroupInterface group;
        if (groupModel.getOrientation() == OneDimensionGroupModel.Orientation.VERTICAL) {
            group = new VerticalGroupProxy();
        } else {
            group = new HorizontalGroupProxy();
        }
        
        normalizeModelSize(model, parent, model.getWidth(), model.getHeight());
        setBasicProperties(model, group.getGroup());
        
        if (groupModel.getAlign() != OneDimensionGroupModel.DEFAULT_ALIGNMENT) {
            group.align(groupModel.getAlign());
        }
        group.space(groupModel.getSpacing());

        group.pad(groupModel.getPadTop(), groupModel.getPadLeft(),
                groupModel.getPadBottom(), groupModel.getPadRight());
        if (groupModel.getPads() != null) {
            float[] pads = groupModel.getPads();
            group.pad(pads[0], pads[1], pads[2], pads[3]);
        }
        
        if (groupModel.isReverse()) group.reverse();
        if (groupModel.isFill()) group.fill();

        List<BaseModel> children = groupModel.getChildren();

        for(BaseModel childModel: children) {
            Actor actor = builders.get(childModel.getClass()).build(childModel, group.getGroup());
            group.getGroup().addActor(actor);
        }
        
        return group.getGroup();
    }
}
