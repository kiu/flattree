package de.schoar.flattree;

import java.awt.Color;

public class Pixel {

	private Color c = Color.BLACK;

	public Color getColor() {
		return c;
	}

	public void setColor(Color c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return "#" + tohex(c.getRed()) + tohex(c.getGreen()) + tohex(c.getBlue());
	}

	public String toC() {
		return "0x" + tohex(c.getRed()) + ", 0x" + tohex(c.getGreen()) + ", 0x" + tohex(c.getBlue()) + ", ";
	}

	private String tohex(int i) {
		String s = Integer.toHexString(i);
		if (s.length() == 1) {
			return "0" + s;
		}
		return s;
	}
}
