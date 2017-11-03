package net.bbmsoft.fxtended.fxml.inject;

import com.google.inject.Binder;
import com.google.inject.Module;

import javafx.fxml.FXMLLoader;
import javafx.util.BuilderFactory;
import net.bbmsoft.fxtended.fxml.GuiceBuilderFactory;
import net.bbmsoft.fxtended.fxml.GuiceFXMLLoader;

public class GuiceFXMLLoaderModule implements Module {
	
	@Override
	public void configure(final Binder binder) {
		binder.bind(FXMLLoader.class).to(GuiceFXMLLoader.class);
		binder.bind(BuilderFactory.class).to(GuiceBuilderFactory.class);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
