package net.bbmsoft.fxtended.fxml;

import javafx.collections.ObservableList;
import net.bbmsoft.fxtended.fxml.inject.InjectFXML;

@InjectFXML
@SuppressWarnings("all")
public interface Printer {
  public abstract void setText(final String text);
  
  public abstract ObservableList<String> getTexts();
  
  public abstract String getText();
  
  public abstract void print();
}
