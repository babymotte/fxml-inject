package net.bbmsoft.fxtended.fxml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import com.google.inject.Injector;

import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import net.bbmsoft.fxtended.fxml.inject.InjectFXML;

public class GuiceBuilderFactory implements BuilderFactory {

	private static final Map<Class<?>, Builder<?>> builders = new HashMap<>();

	private final Injector injector;
	private final BuilderFactory fallbackFactory;
	private final Set<Class<?>> injectableClasses;

	public GuiceBuilderFactory(Injector injector, BuilderFactory delegate) {

		this.injector = injector;
		this.fallbackFactory = delegate;
		this.injectableClasses = ConcurrentHashMap.newKeySet();
	}
	
	@Inject
	private GuiceBuilderFactory(Injector injector) {

		this.injector = injector;
		this.fallbackFactory = new JavaFXBuilderFactory();
		this.injectableClasses = ConcurrentHashMap.newKeySet();
	}
	
	public void registerInjectableTypes(Class<?> ... types) {
		for(Class<?> type : types) {
			this.injectableClasses.add(type);
		}
	}
	
	public void registerInjectableTypes(Iterable<Class<?>> types) {
		for(Class<?> type : types) {
			this.injectableClasses.add(type);
		}
	}

	@Override
	public Builder<?> getBuilder(Class<?> type) {

		if (canInject(type)) {
			Builder<?> builder = builders.get(type);
			if (builder == null) {
				builder = new GuiceBuilder<>(injector, type);
				builders.put(type, builder);
			}
			return builder;
		} else {
			return fallbackFactory.getBuilder(type);
		}
	}

	private boolean canInject(Class<?> type) {
		
		if(type.isAnnotationPresent(InjectFXML.class)) {
			return true;
		}
		
		if(this.injectableClasses.contains(type)) {
			return true;
		}
		
		return false;
	}
}
