package net.peakgames.libgdx.stagebuilder.core.widgets.listwidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.SnapshotArray;
import net.peakgames.libgdx.stagebuilder.core.ICustomWidget;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Displays a list of scrollable items. List items are inserted to the list using an Adapter.
 * Adapter pulls content from a dataasource such as java.util.ArrayList (or any any other collection)
 *
 * This is a scene2d widget. Items are scrolled vertically.
 *
 * This widget reuses its child actors.
 */
public class ListWidget extends WidgetGroup implements ICustomWidget, ListWidgetDataSetChangeListener {

    private static final int EMPTY_USER_OBJECT = -1;
    private static final Group EMPTY_ACTOR = new Group(){{setUserObject(EMPTY_USER_OBJECT);}};
    
    private static final float DEFAULT_FLING_TIME = 1f; // 1 second.
    private static final float DEFAULT_BLOCKED_DRAG_MOVE_COEFFICIENT = 0.15f;
    private static final float MAX_FLING_DELAY_MS = 150;
    private static final float DEFAULT_FLING_VELOCITY_FRICTION = 0.95f;

    private float flingTime = DEFAULT_FLING_TIME;
    private float defaultSettleVelocity = 300f;
    
    private IListWidgetAdapter listAdapter;
    private boolean drawDebug = false;
    private ShapeRenderer debugRenderer;
    private boolean needsLayout = false;
    private boolean resetPosition = true;
    private final Vector2 lastDragPoint = new Vector2();
    private boolean allActorsVisible = true;
    private List<Actor> recycledActors = new ArrayList<Actor>();
    private ListWidgetState state = ListWidgetState.STEADY;
    private float touchDownPos;
    private float flingVelocity;

    //distance (in one dimension) between last two drag events.
    private float dragDistance;
    private float clickCancelDragThreshold = 5f;
    private long lastTouchDragTime;
    
    private boolean isVertical = true;
    private float headPadding;
    private float measure;

    private OnItemClickedListener onItemClickedListener;
    private OnItemLongPressedListener onItemLongPressedListener;
    private ActorGestureListener listItemListener = new ActorGestureListener() {
        @Override
        public void tap(InputEvent event, float x, float y, int count, int button) {
            super.tap(event, x, y, count, button);
            Actor actor = event.getListenerActor();
            int position = (Integer) actor.getUserObject();
            if (onItemClickedListener != null ) {
                onItemClickedListener.onItemClicked(listAdapter.getItem(position), actor, position);
            }
        }

        @Override
        public boolean longPress(Actor actor, float x, float y) {
            int position = (Integer) actor.getUserObject();
            if (onItemLongPressedListener != null && state == ListWidgetState.STEADY) {
                onItemLongPressedListener.onItemLongPressed(listAdapter.getItem(position), actor, position);
            }

            return super.longPress(actor, x, y);
        }
    };

    private enum ListWidgetState {
        STEADY, SETTLE_HEAD, SETTLE_TAIL, FLINGING, DRAG_BACKWARDS_BLOCKED, DRAG_FORWARDS_BLOCKED
    }

    private enum DragDirection {
        FORWARD, BACKWARD
    }

    public void setListAdapter(IListWidgetAdapter listAdapter) {
        this.listAdapter = listAdapter;
        this.listAdapter.registerDataSetChangeListener(this);
        this.needsLayout = true;
    }

    @Override
    public void build(Map<String, String> attributes, AssetsInterface assetsInterface, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        float positionMultiplier = resolutionHelper.getPositionMultiplier();
        this.isVertical = !("horizontal".equals(String.valueOf(attributes.get("orientation"))));
        if (!isVertical && attributes.containsKey("headPadding")) {
            /* head padding only available for horizontal lists for now */
            this.headPadding = Float.valueOf(attributes.get("headPadding")) * positionMultiplier;
        }
        this.drawDebug = Boolean.valueOf(attributes.get("debug"));
        if (this.drawDebug) {
            this.debugRenderer = new ShapeRenderer();
        }
        setSize(getWidth() * positionMultiplier, getHeight() * positionMultiplier);
        clickCancelDragThreshold = clickCancelDragThreshold * positionMultiplier;
        addCaptureListener(new ListWidgetTouchListener());

        measure = isVertical ? getHeight() : getWidth();
    }
    
