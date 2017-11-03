package net.bbmsoft.fxtended.fxml.inject;

import com.google.inject.Binder;
import com.google.inject.Module;

import javafx.fxml.FXMLLoader;
import net.bbmsoft.fxtended.fxml.CustomFXMLLoader;

public class CustomFXMLLoaderModule implements Module {
	
	@Override
	public void configure(final Binder binder) {
		binder.bind(FXMLLoader.class).to(CustomFXMLLoader.class);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
