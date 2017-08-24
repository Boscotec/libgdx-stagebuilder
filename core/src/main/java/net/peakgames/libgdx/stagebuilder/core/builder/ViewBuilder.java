package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import net.peakgames.libgdx.stagebuilder.core.CustomView;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.ViewModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

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
		
		normalizeModelSize(model, model.getWidth(), model.getHeight());
		setBasicProperties(model, view);
	
		if (viewModel.getLayout() != null) {
			try {
				Group children = builder.buildGroup(viewModel.getLayout());
				view.addActor(children);
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
