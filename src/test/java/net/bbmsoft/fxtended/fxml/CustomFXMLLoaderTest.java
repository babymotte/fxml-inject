package net.bbmsoft.fxtended.fxml;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.fxml.FXMLLoader;
import net.bbmsoft.fxtended.fxml.inject.GuiceFXMLLoaderModule;

public class CustomFXMLLoaderTest {

	private Injector injector;

	@Before
	public void setUp() {

		final Module testModule = new Module() {
			@Override
			public void configure(final Binder binder) {
				binder.bind(Printer.class).to(PrinterImpl.class);
				binder.bind(Addition.class).to(AdditionImpl.class);
			}
		};

		GuiceFXMLLoaderModule guiceFXMLLoaderModule = new GuiceFXMLLoaderModule();

		this.injector = Guice.createInjector(Arrays.asList(guiceFXMLLoaderModule, testModule));
	}

	@Test
	public void testLoadClass() throws IOException {

		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/LoadTestClass.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Printer printer = loader.load();

		Assert.assertTrue((printer instanceof PrinterImpl));
	}

	@Test
	public void testSetClass() throws IOException {
		
		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/SetTestClass.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Printer printer = loader.load();
		
		Assert.assertEquals(printer.getText(), "Hello World");
	}

	@Test
	public void testGetClass() throws IOException {
		
		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/GetTestClass.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Printer printer = loader.load();
		
		Assert.assertEquals(3, printer.getTexts().size());
		Assert.assertTrue(printer.getTexts().contains("Hello"));
		Assert.assertTrue(printer.getTexts().contains("World"));
		Assert.assertTrue(printer.getTexts().contains("!"));
	}

	@Test
	public void testLoadInterface() throws IOException {
		
		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/LoadTest.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Printer printer = loader.load();
		
		Assert.assertTrue((printer instanceof PrinterImpl));
	}

	@Test
	public void testSetInterface() throws IOException {
		
		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/SetTest.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Printer printer = loader.load();
		
		Assert.assertEquals(printer.getText(), "Hello World");
	}

	@Test
	public void testGetInterface() throws IOException {
		
		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/GetTest.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Printer printer = loader.load();
		
		Assert.assertEquals(3, printer.getTexts().size());
		Assert.assertTrue(printer.getTexts().contains("Hello"));
		Assert.assertTrue(printer.getTexts().contains("World"));
		Assert.assertTrue(printer.getTexts().contains("!"));
	}

	@Test
	public void testLoadDouble() throws IOException {
		
		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/LoadDoubleTest.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Addition add = loader.load();
		
		Assert .assertEquals(1.1, add.getResult(), 0.01);
	}

	@Test
	public void testGetDouble() throws IOException {
		
		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/GetDoubleTest.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Addition add = loader.load();
		
		Assert .assertEquals(6.6, add.getResult(), 0.01);
	}

	@Test
	public void testNonGuiceDoubleList() throws IOException {
		
		final URL resource = CustomFXMLLoaderTest.class.getResource("/layout/GetDoubleTestNoGuice.fxml");
		FXMLLoader loader = this.injector.getInstance(FXMLLoader.class);
		loader.setLocation(resource);
		final Addition add = loader.load();
		
		Assert .assertEquals(6.6, add.getResult(), 0.01);
	}
}
