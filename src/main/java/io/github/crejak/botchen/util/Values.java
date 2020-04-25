package io.github.crejak.botchen.util;

import io.github.crejak.botchen.PokemonInstance;
import io.github.crejak.botchen.battle.Weather;
import me.sargunvohra.lib.pokekotlin.model.Move;
import me.sargunvohra.lib.pokekotlin.model.MoveMetaData;

import java.util.Random;

public class Values {
    public static double getMultiplier(int statMod) {
        switch (statMod) {
            case -6:
                return 2d/8d;
            case -5:
                return 2d/7d;
            case -4:
                return 2d/6d;
            case -3:
                return 2d/5d;
            case -2:
                return 2d/4d;
            case -1:
                return 2d/3d;
            case 0:
                return 1d;
            case 1:
                return 3d/2d;
            case 2:
                return 4d/2d;
            case 3:
                return 5d/2d;
            case 4:
                return 6d/2d;
            case 5:
                return 7d/2d;
            case 6:
                return 8d/2d;
        }
        return -1;
    }

    public static double getMultiplierForAccuracyEvasion(int statMod) {
        switch (statMod) {
            case -6:
                return 3d/9d;
            case -5:
                return 3d/8d;
            case -4:
                return 3d/7d;
            case -3:
                return 3d/6d;
            case -2:
                return 3d/5d;
            case -1:
                return 3d/4d;
            case 0:
                return 1d;
            case 1:
                return 4d/3d;
            case 2:
                return 5d/3d;
            case 3:
                return 6d/3d;
            case 4:
                return 7d/3d;
            case 5:
                return 8d/3d;
            case 6:
                return 9d/3d;
        }
        return -1;
    }

    public static double getCritChance(int critRate) {
        switch (critRate) {
            case 0:
                return 1d/24d;
            case 1:
                return 1d/8d;
            case 2:
                return 1d/2d;
        }
        if (critRate >= 3) {
            return 1d;
        }
        return -1;
    }

