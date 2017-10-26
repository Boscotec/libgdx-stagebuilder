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
import net.peakgames.libgdx.stagebuilder.core.widgets.ColoredGroup;

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
        normalizeModelSize(model, parent, model.getWidth(), model.getHeight());
        Group group = groupModel.getColor() != null ? new ColoredGroup(groupModel) : new Group();
        setBasicProperties(model, group);
        List<BaseModel> children = groupModel.getChildren();
        Collections.sort(children, new ZIndexComparator());
            
        if (parent != null) setSelfRelativePositions(group, groupModel, parent);    
        
        for (BaseModel child : children) {
            Actor actor = builders.get(child.getClass()).build(child, group);
            group.addActor(actor);
        }
        
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
            child.setWidth(Math.abs(getAnchoredX(toLeft) - (getAnchoredX(toRight) + getScaledWidth(toRight))));
        } else if (toLeft != null) {
            child.setX(getAnchoredX(toLeft) - getScaledWidth(child));
        }

        if (toRight != null) {
            child.setX(getAnchoredX(toRight) + getScaledWidth(toRight));
        }

        if (toBelow != null && toAbove != null) {
            child.setHeight(Math.abs(getAnchoredY(toBelow) - (getAnchoredY(toAbove) + getScaledHeight(toAbove))));
        } else if (toBelow != null) {
            child.setY(getAnchoredY(toBelow) - getScaledHeight(child));
        }

        if (toAbove != null) {
            child.setY(getAnchoredY(toAbove) + getScaledHeight(toAbove));
        }

        float parentW = getScaledWidth(parent);
        float parentH = getScaledHeight(parent);
        int alignInParent = childModel.getAlignInParent();
        if (alignInParent == Align.bottomLeft) return;
        if ((alignInParent & Align.center) == Align.center) {
            child.setX((parentW - getScaledWidth(child)) * 0.5f);
            child.setY((parentH - getScaledHeight(child)) * 0.5f);
        }

        if ((alignInParent & Align.left) == Align.left) {
            child.setX(0);
            if (toLeft != null) {
                child.setWidth(getAnchoredX(toLeft));
            }
        }
        
        if ((alignInParent & Align.bottom) == Align.bottom) {
            child.setY(0);
            if (toBelow != null) {
                child.setHeight(getAnchoredY(toBelow));
            }
        }
        
        if ((alignInParent & Align.right) == Align.right) {
            if (toRight != null) {
                child.setWidth(parentW - (getAnchoredX(toRight) + getScaledWidth(toRight)));
            }
            child.setX(parentW - getScaledWidth(child));
        }
        
        if ((alignInParent & Align.top) == Align.top) {
            if (toAbove != null) {
                child.setHeight(parentH - (getAnchoredY(toAbove) + getScaledHeight(toAbove)));
            }
            child.setY(parentH - getScaledHeight(child));
        }
    }
    
    private static float getScaledWidth(Actor actor) {
        return actor.getWidth() * actor.getScaleX();
    }

    private static float getScaledHeight(Actor actor) {
        return actor.getHeight() * actor.getScaleY();
    }
    
    private static float getAnchoredX(Actor actor) {
        return actor.getX() + actor.getOriginX();
    }

    private static float getAnchoredY(Actor actor) {
        return actor.getY() + actor.getOriginY();
    }
}
