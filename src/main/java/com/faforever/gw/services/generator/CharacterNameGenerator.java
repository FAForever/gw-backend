package com.faforever.gw.services.generator;
import com.faforever.gw.model.Faction;
import org.springframework.stereotype.Service;
@Service
public class CharacterNameGenerator {
	private final static String[] earthNames;
	private final static String[] earthSurnames;
	private final static String[] symbiontNames;
	private final static String[] symbiontSurnames;
	private final static String[] symbiontAI;
	private final static String[] aeonNames;
	private final static String[][] aeonEpithets;
	private final static String[] blacklistNames;
    //Sheppard's unique Seraphim name generator
    private final static String[] firstVowel = {"O", "U", "I", "Y", "I"};
    private final static String[] firstConsonant = {"T", "S", "Z", "Th", "H", "V"};
    private final static String[] consonant = {"n", "t", "h", "s", "z", "th", "tt", "st", "sh", "hw", "ss", "nn", "stl", "n", "t", "h", "s", "z", "n", "t", "h", "s", "z"};
    private final static String[] vowel = {"a", "y", "u", "o", "i", "ou", "oo", "uu", "uo", "ai", "ua", "au", "u", "i", "a", "y", "u", "o",};

	static {
		earthNames = new String[]{
				//Male names
				"Alexander", "Albert", "Arnold", "August",//A
				"Brad", "Bernard", "Bruce", "Baird", "Brian",//B
				"Charles", "Cole", "Christopher",//C
				"David", "Daniel", "Dominic", "Donald",//D
				"Edward", "Evan", "Eric", "Erich",//E
				"Frederick", "Felix",//F
				"Gavin", "Gregory", "Gunther", "George", "Grant", "Gordon",//G
				"Henry", "Harrison", "Hewlett", "Hall", "Howard",//H
				"Isaac",
				"Jackson", "Jacob", "Johnathan", "James",//J
				"Kevin", "Kendall",//K
				"Lewis", "Logan", "Lucius", "Leonard",//L
				"Marcus", "Maverick", "Mathias", "Matthew", "Michael","Mitchel", //M
				"Nicholai", "Nadir", "Noah", "Nigel",//N
				"Octavius", "Oberon",//O
				"Paul", "Percival", "Percius", "Peter",//P
				"Quentin", "Quinlan",//Q
				"Randall", "Raymond", "Richard", "Robin",//R
				"Stephen", "Sergey", "Stanislav", "Steven", "Samuel","Scott", "Shane",//S
				"Tony", "Trent", "Tyler", "Thomas", "Terrence",//T
				"Umberto",//U
				"Victor", "Vladimir",//V
				"Wyatt", "Walter", "William", "Winston",//W
				"Xavier", //X
				"Zane", "Zachary",//Z
				//Female names
				"Alice", "Aiko", "Abigail", "Alexandra", "Amelia", "Ava", "Annabelle", "Avery",
				"Beatrice", "Brynn", "Briana",//B
				"Claire", "Caroline", "Catherine", "Colette", "Chloe", "Cassidy", "Camille",//C
				"Dasha", "Devon", "Dietrich", "Delaney", "Dakota", "Diana",//D
				"Elizabeth", "Emily", "Elena", "Evelyn", "Ella", "Emma",//E
				"Felicia", "Fiona",//F
                "Gabriella", "Grace",
                "Hannah", "Heather", "Holly",//H
                "Isabella", "Irene",
                "Juliana", "Jennifer", "Jillian",//J
				"Kelsey", "Kathleen", "Katherine", "Kristen",//K
				"Lena", "Lyudmila", "Lucy",//L
				"Mackenzie", "Michelle", "Mariane", "Maya", "Madeline", "Miriam", "Macy", "Marina", //M
				"Nicole", "Natalia", "Niobe", "Naomi", "Nina",//N
				"Olivia", "Oksana", "Olga",//O
				"Paula", "Patricia", "Penelope", "Paisley", "Piper", "Polina",//P
				//Q
				"Rochelle", "Raven", "Riley", "Rebecca", "Rachell", //R
				"Sofia", "Sasha", "Sadie",//S
				"Tabitha", "Thalia", "Taylor", "Tatyana",//T
				"Ulyana",//U
				"Victoria", "Vanessa", "Violet", "Valyria",//V
                "Winnifred",
                //X
		};

		earthSurnames = new String[]{
				"Shelby", "Sessions", "Begich", "McCain", "Flake", "Pryor", "Bozeman", "Udall", "Bennett", "Blumenthal",
				"Murphy", "Carper", "Coons", "Nelson", "Rubio", "Chambliss", "Isakson", "Crapo", "Risch",
				"Kirk", "Donnelly", "Grassley", "Harkin", "Moran", "O'Connell", "Paul", "Vitter", "King", "Cardin",
				"Warren", "Cowan", "Levin", "Franklin", "Cochran", "Wicker", "Blunt", "Baucus", "Tester", "Johanns", "Reid",
				"Heller", "Burr", "Hoeven", "Portman", "Grayson", "Porter", "Perry", "McGuire", "Gardner", "Tusk", "Tustin",
				"Coburn", "Wyden", "Merkley", "Casey", "Jr.", "Reed", "Whitehouse", "Scott", "Johnson", "Washington", "Mae",
				"Thune", "Corker", "Cornyn", "Cruz", "Lee", "Leahy", "Warner", "Kaine", "Manchin", "Johnson",
				"Barrasso", "Rokossovsky", "Model", "Satchel", "Antony", "Graff", "Thatcher", "Cicero", "Benedict",
				"Bismark", "Berling", "Randolph", "Brutus", "Mattis", "Dunford", "Ford", "Petrov", "Pershing", "Reinhardt",
				"Mansfeld", "Harvey", "Kennedy", "Schulte", "Wolf", "Mackenzie", "O'Reilly", "Graham", "Owens",
				"Fisher", "Reynolds", "Ferguson", "Hamilton", "Schmidt", "Andrews", "Silva", "Davidson", "Hoffman", "Maxwell", "Floyd",
				"Santana", "Clayton", "Dalton", "Preston", "Booker", "Wilkinson", "Maynard", "Sloan", "Browning", "Winters", "Durham", "Bradshaw",
                "Rivers", "Bartlett", "Goodell", "Sheppard", "Kimball", "Barron", "Pennington", "Macintosh", "Harding", "Hoover", "Richmond",
                "Neller", "Dickinson", "Grayson", "Adams", "Webster", "Stonewall", "Hickery", "Stout", "Lynn", "Mccarthy", "Sears", "Donovan",
                "Stanton", "Lancaster", "Tyson", "Sharpe", "Whitfield", "Stuart", "Conrad", "Kirkland", "Carney", "Hyde", "Vinson", "Mcmahon",
                "Knowles", "Morrison", "Hudson", "Matthews", "Dunn", "Stone", "Dixon", "Crawford", "Hunter", "Webb", "Mason", "Warden", "Shaw",
                "Robertson", "Daniels", "Palmer", "Rice", "Woods", "Butler", "Price", "Watson", "Morgan", "Bell", "Lincoln", "Howard",
                "Cook", "Campbell", "Collins", "Green", "Young", "Clarkson", "Moore", "Anderson", "Martin", "Williams",
        };

		symbiontNames = new String[]{
				//German Male
				"Oskar", "Konstantin", "Hans", "Heinrich", "Joseph", "Martin", "Konrad", "Hendrik", "Ludwig", "Anton", "Arik", "Bartholomäus",
				"Berthold", "Christian", "Dennis", "Domninik", "Eberhart", "Emanuel", "Elias", "Eugen", "Frederich", "Gabriel", "Georg", "Gregor", "Gustl",
				"Godfrey", "Gustav", "Gunter", "Harald", "Hieronymus", "Hildagarde", "Ernst", "Otto", "Karl", "Lukas", "Felix", "Hermann", "Artur", "Bruno",
				"Wilhelm", "Erich", "Rudolph", "Walter", "Werner", "Kurt", "Immanuel", "Rolf", "Ulrich", "Alfons", "Christoph", "Nikolaus", "Theodor", "Leopold",
				//German Female
                "Greta", "Frieda", "Mathilda", "Dietrich", "Angela", "Ada", "Adele", "Angelika", "Ava", "Mathilda", "Erika", "Ethelinda", "Genevieve", "Grisella",
                "Heidemaria", "Wilma", "Irmina", "Karlina", "Karoline", "Brigitte", "Magdalene", "Margarite", "Marlene", "Monika", "Odelina", "Olinda", "Roderika", "Selma",
				"Ulrike", "Wendeline", "Wilma", "Zeta",
				//Russian Male
				"Abram", "Aleksy","Ivan", "Peter", "Artem", "Artyom", "Borislav", "Bronislav", "David", "Dmitri", "Nikolai", "Evgeny", "Grigori", "Isidor",
				"Konstantin", "Leonid", "Luka", "Makar", "Mark", "Maxim", "Miron", "Miroslav", "Nestor", "Pavel", "Radimir", "Radovid", "Rolan", "Samuil", "Sergei",
				"Vladimir", "Sevastian", "Yuri", "Yevgeny", "Yakov", "Timur", "Viktor", "Velen", "Vladislav", "Yakov", "Zakhar", "Alexander", "Mikhail", "Igor", "Evgeny",
                "Andrei", "Pyotr", "Rotislav", "Alexei", "Zhores",
                //Russian Female
				"Alexandra", "Anna", "Annastasia", "Elena", "Ivanna", "Dominika", "Eva", "Galina", "Gala", "Ilya", "Juliana", "Katerina", "Karina", "Julia",
                "Kira", "Klara", "Kristina", "Lara", "Lidya", "Marina", "Mariya", "Masha", "Misha", "Nadya", "Natasia", "Nina", "Polina", "Olga", "Silena",
                "Tatyana", "Svetlana", "Yulia", "Yana", "Tanya", "Valery", "Vasilia", "Viktoria",
        };

		symbiontSurnames = new String[]{
                //German names:
                "Wagner", "Wolff", "Bauer", "Hofmann", "Gunther", "Albrecht", "Baumann", "Friedrich", "Jager", "Pietsch",
                "Altergott", "Andelman", "Brecher", "Beckendorf", "Beissel", "Breslau", "Ehrhardt", "Junker", "Kanitz", "Kaiser", "Kauffman",
                "Klock", "Klaus", "Kreuzer", "Lehr", "Lentz", "Meissner", "Moldenhauer", "Prinz", "Reichman", "Reinhardt", "Radunz", "Rossman", "Schult",
                "Schwarzkopf", "Selig", "Sohn", "Stauben", "Steiner", "Strobl", "Sulz", "Thalberg", "Trager", "Voss", "Vogel", "Wachter", "Wahrmann", "Wohl",
				"Zeigler", "Zorn", "Kruger", "Friedrich", "Winter", "Brandt", "Busch", "Schroder", "Beyer", "Bodmann", "Bruckner", "Becher", "Bachman", "Eichmann",
				"Erhard", "Bismark", "Anacker", "Goring", "Hausser", "Wilhelm", "Jaeger", "Kampf", "Kiesinger", "Kraftman", "Kohlmann", "Kern", "Macher", "Moder",
				"Meyer", "Oberlander", "Oberg", "Reinecke", "Rainer", "Franz", "Oswald", "Schafer", "Stark", "Straube", "Oskar", "Weidenmann", "Weber",
				"Lautenberg", "Heinrich", "Schumer", "Inhofe",
				//Russian names:
                "Mikhailov", "Rokossovsky", "Isatova", "Glayanov", "Markov", "Donskoy", "Brin",
                "Velsky", "Durov", "Lebedev", "Lupanov", "Morozov", "Pavlov", "Stepanov", "Skylarov", "Terekov", "Turchiv", "Ternovsky", "Yablonsky",
                "Petrov", "Chokov", "Grabin", "Khariton", "Dragunov", "Nikonov", "Petrovich", "Simonov", "Tokarev", "Korbac", "Mosin", "Conrad",
                "Alexandrov", "Aristov", "Avilov", "Alenin","Bazin", "Bagrov", "Borodin", "Bolotnikov", "Blatov", "Vanzin", "Veselov", "Vetochkin", "Gorev",
				"Garin", "Gachev", "Grekov", "Gubanov", "Golov", "Dobrianov", "Yerzov", "Yerkhov", "Zhabin", "Zherdev", "Zhukov", "Zholdin", "Ivankov", "Ibragimov",
				"Ivkin", "Kabinov", "Kalashnik", "Korzhev", "Konnikov", "Kapustin", "Klokov", "Kolosov", "Loginovsky", "Lapotnikov", "Lesnichy", "Malinin", "Noskov",
				"Nemtsev", "Olenev", "Obolensky", "Petrenko", "Pavlov", "Poltanov", "Puzanov", "Pogadin", "Pyatosin", "Rostov", "Repin", "Rusakov", "Rybalkin", "Ryzhov",
				"Runov", "Rubashkin", "Romanov", "Rozovsky", "Sabitov", "Savinkov", "Saitov", "Smirnitsky", "Samorkhin", "Stepanov", "Sivakov", "Tamarkin", "Tarasov",
				"Toporov", "Tatarov", "Tereschenko", "Tupolev", "Travinikov", "Usenko", "Frolov", "Fenenko", "Khabalov", "Khloponin", "Khalturin", "Kromov", "Khovansky", "Kholod",
				"Khlebikov", "Tsaryov", "Tsukanov", "Chesnokov", "Chapayev", "Chkalov", "Chupov", "Cherkasov", "Chuprin", "Shirokov", "Sharapov", "Shalyapin", "Shuvalov", "Shinkso",
				"Shurupov", "Shirinov", "Eristov", "Yugov", "Yurlov", "Yuditsky", "Yusupov", "Yurakin", "Yushkov", "Yabloklov", "Yashin"
		};

        symbiontAI = new String[]{
                "Decimal", "Octal", "Hex", "Cipher", "Algo", "Prime", "Unit", "Series",
                "Core", "Gauge", "Probe", "Axon", "Alpha", "Terminal", "Procyon AI", "LM Unit",
                "Torrent", "Switch", "Link", "Nexus", "Proxy", "Cipher", "Shiva", "X-QAI",
				"Mace", "Matrix", "Hive", "Automaton", "Prion", "Root", "Liberated AI", "Prototype",
				"AVR", "Gamma", "Logic", "CMOS", "Octave", "Index", "Domain", "Vector", "Execution",
                "Parameter", "Operant", "NERVE",
        };

		aeonNames = new String[]{
				"Pryderudd", "Merli", "Fanbyn", "Wilii", "Andrasron", "Derauned", "Trevnon", "Pric", "Cerri",
				"Gainri", "Brynmo", "Blodeuwene", "Dallau", "Catrin", "Gwenan", "Aradoc", "Hefionmyr",
				"Merrioale", "Morwelur", "Hefin", "Eadweard", "Aescorn", "Eadgar",
				"Herewardred", "Elfricne", "Eardic", "Leofri", "Ealdg", "Osgaric", "Ealdrarht", "Aesc",
				"Elflaht", "Elfriae", "Aelfwric", "Leofgy", "Hild", "Eadmae", "Eadwinurg", "Aelfgiild",
				"Rianna", "Marxon", "Katherine", "Niobe", "Gael", "Stelios", "Thrace", "Enzi", "Enel",
				"Irime", "Miriel", "Aeron", "Andras", "Brenin", "Bedwyr", "Delwyn", "Emyr", "Elis",
                "Garreth", "Glynn", "Harri", "Iwan", "Loyd", "Macsen", "Marlin", "Teigan", "Tristore", "Anika",
                "Wilona", "Ioanna", "Kyros", "Maris", "Thea", "Thanos", "Theo", "Yanni", "Cara", "Emrys", "Mavis",
				"Muriel", "Morrigan", "Neale", "Nyle", "Orin", "Reagan", "Sloan", "Tristian", "Teyrnon", "Wynne",
				"Asmund", "Audun", "Einar", "Frode", "Geir", "Raul", "Siri", "Torben", "Tyr", "Vernon", "Vali", "Toth",
				"Naxos", "Mace", "Halmar", "Horus", "Neldor", "Cormac", "Taog", "Maol", "Arailt", "Tanovar", "Eirmer",
				"Jonas", "Johannas", "Paeris", "Faelyn", "Rhun", "Caerau", "Medyr", "Eurion", "Gawain", "Aidan",
				"Dabriel", "Zachriel", "Haniel", "Forcas", "Ezekiel", "Tagas", "Ithuriel", "Arioch", "Tadhiel",
				"Alaion", "Maul", "Aron", "Sheelin", "Aodh", "Levi",
		};

		//Index 0 = epithets, index 1 = virtues ([0] " of "  [1])
		aeonEpithets = new String[][]{
			{
				"Servant", "Disciple", "Follower", "Seeker",  "Preacher", "Missionary", "Prophet", "Augur", "Herald",
                    "Vessel", "Upholder", "Guardian", "Patron", "Bulwark", "Defender", "Voice", "Messenger", "Emissary", "Harbinger",
                    "Hero", "Spirit", "Marrow", "Bolster", "Vanguard", "Hammer", "The Axe", "Inquisitor", "The Force", "The Fire",
				"Courier", "Vigor", "Resolve", "The Zeal", "The Ardor", "Arbiter", "Intuition", "The Passion", "Aura", "Sprite",
				"Blade",
			},
			{
				"the Light", "Purity", "The Way", "the Divine", "Holiness", "the Faith", "Conviction", "Reverence",
                    "Devotion", "Fealty", "Fortitude", "Salvation", "Radiance", "the Truth", "Grace", "Elysium", "Paragon",
                    "the Ethereal", "Righteousness", "Redemption", "Deliverence", "Discipline", "Sacrifice", "Judgement", "Rectitude",
                    "Virtue", "Absolution",
            }
		};

		//Full names and surnames from characters in Supreme Commander plot that are blacklisted for GW
		blacklistNames = new String[]{
			//Supreme Commander
			"Min", "Jericho", "Mach", "Berry", "Eris", "Riley", "Arnold", "Clarke", "Ariel", "Brackman", "Dostya", "Eliott", "QAI",
                "Aiko", "Toth", "Burke", "Marxon",
                //Supreme Commander Forged Alliance
			"Hex5", "Hex 5", "Hex-5", "Seth-Iavow", "Shun-Ullevash", "Thel-Uuthow", "Zan-Aishahesh", "Oum-Eoshi",
                "Kael", "Rhiza", "Hall", "Fletcher",
                //Supreme Commander 2
			"Maddox", "Gauge", "Rodgers",
		};
	}
	//Redundant letters simply increase their occurance.

