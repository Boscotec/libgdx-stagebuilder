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
    public Actor build(BaseModel model, Group parent) {
        GroupModel groupModel = (GroupModel) model;
        Group group = new Group();
        normalizeModelSize(model, model.getWidth(), model.getHeight());
        setBasicProperties(model, group);
        List<BaseModel> children = groupModel.getChildren();
        Collections.sort(children, new ZIndexComparator());
        for (BaseModel child : children) {
            Actor actor = builders.get(child.getClass()).build(child, group);
            group.addActor(actor);
        }
        
        //if (groupModel.isRelativeEnabled())
            if (parent != null) setSelfRelativePositions(group, groupModel, parent);    
            setChildRelativePositions(group, children);
        
        return group;
    }
    
    private void setSelfRelativePositions(Group self, GroupModel groupModel, Group parent) {
        setRelativePositions(parent, groupModel, self);
    }

    private void setChildRelativePositions(Group group, List<BaseModel> childrenModels) {
        SnapshotArray<Actor> children = group.getChildren();

        for (int i = 0; i < childrenModels.size(); i++) {
            BaseModel childModel = childrenModels.get(i);
            Actor child = children.get(i);
            setRelativePositions(group, childModel, child);
        }
    }

    private void setRelativePositions(Group parent, BaseModel childModel, Actor child) {
        Actor toLeft = GdxUtils.findActorSafe(parent, childModel.getToLeftOf());
        Actor toRight = GdxUtils.findActorSafe(parent, childModel.getToRightOf());
        Actor toAbove = GdxUtils.findActorSafe(parent, childModel.getToAboveOf());
        Actor toBelow = GdxUtils.findActorSafe(parent, childModel.getToBelowOf());
        
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

        float parentW = parent.getWidth();
        float parentH = parent.getHeight();
        int alignInParent = childModel.getAlignInParent();
        if (alignInParent == Align.bottomLeft) return;
        if ((alignInParent & Align.center) == Align.center) {
            child.setX((parentW - child.getWidth()) * 0.5f);
            child.setY((parentH - child.getHeight()) * 0.5f);
        }

        if ((alignInParent & Align.left) == Align.left) {
            child.setX(0);
            if (toLeft != null) {
                child.setWidth(toLeft.getX());
            }
        }
        
        if ((alignInParent & Align.bottom) == Align.bottom) {
            child.setY(0);
            if (toBelow != null) {
                child.setHeight(toBelow.getY());
            }
        }
        
        if ((alignInParent & Align.right) == Align.right) {
            if (toRight != null) {
                child.setWidth(parentW - (toRight.getX() + toRight.getWidth()));
            }
            child.setX(parentW - child.getWidth());
        }
        
        if ((alignInParent & Align.top) == Align.top) {
            if (toAbove != null) {
                child.setHeight(parentH - (toAbove.getY() + toAbove.getHeight()));
            }
            child.setY(parentH - child.getHeight());
        }
    }
}
