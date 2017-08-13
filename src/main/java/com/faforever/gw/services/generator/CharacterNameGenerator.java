package com.faforever.gw.services.generator;

import com.faforever.gw.model.Faction;
import org.springframework.stereotype.Service;

@Service
public class CharacterNameGenerator {
    private final static String[] names;
    private final static String[] lastNames;
    private final static String[] machineParts;
    private final static String[] aeonNames;
    private final static String[] seraNameParts;

    static {
        names = new String[]{
                //Male names
                "Alexander", "Albert", "Arnold",//A
                "Brad", "Bernard", "Boris",//B
                "Charles", "Cole",//C
                "David", "Daniel", "Dominic",//D
                "Edward", "Evan", "Eric", "Erich",//E
                "Frederick", "Felix",//F
                "Gavin", "Gregory", "Gunther", "George",//G
                "Henry", "Harrison", "Heinz", "Hans",//H
                "Ivan", "Igor",//I
                "Jack", "Jacob",//J
                "Kevin", "Kendall",//K
                "Lewis", "Logan",//L
                "Mark", "Maddox",//M
                "Nicholai", "Nadir",//N
                "Octavius", "Otto",//O
                "Paul", "Percival",//P
                "Quentin", "Quinlan",//Q
                "Randall", "Raymond",//R
                "Stephen", "Sergey", "Stanislav", "Steve",//S
                "Tony", "Trent",//T
                "Umberto",//U
                "Victor", "Vlad",//V
                "Wyatt", "Walter", "Wagner,", "William",//W
                //X
                "Yuri", "Yevgeny",//Y
                "Zane", "Zachary",//Z
                //Female names
                "Alice", "Aiko", "Abigail", "Alexandra",//A
                "Beatrice",//B
                "Claire", "Caroline",//C
                "Dasha", "Devon",//D
                "Elizabeth", "Emily",//E
                "Felicia", "Fiona",//F
                "Gabriella", "Grace",//G
                "Hannah", "Heather",//H
                "Isabella", "Irene",//I
                "Juliana", "Jennifer",//J
                "Kelsey", "Kathleen", "Katherine",//K
                "Lena", "Lyudmila",//L
                "Mackenzie", "Michelle",//M
                "Nicole", "Natalia",//N
                "Olivia", "Oksana",//O
                "Paula", "Patricia",//P
                //Q
                "Rochelle", "Raven",//R
                "Sofia", "Sasha",//S
                "Tiffany", "Thalia",//T
                "Ulyana",//U
                "Victoria", "Vanessa",//V
                "Winnifred", "Wilhelmina",//W
                //X
                "Yulia", "Yana",//Y
                "Zoe", "Zara",//Z
        };

        lastNames = new String[]{
                "Shelby", "Sessions", "Begich", "McCain", "Flake", "Pryor", "Bozeman", "Udall", "Bennett", "Blumenthal",
                "Murphy", "Carper", "Coons", "Nelson", "Rubio", "Chambliss", "Isakson", "Schatz", "Crapo", "Risch",
                "Kirk", "Coats", "Donnelly", "Grassley", "Harkin", "Moran", "O'Connell", "Paul", "Vitter", "King", "Cardin",
                "Warren", "Cowan", "Levin", "Franken", "Cochran", "Wicker", "Blunt", "Baucus", "Tester", "Johanns", "Reid",
                "Heller", "Lautenberg", "Menendez", "Udall", "Heinrich", "Schumer", "Burr", "Hoeven", "Portman", "Inhofe",
                "Coburn", "Wyden", "Merkley", "Casey, Jr.", "Reed", "Whitehouse", "Scott", "Johnson", "Washington", "Mae",
                "Thune", "Corker", "Cornyn", "Cruz", "Lee", "Leahy", "Warner", "Kaine", "Manchin", "Johnson", "Enzi",
                "Barrasso", "Rokossovsky", "Model", "von Manstein"
        };

        machineParts = new String[]{
                "Binary", "Decimal", "Octal", "Hex", "Cipher", "Algo", //math related
                "Circuit", "Transistor", "Capacitor", "Conductor", // electrical components
                "Flash", "Electron", "Voltron", "Refractor", "Pulse", "Gear", "Wave", "Gauge", // physics related
                "Core", "Processor", "Monitor", "Mainframe", "Jumper", // pc parts related
                "iNode", "Stream", "Terminal", "Torrent", "Switch", "Link" // network related
        };

        aeonNames = new String[]{
                "Pryderudd", "Merli", "Fanbyn", "Wili", "Andrasron", "Derauned", "Trevnon", "Pric", "Cerri",
                "Gainri", "Brynmo", "Blodeuwene", "Dallau", "Catrin", "Gwenan", "Aradoc", "Hefionmyr",
                "Merrioale", "Morwelur", "Hefin", "Aelrewarrga", "Eadweard", "Aescorn", "Eadgar",
                "Herewardred", "Elfricne", "Eardic", "Leofri", "Ealdg", "Osgaric", "Ealdrarht", "Aesc",
                "Elflaht", "Elfriae", "Aelfwric", "Leofgy", "Hild", "Eadmae", "Eadwinurg", "Aelfgiild",
                "Rianna", "Marxon", "Kathleen", "Katherine",
        };

        seraNameParts = new String[]{
                "Oshustl", "Zytuo", "Thosta", "Toohy", "Onnais", "Onautt", "Ostuat", "Zuastluo", "Yhunn", "Taussai", "Uhout",
                "Unnaustl", "Tynnoo", "Ithous", "Ohuas", "Ottuhw", "Hithy", "Isauss", "Uthast", "Zytti", "Zisu",
                "Saitha", "Thuti", "Sutty", "Sahwa", "Huzi", "Sittua", "Ustah", "Vynnu", "Unnauss", "Unuan", "Vithy",
                "Oshyst", "Tuhoo", "Hooty", "Issyz", "Hussua", "Ithaust", "Yshust", "Yttuastl", "Ytun", "Yshyhw",
                "Inuth", "Inuostl", "Uhwaush", "Ostuost", "Yenzyne", "Izys", "Hinna", "Uhauhw", "Zussu", "Ittyhw",
                "Istluostl", "Innuaz", "Itutt", "Zizo", "Vasha", "Vaunni", "Hysta", "Utys", "Isaitt", "Ushoz", "Zustlu",
                "Thisha", "Ohwush", "Vytou", "Thathy", "Zaisy", "Zastlu", "Ohwass", "Ihys", "Haustla", "Ynnooz", "Yssooh",
                "Unnaunn", "Thituo", "Issuas", "Ytuastl", "Istus", "Sitou", "Ithitt", "Zithuo", "Histly", "Oshihw"
        };
    }


