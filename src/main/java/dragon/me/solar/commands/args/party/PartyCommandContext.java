package dragon.me.solar.commands.args.party;

import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.configs.records.LanguageRecord;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.party.PartyManager;
import dragon.me.solar.party.invite.PartyInviteManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

public record PartyCommandContext(
        PartyManager partyManager,
        PartyInviteManager partyInviteManager,
        ConfigManager configManager,
        MiniMessage miniMessage,
        MessageService messages) {

    public LanguageRecord language() {
        return configManager.languageRecord();
    }

    public void send(
            org.bukkit.entity.Player player,
            String template,
            net.kyori.adventure.text.minimessage.tag.resolver.TagResolver... placeholders) {
        messages.send(player, template, placeholders);
    }

    public void sendConsoleError(io.papermc.paper.command.brigadier.CommandSourceStack stack) {
        messages.sendConsoleError(stack);
    }
}
