package net.bbmsoft.fxtended.fxml;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.util.BuilderFactory;

public class GuiceFXMLLoader extends CustomFXMLLoader {
	
	@Inject
	protected GuiceFXMLLoader(final BuilderFactory builderFactory, final Injector injector) {

		super(builderFactory);
		
		this.setControllerFactory(clazz -> injector.getInstance(clazz) );
	}
}
