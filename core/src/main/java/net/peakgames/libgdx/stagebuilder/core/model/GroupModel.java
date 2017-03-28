package net.peakgames.libgdx.stagebuilder.core.model;

import java.util.ArrayList;
import java.util.List;

public class GroupModel extends BaseModel {

    private List<BaseModel> children = new ArrayList<BaseModel>();

    public List<BaseModel> getChildren() {
        return children;
    }

    public void setChildren(List<BaseModel> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        toString(sb, 1);
        return sb.toString();
    }

    public void toString(StringBuilder sb, int indent) {
        int childCount = children.size();
        sb.append(getName(this)).append('\n');
        for (int i = 0; i < childCount; i++) {
            BaseModel child = children.get(i);
            for(int j = 0; j < indent; j++) {
                sb.append("|  ");
            }
            if (child instanceof GroupModel) {
                ((GroupModel)child).toString(sb, indent + 1);
            } else {
                sb.append(getName(child)).append('\n');
            }
        }
    }

    private static String getName(BaseModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append(model.getClass().getSimpleName());
        String name = model.getName();
        if (name != null && !"".equals(name.trim())) {
            sb.append("[").append(name).append("]");
        }
        return sb.toString();
    }
}
