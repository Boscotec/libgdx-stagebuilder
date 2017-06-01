package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SnapshotArray;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.GroupModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import net.peakgames.libgdx.stagebuilder.core.util.GdxUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GroupBuilder extends ActorBuilder {
    private final Map<Class<? extends BaseModel>, ActorBuilder> builders;

    public GroupBuilder(Map<Class<? extends BaseModel>, ActorBuilder> builders, AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
        this.builders = builders;
    }

    @Override
    public Actor build(BaseModel model) {
        GroupModel groupModel = (GroupModel) model;
        Group group = new Group();
        normalizeModelSize(model, model.getWidth(), model.getHeight());
        setBasicProperties(model, group);
        List<BaseModel> children = groupModel.getChildren();
        Collections.sort(children, new ZIndexComparator());
        for (BaseModel child : children) {
            Actor actor = builders.get(child.getClass()).build(child);
            group.addActor(actor);
        }
        
        //if (groupModel.isRelativeEnabled()) 
            setRelativePositions(group, children);
        
        return group;
    }

    private void setRelativePositions(Group group, List<BaseModel> childrenModels) {
        SnapshotArray<Actor> children = group.getChildren();

        for (int i = 0; i < childrenModels.size(); i++) {
            BaseModel childModel = childrenModels.get(i);
            Actor child = children.get(i);
            
            Actor toLeft = GdxUtils.findActorSafe(group, childModel.getToLeftOf());
            Actor toRight = GdxUtils.findActorSafe(group, childModel.getToRightOf());
            Actor toAbove = GdxUtils.findActorSafe(group, childModel.getToAboveOf());
            Actor toBelow = GdxUtils.findActorSafe(group, childModel.getToBelowOf());
            
            if (toLeft != null && toRight != null) {
                child.setWidth(Math.abs(toLeft.getX() - (toRight.getX() + toRight.getWidth())));
            } else if (toLeft != null) {
                child.setX(toLeft.getX() - child.getWidth());
            }
            
            if (toRight != null) {
                child.setX(toRight.getX() + toRight.getWidth());
            }

            if (toBelow != null && toAbove != null) {
                child.setHeight(Math.abs(toBelow.getY() - (toAbove.getY() + toAbove.getHeight())));
            } else if (toBelow != null) {
                child.setY(toBelow.getY() - child.getHeight());
            }

            if (toAbove != null) {
                child.setY(toAbove.getY() + toAbove.getHeight());
            }

            float parentW = group.getWidth();
            float parentH = group.getHeight();
            int alignInParent = childModel.getAlignInParent();
            if (alignInParent == Align.bottomLeft) continue;
            if ((alignInParent & Align.center) == Align.center) {
                child.setX((parentW - child.getWidth()) * 0.5f);
                child.setY((parentH - child.getHeight()) * 0.5f);
            }
            
            if ((alignInParent & Align.left) == Align.left) {
                child.setX(0);
            }
            if ((alignInParent & Align.bottom) == Align.bottom) {
                child.setY(0);
            }
            if ((alignInParent & Align.right) == Align.right) {
                child.setX(parentW - child.getWidth());
            }
            if ((alignInParent & Align.top) == Align.top) {
                child.setY(parentH - child.getHeight());
            }
        }
    }
}
