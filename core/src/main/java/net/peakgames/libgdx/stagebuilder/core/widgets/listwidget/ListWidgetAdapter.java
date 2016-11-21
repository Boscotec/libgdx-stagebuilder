package net.peakgames.libgdx.stagebuilder.core.widgets.listwidget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import net.peakgames.libgdx.stagebuilder.core.builder.StageBuilder;

import java.util.Collections;
import java.util.List;

/**
 * An abstract list widget adapter that uses list of items for populating list view.
 */
public abstract class ListWidgetAdapter<T> implements IListWidgetAdapter<T> {

    protected List<T> items = Collections.emptyList();
    protected ListWidgetDataSetChangeListener dataSetChangeListener;
    protected StageBuilder stageBuilder;

    public ListWidgetAdapter(StageBuilder stageBuilder) {
        this.stageBuilder = stageBuilder;
    }

    @Override
    public void initialize(List<T> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public abstract Actor getActor(int position, Actor reusableActor);

    @Override
    public void notifyDataSetChanged() {
        dataSetChangeListener.onListWidgetDataSetChanged(true);
    }

    @Override
    public void notifyDataSetChanged(boolean resetPosition) {
        dataSetChangeListener.onListWidgetDataSetChanged(resetPosition);
    }

    @Override
    public void registerDataSetChangeListener(ListWidgetDataSetChangeListener listener) {
        this.dataSetChangeListener = listener;
    }

    @Override
    public void addItem(T item) {
        this.items.add(item);
    }

    @Override
    public void actorRemoved(Actor actor) {
    }

    public void swapItems(int pos1, int pos2) {
        if (pos1 < 0 || pos1 >= items.size() || pos2 < 0 || pos2 >= items.size()) return;
        Collections.swap(items, pos1, pos2);
    }

    @Override
    public void removeItem(Object object) {
        if(!this.items.isEmpty() && this.items.contains(object)){
            this.items.remove(object);
        }
    }
}
