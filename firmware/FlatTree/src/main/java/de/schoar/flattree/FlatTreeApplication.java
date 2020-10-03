package de.schoar.flattree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.schoar.flattree.addr.A;

@SpringBootApplication
public class FlatTreeApplication {
	public static final boolean PI_EXISTS = true;

	public static final long TICK = 30;

	public static final Pixel[] PIXELS = new Pixel[A.LEDS];

	public static void main(String[] args) {
		for (int i = 0; i < A.LEDS; i++) {
			FlatTreeApplication.PIXELS[i] = new Pixel();
		}
		SpringApplication.run(FlatTreeApplication.class, args);
	}

}
