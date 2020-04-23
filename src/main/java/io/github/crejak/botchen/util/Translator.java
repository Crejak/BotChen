package io.github.crejak.botchen.util;

import me.sargunvohra.lib.pokekotlin.model.Name;
import me.sargunvohra.lib.pokekotlin.model.Type;

import java.util.List;

public class Translator {
    public static String getNameFr(List<Name> names) {
        for (Name name :
                names) {
            if (name.getLanguage().getId() == 5) {
                return name.getName();
            }
        }
        return null;
    }

    public static String getNameFrOrDefault(List<Name> names, String defaultName) {
        String nameFr = getNameFr(names);
        if (nameFr != null) {
            return nameFr;
        }
        return defaultName;
    }

    public static String getTypeNameFr(int typeId) {
        switch (typeId) {
            case 1:
                return "Normal";
            case 2:
                return "Combat";
            case 3:
                return "Vol";
            case 4:
                return "Poison";
            case 5:
                return "Sol";
            case 6:
                return "Roche";
            case 7:
                return "Insecte";
            case 8:
                return "Spectre";
            case 9:
                return "Acier";
            case 10:
                return "Feu";
            case 11:
                return "Eau";
            case 12:
                return "Plante";
            case 13:
                return "Électrik";
            case 14:
                return "Psy";
            case 15:
                return "Glace";
            case 16:
                return "Dragon";
            case 17:
                return "Ténèbres";
            case 18:
                return "Fée";
        }
        return null;
    }
}
