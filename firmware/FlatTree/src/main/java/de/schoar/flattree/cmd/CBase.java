package de.schoar.flattree.cmd;

import java.awt.Color;

import de.schoar.flattree.FlatTreeApplication;
import de.schoar.flattree.addr.A;

public abstract class CBase {

	protected long _mark;
	protected long _duration;
	protected A _addr;
	protected Color _c;

	protected boolean _started = false;
	protected long _started_at = 0;
	protected boolean _finished = false;

	protected Color[] _froms = new Color[A.LEDS];

	protected CBase(long mark, long duration, A addr, Color c) {
		_mark = mark;
		_duration = duration;
		_addr = addr;
		_c = c;
	}

	public void tick(long time) {
		if (_finished) {
			return;
		}

		if (!_started) {
			if (time < _mark) {
				return;
			}
			_started = true;
			_started_at = time;

			for (Integer addr : _addr.getAddr()) {
				_froms[addr] = FlatTreeApplication.PIXELS[addr].getColor();
			}

			started();
		}

		if (time - _started_at >= _duration) {
			finish();
		} else {
			ticked(time - _started_at);
		}
	}

	protected void finish() {
		_finished = true;
		finished();
	}

	protected abstract void started();

	protected abstract void ticked(long leaped);

	protected abstract void finished();
}