    public void setLongPressDuration(float duration) {
        listItemListener.getGestureDetector().setLongPressSeconds(duration);
    }

    public void setOnItemClickListener(OnItemClickedListener listener) {
        this.onItemClickedListener = listener;
    }
    
    public void setOnItemLongPressedListener(OnItemLongPressedListener listener) { 
        this.onItemLongPressedListener = listener;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.flush();
        clipBegin();
        super.draw(batch, parentAlpha);
        clipEnd();
        if (this.drawDebug) {
            batch.end();
            debugRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.rect(getX() + getParent().getX(), getY() + getParent().getY(), getWidth(), getHeight());
            debugRenderer.setColor(Color.YELLOW);
            debugRenderer.end();
            batch.begin();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (needsLayout) {
            if (state != ListWidgetState.STEADY) {
                handleState(delta);
                return;
            }
            
            int headActorIndex = 0;
            if(hasChildren()) {
                Actor headActor = getHeadActor();
                headActorIndex = (Integer)headActor.getUserObject();
            }

            if(resetPosition) {
                clearAndInitActorList(headActorIndex);
            } else {
                resetActorsData(headActorIndex);
            }
            settleIfNecessary();
            needsLayout = false;
        }

        handleState(delta);
    }

    private void handleState(float delta) {
        switch (state) {
            case SETTLE_HEAD:
                handleSettleHead(delta);
                break;
            case SETTLE_TAIL:
                handleSettleTail(delta);
                break;
            case FLINGING:
                handleFling(delta);
                break;
            case DRAG_BACKWARDS_BLOCKED:
                handleDragBackwardsBlocked();
                break;
            case DRAG_FORWARDS_BLOCKED:
                handleDragForwardsBlocked();
                break;

            default:
                break;
        }
    }

    private void resetActorsData(int headActorIndex) {
        Actor lastActor = getTailActor();
        int tailActorIndex = getActorIndex(lastActor);
        int from = Math.min(headActorIndex, listAdapter.getCount());
        int to = Math.max(tailActorIndex + 1, listAdapter.getCount());
        
        for (int i=from; i<to; i++) {
            if (isActorEmpty(lastActor) || (i > tailActorIndex && (isVertical ? getActorOrigin(lastActor) > 0 : getActorPos(lastActor) < measure))) {
                findRecycledActors();
                addItemAfterTail();
                Actor tailActor = getTailActor(); //actor that is added in previous line
                if (isVertical ? getActorPos(tailActor) >= 0 : getActorOrigin(tailActor) >= measure) { 
                    break;
                }
                continue;
            }
            
            Actor actorToUpdate = getActorWithUserObject(i);
            if (actorToUpdate == null || isActorEmpty(actorToUpdate)) {
                continue;
            }
            
            if (i >= listAdapter.getCount()) {
                recycledActors.add(actorToUpdate);
                removeActor(actorToUpdate);
                continue;
            }
            
            guardedGetActor(i, actorToUpdate);
            if (isActorOutside(actorToUpdate)) {
                break;
            }
        }
    }

    boolean isActorOutside(Actor actor) {
        return isVertical ? getActorPos(actor) + getActorMeasure(actor) < 0 :
                getActorPos(actor) - getActorMeasure(actor) > measure;
    }

    private void settleIfNecessary() {
        if (!listAdapter.isEmpty()) {
            Actor tailActor = getTailActor();
            if (isLastActor(tailActor)) {
                Actor headActor = getHeadActor();
                if (headActor.getUserObject().equals(0) && (isVertical || getActorPos(headActor) >= headPadding)) {
                    state = ListWidgetState.SETTLE_HEAD;
                    clearAndInitActorList(0);
                } else if (isVertical ? getActorPos(tailActor) > 0 : getActorOrigin(tailActor) < measure) {
                    state = ListWidgetState.SETTLE_TAIL;
                    clearAndInitActorList(((Integer) headActor.getUserObject()) - 1);
                }
            }
        }
    }

    private void clearAndInitActorList(int headActorIndex) {
        if (headActorIndex < 0) {
            return;
        }
        
        clearChildren();
        allActorsVisible = true;
        int from = Math.min(headActorIndex, listAdapter.getCount());
        int to = listAdapter.getCount();
        if(resetPosition) {
            from = 0; 
        }
        int counter = 0;
        for (int i = from; i < to; i++) {
            Actor actor = addActorToListWidget(counter++, i);
            if (isActorOutside(actor)) {
                allActorsVisible = false;
                break;
            }
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        float primaryPos = isVertical ? y : x;
        
        Actor hitActor = super.hit(x, y, touchable);
        if (hitActor == null) {
            return null;
        }
        if (hitActor == this) {
            return this;
        }

        if(isNotTouchInBorders(primaryPos)) {
            return null;
        } else {
            return hitActor;
        }
    }

    private void handleDragForwardsBlocked() {
        Actor lastActor = isVertical ? getTailActor() : getActorWithUserObject(0);
        if (isActorNotEmpty(lastActor) && isDragging(System.currentTimeMillis())) {
            moveItems(DragDirection.FORWARD, dragDistance * DEFAULT_BLOCKED_DRAG_MOVE_COEFFICIENT);
        }
    }

    private void handleDragBackwardsBlocked() {
        Actor firstActor = isVertical ? getActorWithUserObject(0) : getTailActor();
        if (isActorNotEmpty(firstActor) && isDragging(System.currentTimeMillis())) {
            moveItems(DragDirection.BACKWARD, dragDistance * DEFAULT_BLOCKED_DRAG_MOVE_COEFFICIENT);
        }
    }

    private void handleFling(float delta) {
        flingTime = flingTime - delta;
        if (flingTime > 0 && flingVelocity != 0) {
            float moveDistance = delta * flingVelocity * -1f;
            if (Math.abs(moveDistance) > measure) {
                //limit move distance.
                moveDistance = flingVelocity < 0 ? -measure : measure;
            }
            flingVelocity = flingVelocity * DEFAULT_FLING_VELOCITY_FRICTION;
            DragDirection direction = flingVelocity > 0 ? DragDirection.FORWARD : DragDirection.BACKWARD;
            if (checkDragBlocked(direction)) {
                if (direction == DragDirection.BACKWARD) {
                    state = isVertical ? ListWidgetState.SETTLE_HEAD : ListWidgetState.SETTLE_TAIL;
                } else {
                    state = isVertical ? ListWidgetState.SETTLE_TAIL : ListWidgetState.SETTLE_HEAD;
                }
                return;
            }
            moveItems(direction, moveDistance);
        } else {
            flingVelocity = 0;
            state = ListWidgetState.STEADY;
        }
    }

    private void handleSettleHead(float delta) {
        float moveAmount = delta * defaultSettleVelocity;
        Actor firstItemActor = getActorWithUserObject(0);
        if (isActorNotEmpty(firstItemActor)) {
            boolean isSteady = isVertical ? getActorOrigin(firstItemActor) + moveAmount >= measure : 
                    getActorPos(firstItemActor) - moveAmount <= headPadding;
            if (isSteady) {
                moveAmount = isVertical ? measure - getActorOrigin(firstItemActor) : getActorPos(firstItemActor) - headPadding;
                state = ListWidgetState.STEADY;
            }
            moveChildrenBy(isVertical ? moveAmount : -moveAmount);
        } else {
            state = ListWidgetState.STEADY;
        }
    }

    private void handleSettleTail(float delta) {
        float moveAmount = delta * defaultSettleVelocity;
        Actor lastActor = getTailActor();
        boolean isSteady = isVertical ? getActorPos(lastActor) - moveAmount <= 0 : 
                moveAmount + getActorOrigin(lastActor) >= measure;
        if (isSteady) {
            moveAmount = isVertical ? getActorPos(lastActor) : measure - getActorOrigin(lastActor); 
            state = ListWidgetState.STEADY;
        }
        moveChildrenBy(isVertical ? -moveAmount : moveAmount);
    }

    @Override
    public void onListWidgetDataSetChanged(boolean resetPosition) {
        this.needsLayout = true;
        this.resetPosition = resetPosition;
    }

    private boolean isNotTouchInBorders(float pos) {
        return pos > measure || pos < 0;
    }

    private void cancelChildTouchFocusOf(Group group) {
        SnapshotArray<Actor> children = group.getChildren();
        children.begin();
        int size = children.size;
        for (int i = 0; i < size; i++) {
            Actor child = children.get(i);

            if (getStage() == null) {
                break;
            }

            getStage().cancelTouchFocus(child);

            if (child instanceof Group) {
                cancelChildTouchFocusOf((Group) child);
            }
        }
        children.end();
    }

    private class ListWidgetTouchListener extends DragListener {
        long touchDownTimestamp;

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer) {
            cancelChildTouchFocusOf(ListWidget.this);
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            super.touchDown(event, x, y, pointer, button);

            float primaryPos = isVertical ? y : x;

            if(isNotTouchInBorders(primaryPos)) {
                return false;
            }
            touchDownPos = primaryPos;
            lastDragPoint.set(x, y);
            touchDownTimestamp = System.currentTimeMillis();
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            super.touchUp(event, x, y, pointer, button);

            float primaryPos = isVertical ? y : x;

            flingVelocity = calculateVelocity(primaryPos);
            if (flingVelocity != 0) {
                state = ListWidgetState.FLINGING;
                flingTime = DEFAULT_FLING_TIME;
            }

            touchDownPos = 0;

            if (allActorsVisible) {
                if (isVertical ? getActorOrigin(getHeadActor()) <= measure : getActorPos(getHeadActor()) >= headPadding) {
                    state = ListWidgetState.SETTLE_HEAD;
                } else if (isVertical ? getActorPos(getTailActor()) >= 0 : getActorOrigin(getTailActor()) <= measure) {
                    state = ListWidgetState.SETTLE_TAIL;
                }

                return;
            }

            Actor firstItemActor = getActorWithUserObject(0);
            if (isActorNotEmpty(firstItemActor)) {
                float moveAmount = isVertical ? measure - getActorOrigin(firstItemActor) : getActorPos(firstItemActor);
                if (moveAmount > headPadding) {
                    state = ListWidgetState.SETTLE_HEAD;
                }
            }
            Actor tailActor = getTailActor();
            float moveAmount = isVertical ? getActorPos(tailActor) : measure - getActorOrigin(tailActor);
            if (isLastActor(tailActor) && moveAmount > 0) {
                state = ListWidgetState.SETTLE_TAIL;
            }
        }

        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            listDrag(x, y);
        }

        public void listDrag(float x, float y) {
            DragDirection dragDirection;
            if (isVertical) {
                dragDirection = lastDragPoint.y > y ? DragDirection.BACKWARD : DragDirection.FORWARD;
                dragDistance = lastDragPoint.y - y;
            } else {
                dragDirection = lastDragPoint.x > x ? DragDirection.BACKWARD : DragDirection.FORWARD;
                dragDistance = lastDragPoint.x - x;
            }

            if (isDraggingForbidden(dragDirection)) {
                return;
            }

            if (Math.abs(dragDistance) > clickCancelDragThreshold) {
                lastTouchDragTime = System.currentTimeMillis();
            }

            lastDragPoint.set(x, y);

            if (checkDragBlocked(dragDirection)) {
                if (dragDirection == DragDirection.BACKWARD) {
                    state = ListWidgetState.DRAG_BACKWARDS_BLOCKED;
                } else {
                    state = ListWidgetState.DRAG_FORWARDS_BLOCKED;
                }
                return;
            }

            moveItems(dragDirection, dragDistance);
        }

        private boolean isDraggingForbidden(DragDirection dragDirection) {
            if (isVertical) {
                return allActorsVisible && dragDirection == DragDirection.FORWARD && 
                        getActorOrigin(getHeadActor()) <= measure && getActorPos(getTailActor()) >= 0;
            } else {
                return allActorsVisible && dragDirection == DragDirection.BACKWARD &&
                        getActorPos(getHeadActor()) >= 0 && getActorOrigin(getTailActor()) <= measure;  
            }
        }

        private float calculateVelocity(float pos) {
            long now = System.currentTimeMillis();
            if (ListWidget.this.isDragging(now)) {
                float duration = ((float) (now - touchDownTimestamp)) / 1000f;
                float distance = pos - touchDownPos;
                return distance / duration;
            } else {
                return 0f;
            }
        }
    }

