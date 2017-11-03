package net.bbmsoft.fxtended.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.bbmsoft.fxtended.fxml.inject.InjectFXML;

@InjectFXML
public class PrinterImpl implements Printer {

	private final ObservableList<String> texts = FXCollections.<String>observableArrayList();

	private String text;

	@Override
	public void print() {
		System.out.println(this.text);
	}

	@Override
	public ObservableList<String> getTexts() {
		return this.texts;
	}

	public String getText() {
		return this.text;
	}

	public void setText(final String text) {
		this.text = text;
	}
}
