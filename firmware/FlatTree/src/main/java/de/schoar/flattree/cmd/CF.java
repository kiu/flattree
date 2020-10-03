package de.schoar.flattree.cmd;

import java.awt.Color;

import de.schoar.flattree.FlatTreeApplication;
import de.schoar.flattree.addr.A;

public class CF extends CBase { // FADE

	private float[] _speedsR = new float[A.LEDS];
	private float[] _speedsG = new float[A.LEDS];
	private float[] _speedsB = new float[A.LEDS];

	private float[] _valueR = new float[A.LEDS];
	private float[] _valueG = new float[A.LEDS];
	private float[] _valueB = new float[A.LEDS];

	public CF(long mark, long duration, A addr, Color c) {
		super(mark, duration, addr, c);
	}

	@Override
	protected void started() {
		float ticks = (float) Math.max(1, _duration / FlatTreeApplication.TICK);

		for (Integer addr : _addr.getAddr()) {
			_valueR[addr] = (float) _froms[addr].getRed();
			_valueG[addr] = (float) _froms[addr].getGreen();
			_valueB[addr] = (float) _froms[addr].getBlue();

			_speedsR[addr] = ((float) (_c.getRed() - _froms[addr].getRed())) / ticks;
			_speedsG[addr] = ((float) (_c.getGreen() - _froms[addr].getGreen())) / ticks;
			_speedsB[addr] = ((float) (_c.getBlue() - _froms[addr].getBlue())) / ticks;
		}
	}

	@Override
	protected void ticked(long leaped) {
		for (Integer addr : _addr.getAddr()) {
			_valueR[addr] = _valueR[addr] + _speedsR[addr];
			_valueG[addr] = _valueG[addr] + _speedsG[addr];
			_valueB[addr] = _valueB[addr] + _speedsB[addr];

			int r = Math.min(255, Math.max(0, (int) _valueR[addr]));
			int g = Math.min(255, Math.max(0, (int) _valueG[addr]));
			int b = Math.min(255, Math.max(0, (int) _valueB[addr]));

			FlatTreeApplication.PIXELS[addr].setColor(new Color(r, g, b));
		}
	}

	@Override
	protected void finished() {
		for (Integer addr : _addr.getAddr()) {
			FlatTreeApplication.PIXELS[addr].setColor(_c);
		}
	}

}
