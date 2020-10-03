package de.schoar.flattree.cmd;

import java.awt.Color;

import de.schoar.flattree.FlatTreeApplication;
import de.schoar.flattree.addr.A;

public class CX extends CBase { // CROSSFADE

	private long _upSpeed;
	private long _upHold;
	private long _downSpeed;
	private long _downHold;

	private long _ticks;
	private long _upTicks;
	private long _upHoldTicks;
	private long _downTicks;
	private long _downHoldTicks;

	private float[] _speedsUpR = new float[A.LEDS];
	private float[] _speedsUpG = new float[A.LEDS];
	private float[] _speedsUpB = new float[A.LEDS];

	private float[] _speedsDownR = new float[A.LEDS];
	private float[] _speedsDownG = new float[A.LEDS];
	private float[] _speedsDownB = new float[A.LEDS];

	private float[] _valueR = new float[A.LEDS];
	private float[] _valueG = new float[A.LEDS];
	private float[] _valueB = new float[A.LEDS];

	private MODE _mode;

	private enum MODE {
		UP, UP_HOLD, DOWN, DOWN_HOLD
	};

	public CX(long mark, long duration, A addr, Color c, long upSpeed, long upHold, long downSpeed, long downHold) {
		super(mark, duration, addr, c);
		_upSpeed = upSpeed;
		_upHold = upHold;
		_downSpeed = downSpeed;
		_downHold = downHold;
	}

	@Override
	protected void started() {
		_upTicks = Math.max(1, _upSpeed / FlatTreeApplication.TICK);
		_upHoldTicks = Math.max(1, _upHold / FlatTreeApplication.TICK);
		_downTicks = Math.max(1, _downSpeed / FlatTreeApplication.TICK);
		_downHoldTicks = Math.max(1, _downHold / FlatTreeApplication.TICK);

		for (Integer addr : _addr.getAddr()) {
			_valueR[addr] = (float) _froms[addr].getRed();
			_valueG[addr] = (float) _froms[addr].getGreen();
			_valueB[addr] = (float) _froms[addr].getBlue();

			_speedsUpR[addr] = ((float) (_c.getRed() - _froms[addr].getRed())) / (float) _upTicks;
			_speedsUpG[addr] = ((float) (_c.getGreen() - _froms[addr].getGreen())) / (float) _upTicks;
			_speedsUpB[addr] = ((float) (_c.getBlue() - _froms[addr].getBlue())) / (float) _upTicks;

			_speedsDownR[addr] = ((float) (_froms[addr].getRed() - _c.getRed())) / (float) _downTicks;
			_speedsDownG[addr] = ((float) (_froms[addr].getGreen() - _c.getGreen())) / (float) _downTicks;
			_speedsDownB[addr] = ((float) (_froms[addr].getBlue() - _c.getBlue())) / (float) _downTicks;
		}

		_ticks = 0;
		_mode = MODE.UP;
	}

	@Override
	protected void ticked(long leaped) {
		_ticks = _ticks + 1;

		if (MODE.UP.equals(_mode)) {
			for (Integer addr : _addr.getAddr()) {
				_valueR[addr] = _valueR[addr] + _speedsUpR[addr];
				_valueG[addr] = _valueG[addr] + _speedsUpG[addr];
				_valueB[addr] = _valueB[addr] + _speedsUpB[addr];

				int r = Math.min(255, Math.max(0, (int) _valueR[addr]));
				int g = Math.min(255, Math.max(0, (int) _valueG[addr]));
				int b = Math.min(255, Math.max(0, (int) _valueB[addr]));

				FlatTreeApplication.PIXELS[addr].setColor(new Color(r, g, b));
			}
			if (_ticks >= _upTicks) {
				_mode = MODE.UP_HOLD;
				_ticks = 0;
				for (Integer addr : _addr.getAddr()) {
					_valueR[addr] = (float) _c.getRed();
					_valueG[addr] = (float) _c.getGreen();
					_valueB[addr] = (float) _c.getBlue();
					FlatTreeApplication.PIXELS[addr].setColor(_c);
				}
				return;
			}
		}

		if (MODE.UP_HOLD.equals(_mode)) {
			if (_ticks >= _upHoldTicks) {
				_mode = MODE.DOWN;
				_ticks = 0;
				return;
			}
		}

		if (MODE.DOWN.equals(_mode)) {
			for (Integer addr : _addr.getAddr()) {
				_valueR[addr] = _valueR[addr] + _speedsDownR[addr];
				_valueG[addr] = _valueG[addr] + _speedsDownG[addr];
				_valueB[addr] = _valueB[addr] + _speedsDownB[addr];

				int r = Math.min(255, Math.max(0, (int) _valueR[addr]));
				int g = Math.min(255, Math.max(0, (int) _valueG[addr]));
				int b = Math.min(255, Math.max(0, (int) _valueB[addr]));

				FlatTreeApplication.PIXELS[addr].setColor(new Color(r, g, b));
			}
			if (_ticks >= _downTicks) {
				_mode = MODE.DOWN_HOLD;
				_ticks = 0;
				for (Integer addr : _addr.getAddr()) {
					_valueR[addr] = (float) _froms[addr].getRed();
					_valueG[addr] = (float) _froms[addr].getGreen();
					_valueB[addr] = (float) _froms[addr].getBlue();
					FlatTreeApplication.PIXELS[addr].setColor(_froms[addr]);
				}
				return;
			}
		}

		if (MODE.DOWN_HOLD.equals(_mode)) {
			if (_ticks >= _downHoldTicks) {
				_mode = MODE.UP;
				_ticks = 0;
				return;
			}
		}
	}

	@Override
	protected void finished() {
	}

}
