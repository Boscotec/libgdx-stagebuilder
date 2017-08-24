package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.GroupModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class GroupBuilderTest {

	private AssetsInterface assets = Mockito.mock(AssetsInterface.class);
	private LocalizationService locale = Mockito.mock(LocalizationService.class);
	private ResolutionHelper resHelper = Mockito.mock(ResolutionHelper.class);

	private GroupBuilder groupBuilder;

	@Before
	public void before() {
		Map<Class<? extends BaseModel>, ActorBuilder> builders = new HashMap<Class<? extends BaseModel>, ActorBuilder>();
		groupBuilder = new GroupBuilder(builders, assets, resHelper, locale);
		builders.put(GroupModel.class, groupBuilder);
		
		when(resHelper.getSizeMultiplier()).thenReturn(1f);
		when(resHelper.getPositionMultiplier()).thenReturn(1f);
	}
	
	@Test
	public void alignmentInParent_relativePositionsTest() {
		GroupModel groupModel = groupWithChildrenAlignment(500, 500,
				Arrays.asList(
						Align.topRight, Align.topLeft, Align.top, Align.center, 
						Align.bottom, Align.left, Align.right, Align.bottomRight, Align.bottomLeft,
						Align.top | Align.center));
		Group resultingGroup = (Group) groupBuilder.build(groupModel, null);

		assertEquals(10, resultingGroup.getChildren().size);
		
		//top-right
		assertEquals(490f, resultingGroup.getChildren().get(0).getX(), 0.1f);
		assertEquals(490f, resultingGroup.getChildren().get(0).getY(), 0.1f);

		//top-left
		assertEquals(0f, resultingGroup.getChildren().get(1).getX(), 0.1f);
		assertEquals(490f, resultingGroup.getChildren().get(1).getY(), 0.1f);

		//top
		assertEquals(0f, resultingGroup.getChildren().get(2).getX(), 0.1f);
		assertEquals(490f, resultingGroup.getChildren().get(2).getY(), 0.1f);
		
		//center
		assertEquals(245f, resultingGroup.getChildren().get(3).getX(), 0.1f);
		assertEquals(245f, resultingGroup.getChildren().get(3).getY(), 0.1f);
		
		//bottom
		assertEquals(0f, resultingGroup.getChildren().get(4).getX(), 0.1f);
		assertEquals(0f, resultingGroup.getChildren().get(4).getY(), 0.1f);

		//left
		assertEquals(0f, resultingGroup.getChildren().get(5).getX(), 0.1f);
		assertEquals(0f, resultingGroup.getChildren().get(5).getY(), 0.1f);
		
		//right
		assertEquals(490f, resultingGroup.getChildren().get(6).getX(), 0.1f);
		assertEquals(0f, resultingGroup.getChildren().get(6).getY(), 0.1f);
		
		//bottomRight
		assertEquals(490f, resultingGroup.getChildren().get(7).getX(), 0.1f);
		assertEquals(0f, resultingGroup.getChildren().get(7).getY(), 0.1f);
		
		//bottomLeft
		assertEquals(0f, resultingGroup.getChildren().get(8).getX(), 0.1f);
		assertEquals(0f, resultingGroup.getChildren().get(8).getY(), 0.1f);
		
		//top-center
		assertEquals(245f, resultingGroup.getChildren().get(9).getX(), 0.1f);
		assertEquals(490f, resultingGroup.getChildren().get(9).getY(), 0.1f);
	}
	
	@Test
	public void toXOf_relativePositionsTest() {
		GroupModel parent = groupWith("parent", 0, 0, 500, 500);
		
		GroupModel child1 = groupWith("child1", 10, 10, 10, 10);
		GroupModel child2 = groupWith("child2", 0f, 0f, 20, 20);
		child2.setToAboveOf("child1"); child2.setToRightOf("child1");

		GroupModel child3 = groupWith("child3", 0f, 0f, 10, 20);
		child3.setToAboveOf("child1"); child3.setToLeftOf("child2");
		
		GroupModel child4 = groupWith("child4", 0f, 0f, 10, 20);
		child4.setToAboveOf("child3"); child4.setToRightOf("child2");

		parent.setChildren(Arrays.<BaseModel>asList(child1,child2, child3, child4)); 
		Group resultingGroup = (Group) groupBuilder.build(parent, null);
		
		assertEquals(20, resultingGroup.findActor("child2").getX(), 0.1f);
		assertEquals(20, resultingGroup.findActor("child2").getY(), 0.1f);
		assertEquals(20, resultingGroup.findActor("child2").getWidth(), 0.1f);

		assertEquals(10, resultingGroup.findActor("child3").getX(), 0.1f);
		assertEquals(20, resultingGroup.findActor("child3").getY(), 0.1f);

		assertEquals(40, resultingGroup.findActor("child4").getX(), 0.1f);
		assertEquals(40, resultingGroup.findActor("child4").getY(), 0.1f);
	}

	@Test
	public void toXOf_sizeChange_relativePositionsTest() {
		GroupModel parent = groupWith("parent", 0, 0, 500, 500);

		GroupModel bottomLeftChild = groupWith("bottomLeftChild", 0, 0, 10, 10);
		GroupModel bottomRightChild = groupWith("bottomRightChild", 80, 0, 10, 10);
		GroupModel topRightChild = groupWith("topRightChild", 80, 80, 10, 10);
		GroupModel topLeftChild = groupWith("topLeftChild", 0, 80, 10, 10);
		
		GroupModel child1 = groupWith("child1", 0f, 0f, 10, 20);
		child1.setToRightOf("bottomLeftChild"); child1.setToLeftOf("bottomRightChild");

		GroupModel child2 = groupWith("child2", 0f, 0f, 10, 20);
		child2.setToBelowOf("topLeftChild"); child2.setToRightOf("topLeftChild"); 
		child2.setToAboveOf("bottomLeftChild"); child2.setToLeftOf("bottomRightChild");

		parent.setChildren(Arrays.<BaseModel>asList(
				bottomLeftChild, bottomRightChild, topLeftChild, topRightChild,
				child1, child2));
		Group resultingGroup = (Group) groupBuilder.build(parent, null);

		assertEquals(10, resultingGroup.findActor("child1").getX(), 0.1f);
		assertEquals(0, resultingGroup.findActor("child1").getY(), 0.1f);
		assertEquals(70, resultingGroup.findActor("child1").getWidth(), 0.1f);
		assertEquals(20, resultingGroup.findActor("child1").getHeight(), 0.1f);

		assertEquals(10, resultingGroup.findActor("child2").getX(), 0.1f);
		assertEquals(10, resultingGroup.findActor("child2").getY(), 0.1f);
		assertEquals(70, resultingGroup.findActor("child2").getWidth(), 0.1f);
		assertEquals(70, resultingGroup.findActor("child2").getHeight(), 0.1f);
	}

	@Test
	public void alignInParent_overrides_toXOf_relativePositionsTest() {
		GroupModel parent = groupWith("parent", 0, 0, 500, 500);

		GroupModel bottomLeftChild = groupWith("bottomLeftChild", 0, 0, 10, 10);
		GroupModel bottomRightChild = groupWith("bottomRightChild", 80, 0, 10, 10);
		GroupModel topRightChild = groupWith("topRightChild", 80, 80, 10, 10);
		GroupModel topLeftChild = groupWith("topLeftChild", 0, 80, 10, 10);

		GroupModel child1 = groupWith("child1", 0f, 0f, 10, 20);
		child1.setToRightOf("bottomLeftChild"); child1.setToLeftOf("bottomRightChild");
		child1.setToAboveOf("bottomLeftChild"); child1.setToBelowOf("topRightChild");
		
		child1.setAlignInParent(Align.top | Align.center);
		
		parent.setChildren(Arrays.<BaseModel>asList(
				bottomLeftChild, bottomRightChild, topLeftChild, topRightChild,
				child1));
		Group resultingGroup = (Group) groupBuilder.build(parent, null);

		assertEquals(215f, resultingGroup.findActor("child1").getX(), 0.1f);
		assertEquals(10f, resultingGroup.findActor("child1").getY(), 0.1f);
		assertEquals(70f, resultingGroup.findActor("child1").getWidth(), 0.1f);
		assertEquals(490f, resultingGroup.findActor("child1").getHeight(), 0.1f);
	}
	
	private GroupModel groupWith(String name, float x, float y, float width, float height) {
		GroupModel group = new GroupModel();
		group.setName(name);
		group.setX(x); group.setY(y); 
		group.setWidth(width); group.setHeight(height);
		return group;
	}
	
	private GroupModel groupWithChildrenAlignment(int width, int height, List<Integer> alignments) {
		GroupModel parent = groupWith(null, 0, 0, width, height);
		
		List<BaseModel> children = new ArrayList<BaseModel>(alignments.size());
		for (int alignment: alignments) {
			GroupModel child = groupWith(null, 0, 0, 10, 10);
			child.setAlignInParent(alignment);
			children.add(child);
		}
		
		parent.setChildren(children);
		return parent;
	}
}