	// Creates a single seraphim name based on alternating lists of typical vowels and consonants. Creates names that are two to five syllables long.
	public static String makeSeraphimName(int size){
		String str = "";
		boolean rand = Math.random() > 0.5;
		if(rand) str+= firstVowel[(int) (firstVowel.length*Math.random())];
		else str+= firstConsonant[(int) (firstConsonant.length*Math.random())];
		rand = !rand;
		for(int i = 0; i < size; i++){
			if(rand) str +=  vowel[(int) (vowel.length*Math.random())];
			else str += consonant[(int) (consonant.length*Math.random())];
			rand = !rand;
		}
		return str;
	}

	// A simple helper-method to see if a string exists in a given array of strings.
	public static boolean findString(String str, String[] list){
		for(int i = 0; i < list.length; i++){
			if(list[i].equals(str)) return true;
		}
		return false;
	}

	/* Returns a String of a random name for a given faction. Human factions have roughly a 50/50 chance of being male or female
	 * UEF names consist of a name and surname, pulled from a pool of western culture names (with a balance of male and female names).
	 * Cybran names have a 50/50 chance of being (human name, human surname), or (Machine name - ID number). Human cybran names come from Russian and German
	 * cultures. Aeon names consist of a single name and epithet in the format "(name), (epithet) of (virtue)" to vary them from other other the human factions
	 * and to emphasize their fanaticism. Seraphim names are generated from scratch in two pieces in the format  "(name)-(name)".
	 */
	public String generateName(Faction faction) {
		int index1, index2, index3;
		switch (faction) {
		case UEF:
			String name = earthNames[(int) (Math.random() * earthNames.length)];
			String surname = earthSurnames[(int) (Math.random() * earthSurnames.length)];
			if(findString(surname, blacklistNames)) return generateName(faction);
			return name + " " + surname;
		case CYBRAN:
			double humanName = Math.random();
			if (humanName < 0.5) {
				name = symbiontNames[(int) (Math.random() * symbiontNames.length)];
				surname = symbiontSurnames[(int) (Math.random() * symbiontSurnames.length)];
				if(findString(surname, blacklistNames)) return generateName(faction);
				return name + " " + surname;
			} else {
				String machineName = symbiontAI[(int) (Math.random() * symbiontAI.length)];
				int idNumber = (int) (5 + Math.random() * 15);
				name = machineName + "-" + idNumber;
				if(findString(name, blacklistNames)) return generateName(faction);
				return name;
			}
		case AEON:
			name = aeonNames[(int) (Math.random() * aeonNames.length)];
			String epithet = aeonEpithets[0][(int) (Math.random() * aeonEpithets[0].length)];
			String virtue = aeonEpithets[1][(int) (Math.random() * aeonEpithets[1].length)];
			if(findString(name, blacklistNames)) return generateName(faction);
			return name + ", " + epithet + " of " + virtue;
		case SERAPHIM:
			int size = 3 + (int) (Math.random() * 3);
			name = makeSeraphimName(2) + "-" + makeSeraphimName(size);
			if(findString(name, blacklistNames)) return generateName(faction);
			return name;
		}
		return null;
	}


	public String[] generateNames(Faction faction){
		String[] list = new String[5];
		for(int i = 0; i < list.length; i++){
			list[i] = generateName(faction);
		}
		return list;
	}
}
