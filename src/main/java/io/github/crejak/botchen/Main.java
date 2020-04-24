package io.github.crejak.botchen;

import io.github.crejak.botchen.battle.*;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends ListenerAdapter {
    private final HashMap<String, UserStatus> statusMap;
    private final HashMap<String, Trainer> trainerMap;
    private final Pokedex pokedex;

    public Main() {
        statusMap = new HashMap<String, UserStatus>();
        trainerMap = new HashMap<String, Trainer>();
        this.pokedex = new Pokedex();
    }

    public static void main(String[] args) throws LoginException, IOException {
        Path path = Paths.get("token.txt");
        String token = Files.readAllLines(path).get(0);
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.playing("Pokémon"));
        builder.addEventListeners(new Main());
        builder.build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String raw = event.getMessage().getContentRaw();
        String lcs = raw.toLowerCase().trim();
        String[] args = lcs.split(" ");
        MessageChannel channel = event.getChannel();
        User user = event.getAuthor();
        UserStatus status = getStatusOrDefault(user);

        if (lcs.contains("je veux faire du vélo") || lcs.contains("je veux faire du velo")) {
            channel.sendMessage(user.getName() + ", chaque chose en son temps !").queue();
            return;
        }

        if (status == UserStatus.REGISTER) {
            register(user, channel, raw);
            return;
        } else if (status == UserStatus.BATTLE) {
            trainerMap.get(user.getId()).battleInterface.input(channel, args);
            return;
        }

        if (args.length <= 0 || !args[0].equals(":p")) {
            return;
        }

        if (args.length == 1) {
            printMainHelp(channel);
            return;
        }

        String cmd = args[1];

        if (cmd.equals("help")) {
            help(channel, args);
        } else if (cmd.equals("register")) {
            register(user, channel, null);
        } else if (cmd.equals("me")) {
            printMe(user, channel);
        } else if (cmd.equals("team")) {
            team(user, channel, args);
        } else if (cmd.equals("pc")) {
            pc(user, channel, args);
        } else if (cmd.equals("pkmn")) {
            printRandomPokemon(channel);
        } else if (cmd.equals("catch")) {
            catchRandomPokemon(user, channel);
        } else if (cmd.equals("battle")) {
            initMockBattle(user, channel, args);
        } else {
            channel.sendMessage("Désolé, je ne connais pas cette commande. Utilise `:p help` pour connaître les actions disponibles.").queue();
        }
    }

    public UserStatus getStatusOrDefault(User user) {
        if (!statusMap.containsKey(user.getId())) {
            statusMap.put(user.getId(), UserStatus.NEUTRAL);
        }

        return statusMap.get(user.getId());
    }

    public void printMainHelp(MessageChannel channel) {
        String text = "Bonjour, je suis le professeur Chen ! Je gère tout ce qui concerne les Pokémon sur ce serveur. " +
                "Voilà les actions que tu peux effectuer :\n" +
                "```\n" +
                ":p           Afficher cette aide, équivalent à ':p help'\n" +
                ":p help      Afficher cette aide\n" +
                ":p register  S'enregistrer en tant que dresseur de Pokémon\n" +
                ":p me        Consulter ta carte de dresseur\n" +
                ":p team      Consulter ton équipe actuelle\n" +
                ":p pc        Consulter les Pokémon stockés sur ton PC\n" +
                "```\n" +
                "Si tu veux connaître plus d'informations sur une action en particulier, tu peux utiliser la commande suivante :\n" +
                "```\n" +
                ":p help <command>  Afficher l'aide pour une commande en particulier\n" +
                "```\n";
        channel.sendMessage(text).queue();
    }

    public void help(MessageChannel channel, String[] args) {
        if (args.length == 2) {
            printMainHelp(channel);
            return;
        }

        String command = args[2];
        String text;
        switch (command) {
            case "help":
                text = "```\n" +
                        ":p help <command>\n\n" +
                        "Permet d'avoir des informations détaillées sur une commande en particulier.\n" +
                        "Exemple:\n\n" +
                        ":p help register\n" +
                        "```";
                break;
            case "register":
                text = "```\n" +
                        ":p register\n\n" +
                        "Lance la procédure d'enregistrement auprès de la ligue Pokémon. C'est la première étape pour " +
                        "devenir dresseur de Pokémon, beaucoup d'actions te seront inaccessibles si tu n'est pas enregistré " +
                        "au préalable. Ne t'inquiète pas, je m'occupe de la majorité des démarches ; je te poserai juste quelques " +
                        "questions pour en savoir plus sur toi.\n" +
                        "```";
                break;
            case "me":
                text = "```\n" +
                        ":p me\n\n" +
                        "Affiche ta carte de dresseur. Tu pourras y retrouver les informations suivantes : ton numéro ID, " +
                        "ton nom, ton argent et le nombre de Pokémon enregistrés dans ton pokédex.\n" +
                        "```";
                break;
            case "team":
                text = "```\n" +
                        ":p team\n\n" +
                        "Affiche le résumé de ton équipe Pokémon actuelle. Tu peux également effectuer les actions suivantes :\n\n" +
                        ":p team <index>                   Afficher le résumé d'un Pokémon de ton équipe\n" +
                        ":p team switch <indexA> <indexB>  Intervertir les positions de 2 Pokémon de ton équipe\n" +
                        ":p team drop <index>              Retirer un Pokémon de l'équipe pour le placer dans le PC\n" +
                        "\n" +
                        "Où <index>, <indexA> et <indexB> désignent la position d'un Pokémon dans ton équipe.\n" +
                        "```";
                break;
            case "pc":
                text = "```\n" +
                        ":p pc\n\n" +
                        "Affiche la liste des Pokémon stockés sur ton PC. Tu peux également effectuer les actions suivantes :\n\n" +
                        ":p pc <index>       Afficher le résumé d'un Pokémon du PC\n" +
                        ":p pc take <index>  Déplacer un Pokémon du PC vers ton équipe, si elle comporte moins de 6 Pokémon\n" +
                        ":p pc free <index>  Relâcher un Pokémon du PC dans la nature\n" +
                        "\n" +
                        "Où <index> désigne la position d'un Pokémon dans le PC.\n" +
                        "```";
                break;
            default:
                text = "Désolé, je ne connais pas la commande `" + command + "`. Utilise `:p help` pour connaître les actions disponibles.";
        }
        channel.sendMessage(text).queue();
    }

    public void register(User user, MessageChannel channel, String answer) {
        if (getStatusOrDefault(user) == UserStatus.NEUTRAL) {
            if (trainerMap.containsKey(user.getId())) {
                channel.sendMessage("Tu es déjà enregistré en tant que dresseur de Pokémon !").queue();
                return;
            }

            String text = "...\n" +
                    "...\n" +
                    "On dirait que le soleil va bientôt se coucher...\n" +
                    "Ah ! Mais ce n'est pas le moment d'être mélancolique !\n" +
                    "Désolé de t'avoir fait attendre !\n" +
                    "Bienvenue dans le monde de Pokémon !\n" +
                    "Mon nom est Chen.\n" +
                    "Mais tout le monde m'appelle le Bot Pokémon.\n" +
                    "Tu débutes tout juste ton aventure, je me trompe ?\n" +
                    "Laisse-moi tout d'abord t'enseigner les bases de ce Discord !\n" +
                    "Ce Discord est peuplé de créatures appelées Pokémon.\n" +
                    "Nous, les humains, vivons avec les Pokémon.\n" +
                    "Il nous arrive de jouer ou de travailler ensemble.\n" +
                    "Certaines personnes utilisent les Pokémon pour combattre et créent un lien très fort avec eux.\n" +
                    "Moi ?\n" +
                    "Je me contente de faire des recherches, pour que nous en sachions plus sur les Pokémon.\n" +
                    "Mais avant tout, parle-moi un peu de toi.\n" +
                    "Es-tu un garçon?\n" +
                    "Ou bien une fille?\n" +
                    "Dis-moi tout...";
            channel.sendMessage(text).queue();
            statusMap.put(user.getId(), UserStatus.REGISTER);
        } else if (getStatusOrDefault(user) == UserStatus.REGISTER) {
            String lcs = answer.toLowerCase().trim();
            if (lcs.equals("garçon") || lcs.equals("garcon") || lcs.equals("mec") || lcs.equals("homme")) {
                trainerMap.put(user.getId(), new Trainer(user.getId(), user.getName(), Gender.MALE));
            } else if (lcs.equals("fille") || lcs.equals("meuf") || lcs.equals("femme")) {
                trainerMap.put(user.getId(), new Trainer(user.getId(), user.getName(), Gender.FEMALE));
            } else if (lcs.equals("non binaire") || lcs.equals("trans") || lcs.equals("queer")) {
                trainerMap.put(user.getId(), new Trainer(user.getId(), user.getName(), Gender.NON_BINARY));
            } else {
                return;
            }
            String text = "Le moment est venu, " + user.getName() + " !\n" +
                    "Tout est prêt ?\n" +
                    "Tu es sur le point d'embarquer pour une aventure unique.\n" +
                    "Tu vas vivre des moments de joie, d'autres de peine... Tout un monde de choses à expérimenter!\n" +
                    "Allez, plonge dans le monde des Pokémon!\n" +
                    "Sur ce, à très bientôt!";
            channel.sendMessage(text).queue();
            statusMap.put(user.getId(), UserStatus.NEUTRAL);
        }
    }

    public void printMe(@NotNull User user, MessageChannel channel) {
        Trainer trainer = trainerMap.get(user.getId());
        if (trainer == null) {
            channel.sendMessage("Tu dois d'abord t'enregistrer pour devenir un dresseur. Utilise la commande " +
                    "`:p register` pour ça !").queue();
            return;
        }

        String noId = Integer.toString(trainer.trainerId);
        while (noId.length() < 5) {
            noId = "0" + noId;
        }
        String name = trainer.name;
        String money = Integer.toString(trainer.money) + " $";
        String pokedex = Integer.toString(trainer.pokedex.size());

        int maxFieldLength = Math.max(noId.length(), Math.max(name.length(), Math.max(money.length(), pokedex.length())));
        int length = 15 + maxFieldLength;

        StringBuilder sb = new StringBuilder();
        sb.append("Voilà ta carte de dresseur :\n");
        sb.append("```\n");
        sb.append("┌");
        for (int i = 0; i < length - 2; i++) {
            sb.append("─");
        }
        sb.append("┐\n");
        sb.append("│ N° ID");
        for (int i = 0; i < length - 9 - noId.length(); i++) {
            sb.append(" ");
        }
        sb.append(noId).append(" │\n");
        sb.append("│ NOM");
        for (int i = 0; i < length - 7 - name.length(); i++) {
            sb.append(" ");
        }
        sb.append(name).append(" │\n");
        sb.append("│ POKéDEX");
        for (int i = 0; i < length - 11 - pokedex.length(); i++) {
            sb.append(" ");
        }
        sb.append(pokedex).append(" │\n");
        sb.append("│ ARGENT");
        for (int i = 0; i < length - 10 - money.length(); i++) {
            sb.append(" ");
        }
        sb.append(money).append(" │\n");
        sb.append("└");
        for (int i = 0; i < length - 2; i++) {
            sb.append("─");
        }
        sb.append("┘\n");
        sb.append("```\n");
        channel.sendMessage(sb.toString()).queue();
    }

    public void printRandomPokemon(MessageChannel channel) {
        PokemonInstance pokemon = pokedex.getRandomPokemon();
        String text = pokemon.toDisplayableString();
        channel.sendMessage(text).queue();
    }

    public void catchRandomPokemon(User user, MessageChannel channel) {
        Trainer trainer = trainerMap.get(user.getId());
        if (trainer == null) {
            channel.sendMessage("Tu dois d'abord t'enregistrer pour devenir un dresseur. Utilise la commande " +
                    "`:p register` pour ça !").queue();
            return;
        }

        PokemonInstance pokemon = pokedex.getRandomPokemon();
        int result = trainer.addPokemon(pokemon);
        String text = trainer.name + " a capturé un " + pokemon.getNameFr() + " de niveau " + pokemon.level + " !\n" +
                (result == 1 ? "Le Pokémon a été ajouté à l'équipe." : "L'équipe est pleine, le Pokémon a donc été envoyé au PC.");
        channel.sendMessage(text).queue();
    }

    public void team(User user, MessageChannel channel, String[] args) {
        Trainer trainer = trainerMap.get(user.getId());
        if (trainer == null) {
            channel.sendMessage("Tu dois d'abord t'enregistrer pour devenir un dresseur. Utilise la commande " +
                    "`:p register` pour ça !").queue();
            return;
        }

        if (args.length == 2) {
            channel.sendMessage(trainer.getTeamSummary()).queue();
            return;
        }

        String cmd = args[2];
        try {
            int index = Integer.parseInt(cmd);
            channel.sendMessage(trainer.getPokemonInTeam(index).toDisplayableString()).queue();
            return;
        } catch (NumberFormatException ignored) {
        }

        if (cmd.equals("switch")) {
            if (args.length < 5) {
                channel.sendMessage("Usage de la commande `switch` :\n```\n:p team switch <indexA> <indexB>\n```").queue();
                return;
            }
            try {
                int indexA = Integer.parseInt(args[3]);
                int indexB = Integer.parseInt(args[4]);
                if (Math.max(indexA, indexB) > trainer.team.size()) {
                    channel.sendMessage("Tu n'as que " + trainer.team.size() + " Pokémon dans ton équipe !").queue();
                    return;
                }
                trainer.teamSwitch(indexA, indexB);
                channel.sendMessage("L'ordre des Pokémon dans l'équipe a été modifié.").queue();
                return;
            } catch (NumberFormatException e) {
                channel.sendMessage("Utilise les positions des Pokémon dans ton équipe pour indiquer lesquels tu veux intervertir.").queue();
                return;
            }
        } else if (cmd.equals("drop")) {
            if (args.length < 4) {
                channel.sendMessage("Usage de la commande `drop` :\n```\n:p team drop <index>\n```").queue();
                return;
            }
            try {
                int index = Integer.parseInt(args[3]);
                if (index > trainer.team.size()) {
                    channel.sendMessage("Tu n'as que " + trainer.team.size() + " Pokémon dans ton équipe !").queue();
                    return;
                }
                trainer.dropPokemon(index);
                channel.sendMessage("Le Pokémon a été placé dans le PC.").queue();
                return;
            } catch (NumberFormatException e) {
                channel.sendMessage("Indique la position dans ton équipe du Pokémon que tu veux placer dans ton PC.").queue();
                return;
            }
        } else {
            channel.sendMessage("Désolé, je ne connais pas la commande `team " + cmd + "`. Utilise `:p help team` pour connaître les actions disponibles.").queue();
        }
    }

    public void pc(User user, MessageChannel channel, String[] args) {
        Trainer trainer = trainerMap.get(user.getId());
        if (trainer == null) {
            channel.sendMessage("Tu dois d'abord t'enregistrer pour devenir un dresseur. Utilise la commande " +
                    "`:p register` pour ça !").queue();
            return;
        }

        if (args.length == 2) {
            channel.sendMessage(trainer.getPcSummary()).queue();
            return;
        }

        String cmd = args[2];

        try {
            int index = Integer.parseInt(cmd);
            channel.sendMessage(trainer.getPokemonInPc(index).toDisplayableString()).queue();
            return;
        } catch (NumberFormatException ignored) {
        }

        if (cmd.equals("take")) {
            if (args.length < 4) {
                channel.sendMessage("Usage de la commande `take` :\n```\n:p pc take <indexPC>\n```").queue();
                return;
            }
            try {
                int index = Integer.parseInt(args[3]);
                if (index > trainer.pc.size()) {
                    channel.sendMessage("Tu n'as que " + trainer.team.size() + " Pokémon dans ton PC !").queue();
                    return;
                }
                trainer.takePokemon(index);
                channel.sendMessage("Le Pokémon a été retiré du PC et placé dans l'équipe.").queue();
                return;
            } catch (NumberFormatException e) {
                channel.sendMessage("Indique la position dans le PC du Pokémon que tu veux ajouter à ton équipe.").queue();
                return;
            }
        } else if (cmd.equals("free")) {
            if (args.length < 4) {
                channel.sendMessage("Usage de la commande `free` :\n```\n:p pc free <indexPC>\n```").queue();
                return;
            }
            try {
                int index = Integer.parseInt(args[3]);
                if (index > trainer.pc.size()) {
                    channel.sendMessage("Tu n'as que " + trainer.team.size() + " Pokémon dans ton PC !").queue();
                    return;
                }
                PokemonInstance pokemon = trainer.freePokemon(index);
                channel.sendMessage("Le Pokémon a été relâché. Bye-bye, " + pokemon.getNameFr() + " !").queue();
                return;
            } catch (NumberFormatException e) {
                channel.sendMessage("Indique la position dans le PC du Pokémon que tu veux relâcher.").queue();
                return;
            }
        } else {
            channel.sendMessage("Désolé, je ne connais pas la commande `pc " + cmd + "`. Utilise `:p help pc` pour connaître les actions disponibles.").queue();
        }
    }

    public void initMockBattle(User user, MessageChannel channel, String[] args) {
        Trainer trainer = trainerMap.get(user.getId());
        if (trainer == null) {
            channel.sendMessage("Tu dois d'abord t'enregistrer pour devenir un dresseur. Utilise la commande " +
                    "`:p register` pour ça !").queue();
            return;
        }

        int speciesIndex = Integer.parseInt(args[2]);
        int level = Integer.parseInt(args[3]);

        List<PokemonInstance> enemyTeam = new ArrayList<>();
        enemyTeam.add(pokedex.generatePokemon(speciesIndex, level));

        UserBattleInterface userInterface = new UserBattleInterface(user, trainer, channel);
        BattleInterface enemyInterface = new BasicAiBattleInterface();

        Battle battle = new Battle(trainer.team, enemyTeam, userInterface, enemyInterface, BattleType.WILD_POKEMON);
        trainer.battleInterface = userInterface;
        statusMap.put(user.getId(), UserStatus.BATTLE);

        channel.sendMessage(trainer.name + " débute le combat contre un " + enemyTeam.get(0).getNameFr() + " sauvage !").queue();
    }
}
