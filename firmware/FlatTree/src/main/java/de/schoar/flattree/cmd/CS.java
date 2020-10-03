package de.schoar.flattree.cmd;

import java.awt.Color;

import de.schoar.flattree.FlatTreeApplication;
import de.schoar.flattree.addr.A;

public class CS extends CBase { // SET

	public CS(long mark, long duration, A addr, Color c) {
		super(mark, duration, addr, c);
	}

	@Override
	protected void started() {
	}

	@Override
	protected void ticked(long leaped) {
	}

	@Override
	protected void finished() {
		for (Integer addr : _addr.getAddr()) {
			FlatTreeApplication.PIXELS[addr].setColor(_c);
		}
	}

}
