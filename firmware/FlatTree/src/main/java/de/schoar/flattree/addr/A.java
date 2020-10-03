package de.schoar.flattree.addr;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class A {

	public static final int LEDS = 204;
	public static final int HALF = LEDS / 2;

	private List<Integer> _addrs = new LinkedList<Integer>();

	public static A c(AT... types) {
		return new A(types);
	}

	public static A c(boolean mirror, Integer... addrs) {
		return new A(mirror, addrs);
	}

	public static A r(boolean mirror, int amount) {
		Random rnd = new Random();
		Integer[] addrs = new Integer[amount];
		for (int i = 0; i < amount; i++) {
			addrs[i] = rnd.nextInt(A.LEDS);
		}
		return new A(mirror, addrs);
	}

	private A(AT... types) {

		for (int i = 0; i < A.LEDS; i++) {
			_addrs.add(i);
		}

		List<Integer> sub = new LinkedList<Integer>();

		for (AT type : types) {
			if (AT.CORNER_PEAK.equals(type)) { // OK
				sub.clear();
				sub.add((LEDS / 2) - 1);
				sub.add((LEDS / 2));
				_addrs.retainAll(sub);
			}
			if (AT.CORNER_OUTER.equals(type)) { // OK
				sub.clear();
				sub.add(4 + 10 + 14);
				sub.add(4 + 10 + 14 - 1);
				
				sub.add(4 + 10 + 14 + 18 + 8);
				sub.add(4 + 10 + 14 + 18 + 8 - 1);
				
				sub.add(4 + 10 + 14 + 18 + 8 + 20 + 8);
				sub.add(4 + 10 + 14 + 18 + 8 + 20 + 8 - 1);

				sub.add(LEDS - (4 + 10 + 14));
				sub.add(LEDS - (4 + 10 + 14) - 1);
				
				sub.add(LEDS - (4 + 10 + 14 + 18 + 8));
				sub.add(LEDS - (4 + 10 + 14 + 18 + 8) - 1);
				
				sub.add(LEDS - (4 + 10 + 14 + 18 + 8 + 20 + 8));
				sub.add(LEDS - (4 + 10 + 14 + 18 + 8 + 20 + 8) - 1);
				_addrs.retainAll(sub);
			}
			if (AT.CORNER_INNER.equals(type)) { // OK
				sub.clear();
				sub.add(4 + 10);
				
				sub.add(4 + 10 + 14 + 18 - 1);
				sub.add(4 + 10 + 14 + 18);
				
				sub.add(4 + 10 + 14 + 18 + 8 + 20 - 1);
				sub.add(4 + 10 + 14 + 18 + 8 + 20);

				sub.add(LEDS - (4 + 10 + 1));
				
				sub.add(LEDS - (4 + 10 + 14 + 18) - 1);
				sub.add(LEDS - (4 + 10 + 14 + 18));
				
				sub.add(LEDS - (4 + 10 + 14 + 18 + 8 + 20) - 1);
				sub.add(LEDS - (4 + 10 + 14 + 18 + 8 + 20));
				_addrs.retainAll(sub);
			}
			if (AT.SECTION_TOP.equals(type)) { // OK
				sub.clear();
				for (int i = 4 + 10 + 14 + 18 + 8 + 20; i < 4 + 10 + 14 + 18 + 8 + 20 + 8 + 20; i++) {
					sub.add(i);
					sub.add(LEDS - i - 1);
				}
				_addrs.retainAll(sub);
			}
			if (AT.SECTION_MIDDLE.equals(type)) { // OK
				sub.clear();
				for (int i = 4 + 10 + 14 + 18; i < 4 + 10 + 14 + 18 + 8 + 20; i++) {
					sub.add(i);
					sub.add(LEDS - i - 1);
				}
				_addrs.retainAll(sub);
			}
			if (AT.SECTION_BOTTOM.equals(type)) { // OK
				sub.clear();
				for (int i = 4 + 10; i < 4 + 10 + 14 + 18; i++) {
					sub.add(i);
					sub.add(LEDS - i - 1);
				}
				_addrs.retainAll(sub);
			}
			if (AT.SECTION_TRUNK.equals(type)) { // OK
				sub.clear();
				for (int i = 0; i < 4 + 10; i++) {
					sub.add(i);
					sub.add(LEDS - i - 1);
				}
				_addrs.retainAll(sub);
			}
			if (AT.SIDE_LEFT.equals(type)) { // OK
				sub.clear();
				for (int i = 0; i < A.HALF; i++) {
					sub.add(i);
				}
				_addrs.retainAll(sub);
			}
			if (AT.SIDE_RIGHT.equals(type)) { // OK
				sub.clear();
				for (int i = A.HALF; i < A.LEDS; i++) {
					sub.add(i);
				}
				_addrs.retainAll(sub);
			}
		}
	}

	private A(boolean mirror, Integer... addrs) {
		_addrs = new LinkedList<Integer>(Arrays.asList(addrs));
		if (mirror) {
			for (Integer addr : addrs) {
				_addrs.add(A.LEDS - addr - 1);
			}
		}
	}

	public List<Integer> getAddr() {
		return _addrs;
	}

}