    public String[] generateNames(Faction faction) {
        String[] list = new String[5];
        int rand1, rand2;
        switch (faction) {
            case UEF:
                for (int i = 0; i < list.length; i++) {
                    rand1 = (int) (Math.random() * names.length);
                    rand2 = (int) (Math.random() * lastNames.length);
                    list[i] = names[rand1] + " " + lastNames[rand2];
                }
                break;
            case CYBRAN:
                for (int i = 0; i < list.length; i++) {
                    double humanName = Math.random();
                    if (humanName < 0.5) {
                        rand1 = (int) (Math.random() * names.length);
                        rand2 = (int) (Math.random() * lastNames.length);
                        list[i] = names[rand1] + " " + lastNames[rand2];
                    } else {
                        rand1 = (int) (Math.random() * machineParts.length);
                        rand2 = (int) (Math.random() * 10);
                        list[i] = machineParts[rand1] + " " + rand2;
                    }
                }
                break;
            case AEON:
                for (int i = 0; i < list.length; i++) {
                    rand1 = (int) (Math.random() * aeonNames.length);
                    list[i] = aeonNames[rand1];
                }
                break;
            case SERAPHIM:
                for (int i = 0; i < list.length; i++) {
                    rand1 = (int) (Math.random() * seraNameParts.length);
                    rand2 = (int) (Math.random() * seraNameParts.length);
                    list[i] = seraNameParts[rand1] + "-" + seraNameParts[rand2];
                }
                break;
        }
        return list;
    }
}
