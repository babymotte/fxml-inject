package net.bbmsoft.fxtended.fxml;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.util.BuilderFactory;

public class CustomFXMLLoader extends FXMLLoader {
	
	@Inject
	protected CustomFXMLLoader(final BuilderFactory builderFactory) {
		this.setBuilderFactory(builderFactory);
	}
}
