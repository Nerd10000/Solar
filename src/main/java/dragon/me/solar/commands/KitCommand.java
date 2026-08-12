package dragon.me.solar.commands;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.records.KitFlagsRecord;
import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.suggestion.Suggestions;

public final class KitCommand {

    @Command("kit create <id>")
    @Permission("solar.kit.manage.create")
    public void create(
            CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String id) {

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

        InMemoryKit kit = new InMemoryKit(id, null, List.of(), null, null, KitFlagsRecord.DEFAULT);

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
    public void setItems(
            CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String id) {

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

        ItemStack offhand =
                playerSender.getInventory().getItemInOffHand().getType().equals(Material.AIR)
                        ? null
                        : playerSender.getInventory().getItemInOffHand();

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
    public void setEffects(
            CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String id) {

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
    public void finalize(
            CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String id) {

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
            @Argument(value = "id", suggestions = "kits") String id,
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

        Solar.kitManager.applyKit(player, kit);
    }

    @Command("kit setFlags <kit>")
    @Permission("solar.setflags")
    public void setFlags(
            CommandSourceStack stack,
            @Argument(value = "kit", suggestions = "kits") String kit,
            @Flag("max-health") @Default("20") Float maxHealth,
            @Flag("natural-regeneration") @Default("true") Boolean naturalRegeneration,
            @Flag("natural-saturation") @Default("true") Boolean naturalSaturation,
            @Flag("hunger-loss") @Default("true") Boolean hungerLoss,
            @Flag("pearl-cooldown") @Default("-1") @Range(min = "-1", max = "20")
                    Integer pearlCooldown,
            @Flag("golden-apple-cooldown") @Default("-1") @Range(min = "-1", max = "20")
                    Integer goldenAppleCooldown,
            @Flag("windcharge-cooldown") @Default("-1") @Range(min = "-1", max = "20")
                    Integer windChargeCooldown,
            @Flag("prevent-block-place") @Default("false") Boolean preventBlockPlace,
            @Flag("prevent-block-break") @Default("false") Boolean preventBlockBreak,
            @Flag("prevent-item-drop") @Default("false") Boolean preventItemDrop,
            @Flag("prevent-movement-before-start") @Default("false")
                    Boolean preventMovementBeforeStart) {

        if (stack.getSender() instanceof Player p) {

            InMemoryKit inMemoryKit = Solar.kitManager.getKit(kit);

            if (inMemoryKit == null) {
                p.sendMessage(
                        Solar.miniMessage.deserialize(
                                Solar.configManager.languageRecord().kitNotFound(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix()),
                                Placeholder.parsed("kit", kit)));
                return;
            }
            float finalMaxHealth = maxHealth != null ? maxHealth : 20.0f;
            boolean finalNaturalRegeneration =
                    naturalRegeneration != null ? naturalRegeneration : true;
            boolean finalNaturalSaturation = naturalSaturation != null ? naturalSaturation : true;
            boolean finalHungerLoss = hungerLoss != null ? hungerLoss : true;

            int finalPearlCooldown = pearlCooldown != null ? pearlCooldown : -1;
            int finalGoldenAppleCooldown = goldenAppleCooldown != null ? goldenAppleCooldown : -1;
            int finalWindChargeCooldown = windChargeCooldown != null ? windChargeCooldown : -1;

            boolean finalPreventBlockPlace = preventBlockPlace != null && preventBlockPlace;
            boolean finalPreventBlockBreak = preventBlockBreak != null && preventBlockBreak;
            boolean finalPreventItemDrop = preventItemDrop != null && preventItemDrop;
            boolean finalPreventMovementBeforeStart =
                    preventMovementBeforeStart != null && preventMovementBeforeStart;

            inMemoryKit.flags =
                    new KitFlagsRecord(
                            finalMaxHealth,
                            finalNaturalRegeneration,
                            finalNaturalSaturation,
                            finalHungerLoss,
                            finalPearlCooldown,
                            finalGoldenAppleCooldown,
                            finalWindChargeCooldown,
                            finalPreventBlockPlace,
                            finalPreventBlockBreak,
                            finalPreventItemDrop,
                            finalPreventMovementBeforeStart);

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().kitFlagsSet(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", kit)));

        } else {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }
    }

    @Suggestions("kits")
    public List<String> kitSuggestions() {

        return Solar.kitManager.KITS.keySet().stream().toList();
    }
}
