package net.bbmsoft.fxtended.fxml;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Injector;
import com.sun.javafx.fxml.BeanAdapter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Builder;

@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public class GuiceBuilder<T> extends AbstractMap<String, Object> implements Builder<T> {

	private final Injector injector;
	private final Class<T> type;
	private final Map<String, Object> containers;
	private final Map<String, Method> setters;
	private final Map<String, Method> getters;

	private Map<Object, Object> properties;

	public GuiceBuilder(Injector injector, Class<T> type) {

		this.injector = injector;
		this.type = type;

		this.properties = new HashMap<>();
		this.containers = new HashMap<>();
		this.setters = new HashMap<>();
		this.getters = new HashMap<>();
	}

	@Override
	public T build() {

		this.containers.forEach((k, v) -> put(k, v));

		T instance = this.injector.getInstance(type);

		if (instance instanceof Node) {
			((Node) instance).getProperties().putAll(properties);
		}

		try {
			for (Entry<Object, Object> entry : this.properties.entrySet()) {

				Object key = entry.getKey();
				Object value = entry.getValue();

				if (value instanceof List<?>) {
					List<?> list = (List<?>) getters.get(key).invoke(instance);
					list.addAll((List) value);
				} else if (value instanceof Map<?, ?>) {
					Map<?, ?> map = (Map<?, ?>) getters.get(key).invoke(instance);
					map.putAll((Map) value);
				} else {
					Method method = this.setters.get(key);
					if(method != null) {
						method.invoke(instance, value);
					} else {
						throw new RuntimeException(new NoSuchMethodException(type.getName() + " has no method called " + key));
					}
				}
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		properties.clear();
		containers.clear();
		setters.clear();
		getters.clear();

		return instance;
	}

	@Override
	public Object put(String key, Object value) {

		if (Node.class.isAssignableFrom(type) && "properties".equals(key)) {
			properties = (Map<Object, Object>) value;
			return null;
		}

		try {

			getReadOnlyProperty(key);

			Method setter = setters.get(key);
			Method getter = getters.get(key);

			Type type = null;
			if (setter != null) {
				type = setter.getGenericParameterTypes()[0];
			} else if (getter != null) {
				type = getter.getGenericReturnType();
			}
			ParameterizedType paramType = type instanceof ParameterizedType ? (ParameterizedType) type : null;
			Class<?> rawType = (Class<?>) (paramType != null ? paramType.getRawType() : type);

			if (type != null) {
				try {
					Object theValue;
					if (rawType.isArray()) {
						List<?> list = Arrays.asList(value.toString().split(FXMLLoader.ARRAY_COMPONENT_DELIMITER));
						Class<?> componentType = rawType.getComponentType();
						Object array = Array.newInstance(componentType, list.size());
						for (int i = 0; i < list.size(); i++) {
							Array.set(array, i, BeanAdapter.coerce(list.get(i), componentType));
						}
						theValue = array;
					} else if (List.class.isAssignableFrom(rawType)) {
						List<Object> list = (List<Object>) getInstance(rawType);
						Class<?> componentType = getGenericCollectionTypeArgument(getter);
						if (componentType != null) {
							for (Object e : ((List<?>) value)) {
								list.add(BeanAdapter.coerce(e, componentType));
							}
							theValue = list;
						} else {
							theValue = value;
						}
					} else {
						theValue = value;
					}

					Object coercedValue = BeanAdapter.coerce(theValue, rawType);

					properties.put(key, coercedValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Object getInstance(Class<?> type) {

		if (ObservableList.class.isAssignableFrom(type)) {
			return FXCollections.observableArrayList();
		}
		if (ObservableSet.class.isAssignableFrom(type)) {
			return FXCollections.observableSet();
		}
		if (ObservableMap.class.isAssignableFrom(type)) {
			return FXCollections.observableHashMap();
		}
		if (List.class.isAssignableFrom(type)) {
			return new ArrayList<>();
		}
		if (Set.class.isAssignableFrom(type)) {
			return new HashSet<>();
		}
		if (Map.class.isAssignableFrom(type)) {
			return new HashMap<>();
		}

		try {
			return this.injector.getInstance(type);
		} catch (Throwable th) {
			return null;
		}
	}

	private Class<?> getGenericCollectionTypeArgument(Method getter) {
		ParameterizedType rett = (ParameterizedType) getter.getGenericReturnType();
		return (Class<?>) rett.getActualTypeArguments()[0];
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return getTemporaryContainer(key.toString()) != null;
	}

	@Override
	public Object get(Object key) {
		return getTemporaryContainer(key.toString());
	}

	/**
	 * This is used to support read-only collection property. This method must
	 * return a Collection of the appropriate type if 1. the property is read-only,
	 * and 2. the property is a collection. It must return null otherwise.
	 **/
	private Object getTemporaryContainer(String propName) {

		Object o = containers.get(propName);

		if (o == null) {
			o = getReadOnlyProperty(propName);
		}

		if (o instanceof Iterable<?>) {
			containers.put(propName, o);
		}
		return o;
	}

	private Object getReadOnlyProperty(String propName) {

		if (setters.get(propName) != null) {
			return null;
		}

		Method getter = getters.get(propName);

		if (getter == null) {

			Method setter = null;
			String suffix = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);

			try {
				getter = type.getMethod(String.format("get%s", suffix));
				setter = type.getMethod(String.format("set%s", suffix), getter.getReturnType());
			} catch (Exception x) {
			}
			if (getter != null) {
				getters.put(propName, getter);
				setters.put(propName, setter);
			}
			if (setter != null)
				return null;
		}

		Class<?> type;
		if (getter == null) {
			Method m = findMethod(propName);
			if (m == null) {
				return null;
			}
			type = m.getParameterTypes()[0];
			if (type.isArray())
				type = List.class;
		} else {
			type = getter.getReturnType();
		}

		if (ObservableMap.class.isAssignableFrom(type)) {
			return FXCollections.observableMap(new HashMap<Object, Object>());
		} else if (Map.class.isAssignableFrom(type)) {
			return new HashMap<Object, Object>();
		} else if (ObservableList.class.isAssignableFrom(type)) {
			return FXCollections.observableArrayList();
		} else if (List.class.isAssignableFrom(type)) {
			return new ArrayList<Object>();
		} else if (Set.class.isAssignableFrom(type)) {
			return new HashSet<Object>();
		}
		return null;
	}

	private Method findMethod(String name) {

		String theName;
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
			theName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		} else {
			theName = name;
		}

		for (Method m : type.getMethods()) {
			if (m.getName().equals(theName)) {
				return m;
			}
		}
		throw new IllegalArgumentException(
				String.format("Method %s could not be found at class %s", theName, type.getSimpleName()));
	}

}
