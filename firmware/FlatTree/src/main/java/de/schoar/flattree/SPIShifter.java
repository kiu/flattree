package de.schoar.flattree;

import java.awt.Color;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import de.schoar.flattree.addr.A;
import de.schoar.flattree.addr.AT;
import de.schoar.flattree.cmd.CBase;
import de.schoar.flattree.cmd.CF;
import de.schoar.flattree.cmd.CS;

@Service
public class SPIShifter extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(SPIShifter.class);

	private List<CBase> _cmds = new LinkedList<CBase>();

	@Autowired
	private ApplicationContext _appContext;

	private long _d = 0;

	private boolean _running = true;
	private Boolean _poweroff = false;

	private SpiDevice _spi = null;
	private GpioController _gpio = null;

	@PostConstruct
	private void init() {
		initSPI();
		start();
	}

	private void initSPI() {
		if (FlatTreeApplication.PI_EXISTS) {
			try {
				_spi = SpiFactory.getInstance(SpiChannel.CS0, 1000000, SpiDevice.DEFAULT_SPI_MODE);
			} catch (IOException e) {
				LOG.error("Could not initialize SPI!", e);
				SpringApplication.exit(_appContext, (ExitCodeGenerator) () -> 23);
				return;
			}

			_gpio = GpioFactory.getInstance();
			GpioPinDigitalInput pin_mode = _gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.OFF);
			pin_mode.addListener(new GpioPinListenerDigital() {
				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
					if (event.getEdge().getValue() == 3) {
						LOG.info("Poweroff by button event");
						poweroff();
					}
				}
			});
		}
	}

	private void initCommands() {
		_cmds.clear();
		_d = 0;

//		_cmds.add(new CS(0, 0, A.c(AT.ALL), Color.BLACK));
//		_d = 5000;

		List<Color> single = new LinkedList<Color>();
		single.add(Color.GREEN);
		single.add(Color.RED);
		single.add(Color.BLUE);
		single.add(Color.CYAN);
		single.add(Color.MAGENTA);
		single.add(Color.ORANGE);
		Color c1;
		Color c2;
		Color c3;
		LinkedList<Color> selection;

		// ------------------------ Fade all

		Collections.shuffle(single);
		c1 = single.get(0);

		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(_d += 30, 3000, A.c(true, i), c1));
		}
		_d += 3000;

		// ------------------------ Filled chase

		selection = new LinkedList<Color>(single);
		selection.remove(c1);

		Collections.shuffle(selection);
		c1 = selection.get(0);
		c2 = selection.get(1);
		c3 = selection.get(2);

		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(_d += 30, 3000, A.c(true, i), c1));
		}
		_d += 3000;

		long cur = _d;
		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(cur, 300, A.c(true, i), c2));
			_cmds.add(new CF(cur + 700, 300, A.c(true, i), c1));
			cur += 30;
		}
		cur = _d + 1500;
		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(cur, 300, A.c(true, i), c3));
			_cmds.add(new CF(cur + 700, 300, A.c(true, i), c1));
			cur += 30;
		}
		cur = _d + 3000;
		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(cur, 300, A.c(true, i), c2));
			_cmds.add(new CF(cur + 700, 300, A.c(true, i), c1));
			cur += 30;
		}
		cur = _d + 4500;
		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(cur, 300, A.c(true, i), c3));
			_cmds.add(new CF(cur + 700, 300, A.c(true, i), c1));
			cur += 30;
		}
		cur = _d + 6000;
		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(cur, 300, A.c(true, i), c2));
			_cmds.add(new CF(cur + 700, 300, A.c(true, i), c1));
			cur += 30;
		}
		_d = cur;
		_d += 1000;

		// ------------------------ Fade all

		selection = new LinkedList<Color>(single);
		selection.remove(c1);
		selection.remove(c2);
		selection.remove(c3);

		Collections.shuffle(selection);
		c1 = selection.get(0);

		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(_d += 30, 3000, A.c(true, i), c1));
		}
		_d += 3000;

		// ------------------------ Split sides

		selection = new LinkedList<Color>(single);
		selection.remove(c1);

		Collections.shuffle(selection);

		_cmds.add(new CF(_d, 5000, A.c(AT.SIDE_RIGHT), single.get(0)));
		_d += 8000;
		_cmds.add(new CF(_d, 5000, A.c(AT.SIDE_LEFT), single.get(1)));
		_d += 8000;
		_cmds.add(new CF(_d, 5000, A.c(AT.SIDE_RIGHT), single.get(2)));
		_d += 8000;
		_cmds.add(new CF(_d, 5000, A.c(AT.SIDE_LEFT), single.get(3)));
		_d += 8000;
		_cmds.add(new CF(_d, 5000, A.c(AT.SIDE_RIGHT), single.get(4)));
		_d += 8000;

		// ------------------------ Fade all

		selection = new LinkedList<Color>(single);
		selection.remove(single.get(3));
		selection.remove(single.get(4));

		Collections.shuffle(selection);
		c1 = selection.get(0);

		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(_d += 30, 3000, A.c(true, i), c1));
		}
		_d += 3000;

		// ------------------------ Chasing

		selection = new LinkedList<Color>(single);
		selection.remove(c1);

		Collections.shuffle(selection);
		c1 = selection.get(0);
		c2 = selection.get(1);

		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(_d += 30, 300, A.c(true, i), c1));
			_cmds.add(new CF(_d + 300, 300, A.c(true, i), Color.BLACK));
		}
		_d += 1000;
		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(_d += 15, 300, A.c(false, i), c1));
			_cmds.add(new CF(_d + 300, 300, A.c(false, i), Color.BLACK));
			_cmds.add(new CF(_d += 15, 300, A.c(false, A.LEDS - i - 1), c2));
			_cmds.add(new CF(_d + 300, 300, A.c(false, A.LEDS - i - 1), Color.BLACK));
		}
		_d += 1000;
		for (int i = A.HALF; i > 0; i--) {
			_cmds.add(new CF(_d += 15, 300, A.c(false, i), c2));
			_cmds.add(new CF(_d + 300, 300, A.c(false, i), Color.BLACK));
			_cmds.add(new CF(_d += 15, 300, A.c(false, A.LEDS - i - 1), c1));
			_cmds.add(new CF(_d + 300, 300, A.c(false, A.LEDS - i - 1), Color.BLACK));
		}
		_d += 1000;

		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(_d += 30, 300, A.c(true, i), c2));
			_cmds.add(new CF(_d + 300, 300, A.c(true, i), Color.BLACK));
		}
		_d += 1000;

		// ------------------------ Fade all

		selection = new LinkedList<Color>(single);
		selection.remove(c1);
		selection.remove(c2);

		Collections.shuffle(selection);
		c1 = selection.get(0);

		for (int i = 0; i < A.HALF; i++) {
			_cmds.add(new CF(_d += 30, 3000, A.c(true, i), c1));
		}
		_d += 3000;

		// ------------------------ Split sections

		selection = new LinkedList<Color>(single);
		selection.remove(c1);

		Collections.shuffle(selection);

		_cmds.add(new CF(_d, 5000, A.c(AT.SECTION_TOP), selection.get(0)));
		_d += 8000;
		_cmds.add(new CF(_d, 5000, A.c(AT.SECTION_MIDDLE), selection.get(1)));
		_d += 8000;
		_cmds.add(new CF(_d, 5000, A.c(AT.SECTION_BOTTOM), selection.get(2)));
		_d += 8000;
		_cmds.add(new CF(_d, 5000, A.c(AT.SECTION_TRUNK), selection.get(3)));
		_d += 8000;

		// ------------------------ Fade out

		for (int i = A.HALF; i >= 0; i--) {
			_cmds.add(new CF(_d += 30, 3000, A.c(true, i), Color.BLACK));
		}
		_d += 4000;
	}

	public void poweroff() {
		synchronized (_poweroff) {
			if (_poweroff) {
				return;
			}
			_poweroff = true;
		}

		off();

		try {
			Runtime.getRuntime().exec("/usr/bin/sudo /sbin/poweroff");
		} catch (IOException e) {
		}

		SpringApplication.exit(_appContext, (ExitCodeGenerator) () -> 42);
	}

	@Override
	public void run() {
		super.run();

		byte[] data = new byte[4 + (A.LEDS * 4) + 4];
		long start = 0;

		while (true) {

			initCommands();

			for (long time = 0; time < _d; time += FlatTreeApplication.TICK) {

				if (!_running) {
					return;
				}

				for (CBase c : _cmds) {
					c.tick(time);
				}

				int i = 0;
				data[i++] = (byte) 0x00;
				data[i++] = (byte) 0x00;
				data[i++] = (byte) 0x00;
				data[i++] = (byte) 0x00;

				for (Pixel p : FlatTreeApplication.PIXELS) {
					data[i++] = (byte) 0xE0 | 0xFF;
					data[i++] = (byte) p.getColor().getBlue();
					data[i++] = (byte) p.getColor().getGreen();
					data[i++] = (byte) p.getColor().getRed();
				}

				data[i++] = (byte) 0xFF;
				data[i++] = (byte) 0xFF;
				data[i++] = (byte) 0xFF;
				data[i++] = (byte) 0xFF;

				if (FlatTreeApplication.PI_EXISTS) {
					start = System.currentTimeMillis();
					try {
						_spi.write(data);
					} catch (IOException e) {
						LOG.error("Failed to send frame", e);
					}
					se(FlatTreeApplication.TICK - (System.currentTimeMillis() - start));
				} else {
					LOG.info(time + " " + data);
				}

			}
		}
	}

	@PreDestroy
	private void off() {
		_running = false;
		if (FlatTreeApplication.PI_EXISTS) {
			_gpio.shutdown();
		}
	}

	private void se(long ms) {
		if (ms < 0) {
			return;
		}
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
}
