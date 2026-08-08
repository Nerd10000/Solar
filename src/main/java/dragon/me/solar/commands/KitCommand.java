package dragon.me.solar.commands;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffect;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public final class KitCommand {

    @Command("kit create <id>")
    @Permission("solar.kit.manage.create")
    public void create(CommandSourceStack stack, @Argument("id") String id) {

        if (!(stack.getSender() instanceof Player playerSender)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryKit kit = new InMemoryKit(id,  null, List.of(), null, null);

        if (Solar.kitManager.create(kit)) {
            playerSender.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().kitCreated(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", id)));
        } else {
            playerSender.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().kitExists(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", id)));
        }
    }

    @Command("kit set-items <id>")
    @Permission("solar.kit.manage.set-items")
    public void setItems(CommandSourceStack stack, @Argument("id") String id) {

        if (!(stack.getSender() instanceof Player playerSender)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        ItemStack[] items = playerSender.getInventory().getStorageContents();

        ItemStack[] armor = playerSender.getInventory().getArmorContents();


        ItemStack offhand = playerSender.getInventory().getItemInOffHand().getType().equals(Material.AIR) ? null : playerSender.getInventory().getItemInOffHand();

        InMemoryKit inMemoryKit = Solar.kitManager.getKit(id);

        if (inMemoryKit == null) {
            playerSender.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().kitNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", id)));
            return;
        }

        inMemoryKit.items = items;
        inMemoryKit.armor = armor;
        inMemoryKit.offhand = offhand;

        playerSender.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().kitItemsSet(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("kit", id)));
    }

    @Command("kit set-effects <id>")
    @Permission("solar.kit.manage.set-effects")
    public void setEffects(CommandSourceStack stack, @Argument("id") String id) {

        if (!(stack.getSender() instanceof Player playerSender)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        List<PotionEffect> effects =
                new java.util.ArrayList<>(playerSender.getActivePotionEffects());

        InMemoryKit inMemoryKit = Solar.kitManager.getKit(id);

        if (inMemoryKit == null) {
            playerSender.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().kitNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", id)));
            return;
        }

        inMemoryKit.effectList = effects;

        playerSender.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().kitEffectsSet(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("kit", id)));
    }

    @Command("kit finalize <id>")
    @Permission("solar.kit.manage.finalize")
    public void finalize(CommandSourceStack stack, @Argument("id") String id) {

        if (!(stack.getSender() instanceof Player playerSender)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryKit memoryKit = Solar.kitManager.getKit(id);

        if (memoryKit == null) {
            playerSender.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().kitNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", id)));

            return;
        }

        KitRecord record = memoryKit.toRecord();
        Solar.configManager.registerKit(record);

        playerSender.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().kitFinalized(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("kit", id)));
    }

    @Command("kit give <id> <player>")
    @Permission("solar.kit.manage.give")
    public void give(
            CommandSourceStack stack,
            @Argument("id") String id,
            @Argument("player") Player player) {

        if (player == null) return;

        InMemoryKit kit = Solar.kitManager.getKit(id);

        if (kit == null) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().kitNotFound(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix()),
                                    Placeholder.parsed("kit", id)));

            return;
        }

        player.getInventory().clear();

        if (kit.items != null) {

            player.getInventory().setStorageContents(kit.items);


            player.getInventory().setArmorContents(kit.armor);

           player.getInventory().setItemInOffHand(kit.offhand);

        }

        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }

        if (kit.effectList != null) {
            for (PotionEffect e : kit.effectList) {
                player.addPotionEffect(e);
            }
        }
    }
}
