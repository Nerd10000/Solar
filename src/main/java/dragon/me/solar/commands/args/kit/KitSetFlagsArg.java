package dragon.me.solar.commands.args.kit;

import dragon.me.solar.configs.records.KitFlagsRecord;
import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;

public class KitSetFlagsArg {
    private final KitCommandContext context;

    public KitSetFlagsArg(KitCommandContext context) {
        this.context = context;
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
            @Flag("pearl-cooldown") @Default("-1") @Range(min = "-1", max = "20") Integer pearlCooldown,
            @Flag("golden-apple-cooldown") @Default("-1") @Range(min = "-1", max = "20") Integer goldenAppleCooldown,
            @Flag("windcharge-cooldown") @Default("-1") @Range(min = "-1", max = "20") Integer windChargeCooldown,
            @Flag("prevent-block-place") @Default("false") Boolean preventBlockPlace,
            @Flag("prevent-block-break") @Default("false") Boolean preventBlockBreak,
            @Flag("prevent-item-drop") @Default("false") Boolean preventItemDrop,
            @Flag("prevent-movement-before-start") @Default("false") Boolean preventMovementBeforeStart) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }

        InMemoryKit inMemoryKit = context.kitManager().getKit(kit);
        if (inMemoryKit == null) {
            context.send(player, context.messages().language().kitNotFound(), Placeholder.parsed("kit", kit));
            return;
        }

        inMemoryKit.flags = new KitFlagsRecord(
                maxHealth != null ? maxHealth : 20.0f,
                naturalRegeneration == null || naturalRegeneration,
                naturalSaturation == null || naturalSaturation,
                hungerLoss == null || hungerLoss,
                pearlCooldown != null ? pearlCooldown : -1,
                goldenAppleCooldown != null ? goldenAppleCooldown : -1,
                windChargeCooldown != null ? windChargeCooldown : -1,
                preventBlockPlace != null && preventBlockPlace,
                preventBlockBreak != null && preventBlockBreak,
                preventItemDrop != null && preventItemDrop,
                preventMovementBeforeStart != null && preventMovementBeforeStart);
        context.send(player, context.messages().language().kitFlagsSet(), Placeholder.parsed("kit", kit));
    }
}
