package dragon.me.solar.commands;

import dragon.me.solar.Solar;
import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.utils.SoundUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public class PartyCommand {

    @Command("party create")
    public void create(CommandSourceStack stack) {

        if (!(stack.getSender() instanceof Player p)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        if (Solar.partyManager.isMemberOfAParty(p.getUniqueId())) {

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().cantCreatePartyAsMemberOfAParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        InMemoryParty party = new InMemoryParty(UUID.randomUUID(), p.getUniqueId());

        Solar.partyManager.addParty(party);

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().partyCreated(),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));
    }

    @Command("party leave")
    public void leave(CommandSourceStack stack) {

        if (!(stack.getSender() instanceof Player p)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        if (Solar.partyManager.isMemberOfAParty(p.getUniqueId())) {

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().cantCreatePartyAsMemberOfAParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        InMemoryParty party = new InMemoryParty(UUID.randomUUID(), p.getUniqueId());

        Solar.partyManager.addParty(party);

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().partyCreated(),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));
    }

    @Command("party disband")
    public void disband(CommandSourceStack stack) {

        if (!(stack.getSender() instanceof Player p)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }
        InMemoryParty party = Solar.partyManager.getByOwner(p.getUniqueId());

        if (party == null) {

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().notInParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        if (!Solar.partyManager.isMemberOfAParty(p.getUniqueId())) {

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().cantCreatePartyAsMemberOfAParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        if (!party.getOwner().equals(p.getUniqueId())) {

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().cantDoPartyAsMemberOnly(),
                            Placeholder.parsed("action", "disband"),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        Solar.partyManager.removeParty(party);

        Solar.partyManager.executeForEachMember(
                party,
                player -> {
                    player.sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().partyDisbanded(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix()),
                                    Placeholder.parsed("owner", p.getName())));

                    SoundUtils.playConfiguredSound(
                            player,
                            Solar.configManager.settingsRecord().soundRecords().partyDisbanded(),
                            Sound.ENTITY_GOAT_HORN_BREAK);
                });
    }
}
