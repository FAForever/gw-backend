package com.faforever.gw.services.generator;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class SolarSystemNameGenerator {
    private Random rand = new Random();
	private Set<String> currentNames = new HashSet<String>();
    
    
    private final static String[] greekLetters = { "Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota",
			"Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron", "pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi", "Psi",
			"Omega" };

	private final static String[] constellations = { "Andromedae", "Antilae", "Apodis", "Aquarii", "Aquilae", "Arae", "Arietis",
			"Aurigae", "Boötis", "Camelopardis", "Canis Majoris", "Canis Minoris", "Draconis", "Eridani", "Gemini", "Herculis", "Leonis",
			"Lyrae", "Octanis", "Orionis", "Pegasi", "Persei", "Phoenicis", "Ursae Majoris", "Ursae Minoris",
	"Volantis" };
	
	private final static String[] astronomers = { "Galilea", "Copernicus", "Kepler", "Hubble", "Tyson", "Ptolemy", "Sagan",
			"Herschel", "Halley", "Messier", "Kuiper", "Laplace", "Brahe", "Huygens", "Cassini"};

	
	private final static String[] otherPrefixes = {"NGC", "HR", "WNC", "SSDS", "V", "WISE", "LQ", "DENIS-P", "HIP", "HD"};

    	// Clears the set of previous names
	public void clear() {
		currentNames.clear();
	}
	
	// Generate a given amount of solar system names in batch
	public String[] generateNames(int amount) {
		String[] names = new String[amount];
		for (int i=0;i<amount;i++) {
			names[i] = next();
		}

		return names;
	}
	
	// Generate another solar system name
	public String next() {
		// Proportion the names 6/10 Greek/constellation, 3/10 after astronomers and 1/10 based on astronomical surveys.
		int val = rand.nextInt(10);
		
		if (val < 6) {
			return greekName();
		}
		else if (val < 9) {
			return astroName();
		}
		else {
			return otherName();			
		}
	}
	
	private String greekName() {
		String name = greekLetters[rand.nextInt(greekLetters.length)] + " " +  constellations[rand.nextInt(constellations.length)];
		while (currentNames.contains(name) == true){
			name = greekLetters[rand.nextInt(greekLetters.length)] + " " +  constellations[rand.nextInt(constellations.length)];
		}
		currentNames.add(name);
		return name;
	}
	
	private String astroName() {
		String name = astronomers[rand.nextInt(astronomers.length)] + " " + (rand.nextInt(80) + 1); 
		while (currentNames.contains(name) == true) {
			name = astronomers[rand.nextInt(astronomers.length)] + " " + (rand.nextInt(80) + 1);
		}
		currentNames.add(name);
		return name;
	}
	
	private String otherName() {
		String name = otherPrefixes[rand.nextInt(otherPrefixes.length)] + " " + rand.nextInt(10000) + 1;
		while (currentNames.contains(name) == true) {
			name = otherPrefixes[rand.nextInt(otherPrefixes.length)] + " " + rand.nextInt(10000) + 1;
		}
		currentNames.add(name);
		return name;
	}
}