    public static double getTypeEffectiveness(int moveType, int pokemonType) {
        switch (moveType) {
            case ApiConstants.TYPE_NORMAL:
                switch (pokemonType) {
                    case ApiConstants.TYPE_GHOST:
                        return 0;
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_STEEL:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_FIGHTING:
                switch (pokemonType) {
                    case ApiConstants.TYPE_GHOST:
                        return 0;
                    case ApiConstants.TYPE_NORMAL:
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_ICE:
                    case ApiConstants.TYPE_DARK:
                        return 2;
                    case ApiConstants.TYPE_FLYING:
                    case ApiConstants.TYPE_POISON:
                    case ApiConstants.TYPE_BUG:
                    case ApiConstants.TYPE_PSYCHIC:
                    case ApiConstants.TYPE_FAIRY:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_FLYING:
                switch (pokemonType) {
                    case ApiConstants.TYPE_FIGHTING:
                    case ApiConstants.TYPE_BUG:
                    case ApiConstants.TYPE_GRASS:
                        return 2;
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_ELECTRIC:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_POISON:
                switch (pokemonType) {
                    case ApiConstants.TYPE_STEEL:
                        return 0;
                    case ApiConstants.TYPE_GRASS:
                    case ApiConstants.TYPE_FAIRY:
                        return 2;
                    case ApiConstants.TYPE_POISON:
                    case ApiConstants.TYPE_GROUND:
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_GHOST:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_GROUND:
                switch (pokemonType) {
                    case ApiConstants.TYPE_FLYING:
                        return 0;
                    case ApiConstants.TYPE_POISON:
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_FIRE:
                    case ApiConstants.TYPE_ELECTRIC:
                        return 2;
                    case ApiConstants.TYPE_BUG:
                    case ApiConstants.TYPE_GRASS:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_ROCK:
                switch (pokemonType) {
                    case ApiConstants.TYPE_FLYING:
                    case ApiConstants.TYPE_BUG:
                    case ApiConstants.TYPE_FIRE:
                    case ApiConstants.TYPE_ICE:
                        return 2;
                    case ApiConstants.TYPE_FIGHTING:
                    case ApiConstants.TYPE_GROUND:
                    case ApiConstants.TYPE_STEEL:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_BUG:
                switch (pokemonType) {
                    case ApiConstants.TYPE_GRASS:
                    case ApiConstants.TYPE_PSYCHIC:
                    case ApiConstants.TYPE_DARK:
                        return 2;
                    case ApiConstants.TYPE_FIGHTING:
                    case ApiConstants.TYPE_FLYING:
                    case ApiConstants.TYPE_POISON:
                    case ApiConstants.TYPE_GHOST:
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_FIRE:
                    case ApiConstants.TYPE_FAIRY:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_GHOST:
                switch (pokemonType) {
                    case ApiConstants.TYPE_NORMAL:
                        return 0;
                    case ApiConstants.TYPE_GHOST:
                    case ApiConstants.TYPE_PSYCHIC:
                        return 2;
                    case ApiConstants.TYPE_DARK:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_STEEL:
                switch (pokemonType) {
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_ICE:
                    case ApiConstants.TYPE_FAIRY:
                        return 2;
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_FIRE:
                    case ApiConstants.TYPE_WATER:
                    case ApiConstants.TYPE_ELECTRIC:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_FIRE:
                switch (pokemonType) {
                    case ApiConstants.TYPE_BUG:
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_GRASS:
                    case ApiConstants.TYPE_ICE:
                        return 2;
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_FIRE:
                    case ApiConstants.TYPE_WATER:
                    case ApiConstants.TYPE_DRAGON:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_WATER:
                switch (pokemonType) {
                    case ApiConstants.TYPE_GROUND:
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_FIRE:
                        return 2;
                    case ApiConstants.TYPE_WATER:
                    case ApiConstants.TYPE_GRASS:
                    case ApiConstants.TYPE_DRAGON:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_GRASS:
                switch (pokemonType) {
                    case ApiConstants.TYPE_GROUND:
                    case ApiConstants.TYPE_ROCK:
                    case ApiConstants.TYPE_WATER:
                        return 2;
                    case ApiConstants.TYPE_FLYING:
                    case ApiConstants.TYPE_POISON:
                    case ApiConstants.TYPE_BUG:
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_FIRE:
                    case ApiConstants.TYPE_GRASS:
                    case ApiConstants.TYPE_DRAGON:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_ELECTRIC:
                switch (pokemonType) {
                    case ApiConstants.TYPE_GROUND:
                        return 0;
                    case ApiConstants.TYPE_FLYING:
                    case ApiConstants.TYPE_WATER:
                        return 2;
                    case ApiConstants.TYPE_GRASS:
                    case ApiConstants.TYPE_ELECTRIC:
                    case ApiConstants.TYPE_DRAGON:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_PSYCHIC:
                switch (pokemonType) {
                    case ApiConstants.TYPE_DARK:
                        return 0;
                    case ApiConstants.TYPE_FIGHTING:
                    case ApiConstants.TYPE_POISON:
                        return 2;
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_PSYCHIC:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_ICE:
                switch (pokemonType) {
                    case ApiConstants.TYPE_FLYING:
                    case ApiConstants.TYPE_GROUND:
                    case ApiConstants.TYPE_GRASS:
                    case ApiConstants.TYPE_DRAGON:
                        return 2;
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_FIRE:
                    case ApiConstants.TYPE_WATER:
                    case ApiConstants.TYPE_ICE:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_DRAGON:
                switch (pokemonType) {
                    case ApiConstants.TYPE_FAIRY:
                        return 0;
                    case ApiConstants.TYPE_DRAGON:
                        return 2;
                    case ApiConstants.TYPE_STEEL:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_DARK:
                switch (pokemonType) {
                    case ApiConstants.TYPE_GHOST:
                    case ApiConstants.TYPE_PSYCHIC:
                        return 2;
                    case ApiConstants.TYPE_FIGHTING:
                    case ApiConstants.TYPE_DARK:
                    case ApiConstants.TYPE_FAIRY:
                        return 0.5;
                    default:
                        return 1;
                }
            case ApiConstants.TYPE_FAIRY:
                switch (pokemonType) {
                    case ApiConstants.TYPE_FIGHTING:
                    case ApiConstants.TYPE_DRAGON:
                    case ApiConstants.TYPE_DARK:
                        return 2;
                    case ApiConstants.TYPE_POISON:
                    case ApiConstants.TYPE_STEEL:
                    case ApiConstants.TYPE_FIRE:
                        return 0.5;
                    default:
                        return 1;
                }
        }
        return 1;
    }
}
