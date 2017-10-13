package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.SnapshotArray;
import net.peakgames.libgdx.stagebuilder.core.CustomView;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.GroupModel;
import net.peakgames.libgdx.stagebuilder.core.model.ViewModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.List;

public class ViewBuilder extends ActorBuilder {

	private StageBuilder builder;
	
	public ViewBuilder(StageBuilder builder, AssetsInterface assets, ResolutionHelper resolutionHelper,
	                   LocalizationService localizationService) {
		super(assets, resolutionHelper, localizationService);
		this.builder = builder;
	}

	@Override
	public Actor build(BaseModel model, Group parent) {
		ViewModel viewModel = (ViewModel) model;
		
		Class<?> klass;
		try {
			klass = Class.forName(viewModel.getKlass());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not find class for '" + viewModel.getKlass() +"'", e);
		}

		CustomView view;
		try {
			view = (CustomView) klass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not instantiate '" + viewModel.getKlass() +"'", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Provided class for view has no public parameterless constructor: '" + viewModel.getKlass() +"'", e);
		}
		
		normalizeModelSize(model, parent, model.getWidth(), model.getHeight());
		setBasicProperties(model, view);
	
		if (viewModel.getLayout() != null) {
			try {
				List<BaseModel> children = builder.getBaseModelList(viewModel.getLayout());
				GroupModel rootGroup = (GroupModel) children.get(0);
				viewModel.setChildren(rootGroup.getChildren());
				Group group = builder.buildGroup(viewModel);

				SnapshotArray<Actor> proxyChildren = group.getChildren();
				int proxySize = proxyChildren.size;
				Actor[] stored = proxyChildren.begin();
				for (int i = 0; i < proxySize; i++) {
					view.addActor(stored[i]);
				}
				proxyChildren.end();
			} catch (Exception e) {
				throw new RuntimeException("Could not build given layout: " + viewModel.getLayout(), e);
			}
		}

		try {
			view.build(viewModel.getAttrs(), assets, resolutionHelper, localizationService);
		} catch (Exception e) {
			throw new RuntimeException("Exception in custom view's build method: " + viewModel.getName(), e);
		}

		return (Actor) view;
	}
}