    private boolean isLastActor(Actor actor) {
        return getActorIndex(actor) == listAdapter.getCount() - 1;
    }

    private boolean isDragging(long now) {
        return now - lastTouchDragTime < MAX_FLING_DELAY_MS;
    }

    /**
     * @param dragDirection touchDragged direction (FORWARD or BACKWARD)
     * @return returns true if drag is allowed.
     */
    private boolean checkDragBlocked(DragDirection dragDirection) {
        if (dragDirection == DragDirection.BACKWARD) {
            Actor lastActorBackwards = isVertical ? getHeadActor() : getTailActor();
            //if first item, block dragging
            if (getActorOrigin(lastActorBackwards) < measure) {
                if ((isVertical && isActorNotEmpty(lastActorBackwards) && getActorIndex(lastActorBackwards) == 0) ||
                        (!isVertical && isActorNotEmpty(lastActorBackwards) && isLastActor(lastActorBackwards)))    
                    return true;
            }
        } else { //forward
            Actor lastActorForwards = isVertical ? getTailActor() : getHeadActor();
            //if last item, block dragging
            if (getActorPos(lastActorForwards) > headPadding) {
                if ((isVertical && isLastActor(lastActorForwards)) || (!isVertical && getActorIndex(lastActorForwards) == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void moveItems(DragDirection dragDirection, float dragDistance) {
        if ((dragDirection == DragDirection.FORWARD && dragDistance > 0) || (dragDirection == DragDirection.BACKWARD && dragDistance < 0)) {
            return;
        }

        findRecycledActors(dragDirection, Math.abs(dragDistance));
        if (recycledActors.isEmpty() == false) {
            if (dragDirection == DragDirection.FORWARD) {
                if (isVertical) {
                    addItemAfterTail();
                } else {
                    addItemBeforeHead();
                }
            } else {
                if (isVertical) {
                    addItemBeforeHead();
                } else {
                    addItemAfterTail();
                }
            }
        }
        SnapshotArray<Actor> children = getChildren();
        int size = children.size;
        for (int i=0; i<size; i++) {
            Actor actor = children.get(i);
            setActorPos(actor, getActorPos(actor) - dragDistance);
        }
    }
    
    private void addItemBeforeHead () {
        Actor recycledActor = recycledActors.isEmpty() ? null : recycledActors.remove(0);
        Actor addBeforeActor = getHeadActor();
        
        int minIndex = getActorIndex(addBeforeActor);
        if (minIndex == 0) {
            return;
        }
        Actor newActor = guardedGetActor(minIndex - 1, recycledActor);
        if (newActor == null) {
            return;
        }
        newActor.setUserObject(minIndex - 1);
        setActorPos(newActor, isVertical ? getActorOrigin(addBeforeActor) : getActorPos(addBeforeActor) - getActorMeasure(addBeforeActor));
        
        if (recycledActor != null) {
            removeActor(recycledActor);
            listAdapter.actorRemoved(recycledActor);
        }
        addActor(newActor);
    }
    
    private void addItemAfterTail () {
        Actor recycledActor = recycledActors.isEmpty() ? null : recycledActors.remove(0);
        Actor addAfterActor = getTailActor();

        int maxIndex = getActorIndex(addAfterActor);
        if (maxIndex >= listAdapter.getCount() - 1) {
            return;
        }
        Actor newActor = guardedGetActor(maxIndex + 1, recycledActor);
        if (newActor == null) {
            return;
        }
        newActor.setUserObject(maxIndex + 1);
        if (isActorEmpty(addAfterActor)) {
            setActorPos(newActor, isVertical ? measure - getActorMeasure(newActor) : headPadding);
        } else {
            setActorPos(newActor, isVertical ? getActorPos(addAfterActor) - getActorMeasure(newActor) : getActorOrigin(addAfterActor));
        }
        
        if (recycledActor != null) {
            removeActor(recycledActor);
            listAdapter.actorRemoved(recycledActor);
        }
        
        addActor(newActor);
    }
    
    private Actor guardedGetActor(int position, Actor reusable) {
        if (position < 0 || position >= listAdapter.getCount()) {
            return null;
        }
        
        return listAdapter.getActor(position, reusable);
    }

    private void findRecycledActors(DragDirection dragDirection, float dragDistance) {
        recycledActors.clear();
        if (dragDirection == DragDirection.FORWARD) {
            Actor actorToRemove = isVertical ? getHeadActor() : getTailActor();
            if (isActorEmpty(actorToRemove)) {
                return;
            }
            int actorIndex = getActorIndex(actorToRemove);
            for (int i = actorIndex; i < listAdapter.getCount(); i++) {
                Actor actor = getActorWithUserObject(i);
                if (!isActorEmpty(actor) && getActorPos(actor) + dragDistance > measure) {
                    recycledActors.add(actor);
                } else {
                    break;
                }
            }
        } else {
            //drag BACKWARDS
            Actor actorToRemove = isVertical ? getTailActor() : getHeadActor();
            int actorIndex = getActorIndex(actorToRemove);
            for (int i = actorIndex; i >= 0; i--) {
                Actor actor = getActorWithUserObject(i);
                if (!isActorEmpty(actor) && getActorOrigin(actor) - dragDistance < 0) {
                    recycledActors.add(actor);
                } else {
                    break;
                }
            }
        }
    }
    
    private void findRecycledActors () {
        recycledActors.clear();
        SnapshotArray<Actor> children = getChildren();
        int size = children.size;
        for(int i=0; i<size; i++) {
            Actor actor =  children.get(i);
            boolean isOut = getActorPos(actor) + getActorMeasure(actor) > measure
                    || getActorOrigin(actor) - getActorMeasure(actor) < 0;
            if (!isActorEmpty(actor) && isOut) {
                recycledActors.add(actor);
            }
        }
    }

    private Actor addActorToListWidget(final int listAdapterIndex, int actorIndex) {
        final Actor actor = guardedGetActor(actorIndex, null);
        if (actor == null) {
            return null;
        }
        actor.setUserObject(actorIndex);
        addActor(actor);

        float totalMeasureOfPreviousActors = isVertical ? getActorMeasure(actor) : headPadding;
        int itemIndex = 0;
        SnapshotArray<Actor> children = getChildren();
        int size = children.size;
        for(int i=0; i<size; i++) {
            Actor previousActor =  children.get(i);
            if(itemIndex >= listAdapterIndex) {
                break;
            }
            
            if (isVertical ? getActorPos(previousActor) >= measure : getActorOrigin(previousActor) <= 0) {
                continue;
            }
            
            totalMeasureOfPreviousActors += getActorMeasure(previousActor);
            itemIndex++;
        }

        setActorPos(actor, isVertical ? measure - totalMeasureOfPreviousActors : totalMeasureOfPreviousActors);
        actor.addListener(listItemListener);
        return actor;
    }

    /**
     * tum child actor'leri (list items) value kadar listview ekseninde hareket ettirir.
     *
     * @param value
     */
    private void moveChildrenBy(float value) {
        SnapshotArray<Actor> children = getChildren();
        int count = children.size;
        for (int i = 0; i < count; i++) {
            Actor child = children.get(i);
            setActorPos(child, getActorPos(child) + value);
        }
    }

    private Actor getTailActor() {
        return isVertical ? getActorWithMinPos() : getActorWithMaxPos();
    }

    private Actor getHeadActor() {
        return isVertical ? getActorWithMaxPos() : getActorWithMinPos();
    }
    
    private Actor getActorWithMinPos () {
        Actor minPosActor = EMPTY_ACTOR;
        float min = Integer.MAX_VALUE;
        SnapshotArray<Actor> children = getChildren();
        int size = children.size;
        for (int i=0; i<size; i++) {
            Actor actor = children.get(i);
            if (getActorPos(actor) < min) {
                minPosActor = actor;
                min = getActorPos(minPosActor);
            }
        }
        return minPosActor;
    }
    
    private Actor getActorWithMaxPos () {
        Actor maxPosActor = EMPTY_ACTOR;
        float max = Integer.MIN_VALUE;

        SnapshotArray<Actor> children = getChildren();
        int size = children.size;
        for (int i=0; i<size; i++) {
            Actor actor = children.get(i);
            if (getActorPos(actor) > max) {
                maxPosActor = actor;
                max = getActorPos(maxPosActor);
            }
        }
        return maxPosActor;
    }

    /**
     * Actor'un list adapter'daki sirasini doner.
     *
     * @param actor
     * @return actor index.
     */
    private int getActorIndex(Actor actor) {
        Object userObject = actor.getUserObject();
        if (userObject == null) {
            throw new RuntimeException("Actor " + actor + " does not have a userObject");
        }
        return (Integer) userObject;
    }

    public Actor getActorWithUserObject(int index) {
        SnapshotArray<Actor> children = getChildren();
        int count = children.size;
        for (int i = 0; i < count; i++) {
            Actor child = children.get(i);
            if (child.getUserObject() != null && index == (Integer)child.getUserObject()) {
                return child;
            }
        }
        return EMPTY_ACTOR;
    }

    //rightmost x point if horizontal, top y if vertical
    private float getActorOrigin(Actor actor) {
        return getActorMeasure(actor) + getActorPos(actor);
    }

    private boolean isActorNotEmpty(Actor actor) {
        return actor != EMPTY_ACTOR;
    }

    private boolean isActorEmpty(Actor actor) {
        return actor == EMPTY_ACTOR;
    }
    
    private float getActorPos(Actor actor) {
        return isVertical ? actor.getY() : actor.getX();
    }
    
    private void setActorPos (Actor actor, float pos) {
        if (isVertical) {
            actor.setY(pos);
        } else {
            actor.setX(pos);
        }
    }
    
    private float getActorMeasure(Actor actor) {
        return isVertical ? actor.getHeight() : actor.getWidth();
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }
    
    public boolean isVertical() { 
        return isVertical;
    }

    public void setDefaultSettleVelocity(float defaultSettleVelocity) {
        this.defaultSettleVelocity = defaultSettleVelocity;
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        if (!isVertical) {
            measure = width;
        }
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        if (isVertical) {
            measure = height;
        }
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        measure = isVertical ? height : width;
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        measure = isVertical ? height : width;
    }

    public boolean replaceChildWith(Actor actorToRemove, Actor actorToInsert) {
        int index = getChildren().indexOf(actorToRemove, false);
        if (index == -1) return false;
        
        actorToInsert.setPosition(actorToRemove.getX(), actorToRemove.getY());
        actorToInsert.setUserObject(actorToRemove.getUserObject());
        removeActor(actorToRemove, false);
        addActorAt(index, actorToInsert);
        
        return true;
    }
}
