package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.Gdx;
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
		try {
			ViewModel viewModel = (ViewModel) model;
			Class<?> klass = Class.forName(viewModel.getKlass());
			CustomView view = (CustomView) klass.newInstance();

			setBasicProperties(model, view);
			Group children = builder.buildGroup(viewModel.getLayout());
			view.addActor(children);
			view.build(assets, resolutionHelper, localizationService);

			return (Actor) view;
		} catch (Exception e) {
			Gdx.app.log("GdxWidgets", "Failed to create custom view.", e);
			return null;
		}
	}
}
