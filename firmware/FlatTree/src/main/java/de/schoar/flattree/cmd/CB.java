package de.schoar.flattree.cmd;

import java.awt.Color;

import de.schoar.flattree.FlatTreeApplication;
import de.schoar.flattree.addr.A;

public class CB extends CBase { // BLINK

	private long _upHold;
	private long _downHold;

	private long _ticks;
	private long _upHoldTicks;
	private long _downHoldTicks;

	private MODE _mode;

	private enum MODE {
		UP, UP_HOLD, DOWN, DOWN_HOLD
	};

	public CB(long mark, long duration, A addr, Color c, long upHold, long downHold) {
		super(mark, duration, addr, c);
		_upHold = upHold;
		_downHold = downHold;
	}

	@Override
	protected void started() {
		_upHoldTicks = Math.max(1, _upHold / FlatTreeApplication.TICK);
		_downHoldTicks = Math.max(1, _downHold / FlatTreeApplication.TICK);
		_ticks = 0;
		_mode = MODE.UP;
	}

	@Override
	protected void ticked(long leaped) {
		_ticks = _ticks + 1;

		if (MODE.UP.equals(_mode)) {
			for (Integer addr : _addr.getAddr()) {
				FlatTreeApplication.PIXELS[addr].setColor(_c);
			}
			_mode = MODE.UP_HOLD;
			_ticks = 0;
			return;
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
				FlatTreeApplication.PIXELS[addr].setColor(_froms[addr]);
			}
			_mode = MODE.DOWN_HOLD;
			_ticks = 0;
			return;
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
		for (Integer addr : _addr.getAddr()) {
			FlatTreeApplication.PIXELS[addr].setColor(_c);
		}
	}

}
