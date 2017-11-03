package net.bbmsoft.fxtended.fxml;

import java.util.ArrayList;
import java.util.List;

import net.bbmsoft.fxtended.fxml.inject.InjectFXML;

@InjectFXML
public class AdditionImpl implements Addition {

	private double a;

	private double b;

	private List<Double> args = new ArrayList<>();

	@Override
	public double getResult() {
		double sum = 0;
		for (final Double d : this.args) {
			sum += d;
		}
		sum += (this.a + this.b);
		return sum;
	}

	public double getA() {
		return this.a;
	}

	public void setA(final double a) {
		this.a = a;
	}

	public double getB() {
		return this.b;
	}

	public void setB(final double b) {
		this.b = b;
	}

	public List<Double> getArgs() {
		return this.args;
	}
}
