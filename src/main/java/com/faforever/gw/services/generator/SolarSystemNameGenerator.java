package com.faforever.gw.services.generator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class SolarSystemNameGenerator {
    private Random rand = new Random();
	private Set<String> names = new HashSet<String>();
    private long index = 1;
    
    private String[] greekLetters = { "Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota",
			"Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron", "pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi", "Psi",
			"Omega" };

	private String[] constellations = { "Andromedae", "Antilae", "Apodis", "Aquarii", "Aquilae", "Arae", "Arietis",
			"Aurigae", "Boötis", "Camelopardis", "Canis Majoris", "Canis Minoris", "Eridani", "Herculis", "Leonis",
			"Lyrae", "Octanis", "Orionis", "Pegasi", "Persei", "Phoenicis", "Ursae Majoris", "Ursae Minoris",
	"Volantis" };
	
	private String[] astronomers = { "Galilea", "Copernicus", "Kepler", "Hubble", "Tyson", "Ptolemy", "Sagan",
			"Herschel", "Halley", "Messier", "Kuiper", "Laplace", "Brahe", "Huygens", "Cassini"};

    public String next() {
		//Proportion the names 6/10 greek/constellation, 3/10 after astronomers and 1/10 based on Survey.
		int val = rand.nextInt(10);
		
		if (val < 6) {
			String name = greekLetters[rand.nextInt(greekLetters.length)] + " " +  constellations[rand.nextInt(constellations.length)];
			while (names.contains(name) == true){
				name = greekLetters[rand.nextInt(greekLetters.length)] + " " +  constellations[rand.nextInt(constellations.length)];
			}
			return name;
		}
		if (val < 9) {
			String name = astronomers[rand.nextInt(astronomers.length)] + " " + rand.nextInt(40);
			while (names.contains(name) == true) {
				name = astronomers[rand.nextInt(astronomers.length)] + " " + rand.nextInt(40);
			}
			return name;
		}
		
		return "solar system #" + index++; // FIXME: Implement actual logic
	}
}
