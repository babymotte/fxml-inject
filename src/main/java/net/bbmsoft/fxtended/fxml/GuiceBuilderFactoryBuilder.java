package net.bbmsoft.fxtended.fxml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.util.BuilderFactory;

public class GuiceBuilderFactoryBuilder {
	
	private final List<Module> modules;
	private final List<Class<?>> injectableClasses;
	private final BuilderFactory fallbackFactory;

	private GuiceBuilderFactoryBuilder(BuilderFactory fallbackFactory) {
		this.fallbackFactory = fallbackFactory;
		this.modules = new ArrayList<>();
		this.injectableClasses = new ArrayList<>();
	}

	public static GuiceBuilderFactoryBuilder create(BuilderFactory fallbackFactory) {
		return new GuiceBuilderFactoryBuilder(fallbackFactory);
	}
	
	public GuiceBuilderFactoryBuilder addModules(Module ... modules) {
		return this.addModules(Arrays.asList(modules));
	}

	public GuiceBuilderFactoryBuilder addModules(Iterable<Module> modules) {
		for (Module module : modules) {
			this.modules.add(module);
		}
		return this;
	}
	
	public GuiceBuilderFactoryBuilder registerInjectableClasses(Class<?> ... classes) {
		return this.registerInjectableCLasses(Arrays.asList(classes));
	}

	public GuiceBuilderFactoryBuilder registerInjectableCLasses(Iterable<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			this.injectableClasses.add(clazz);
		}
		return this;
	}
	
	public GuiceBuilderFactory build() {
		Injector injector = Guice.createInjector(this.modules);
		GuiceBuilderFactory guiceBuilderFactory = new GuiceBuilderFactory(injector, fallbackFactory);
		guiceBuilderFactory.registerInjectableTypes(this.injectableClasses);
		return guiceBuilderFactory;
	}
}
