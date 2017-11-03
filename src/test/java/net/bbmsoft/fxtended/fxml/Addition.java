package net.bbmsoft.fxtended.fxml;

import java.util.List;
import net.bbmsoft.fxtended.fxml.inject.InjectFXML;

@InjectFXML
public interface Addition {
	
	public abstract void setA(final double a);

	public abstract void setB(final double b);

	public abstract double getA();

	public abstract double getB();

	public abstract List<Double> getArgs();

	public abstract double getResult();
}
