package dragon.me.solar.hooks.papi;

import dragon.me.solar.Solar;
import dragon.me.solar.hooks.papi.handlers.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolarExpansion extends PlaceholderExpansion {

    private final Solar instance;
    private final Map<String, PapiHandler> handlers = new HashMap<>();

    public SolarExpansion(Solar instance) {
        this.instance = instance;

        register(new WinPapiHandler());
        register(new LossesPapiHandler());
        register(new WinratePapiHandler());
        register(new WlrPapiHandler());
        register(new EloPapiHandler());
        register(new MatchPapiHandler());
        register(new InQueuePapiHandler());
        register(new TeamQueuePapiHandler());
    }

    private void register(PapiHandler handler) {
        handlers.put(handler.identifier().toLowerCase(Locale.ROOT), handler);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "solar";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DragonPvp";
    }

    @Override
    public @NotNull String getVersion() {
        return instance.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        Solar.instance
                .getLogger()
                .info("[PAPI DEBUG] onRequest params='" + params + "' player="
                        + (player == null ? "null" : player.getName()));

        if (player == null) {
            return null;
        }

        ParsedPlaceholder parsed = parse(params);

        Solar.instance
                .getLogger()
                .info("[PAPI DEBUG] identifier='" + parsed.identifier() + "' arguments='" + parsed.arguments() + "'");

        PapiHandler handler = handlers.get(parsed.identifier());

        Solar.instance
                .getLogger()
                .info("[PAPI DEBUG] handler="
                        + (handler == null ? "null" : handler.getClass().getSimpleName()));

        if (handler == null) {
            return null;
        }

        return handler.handleOffline(player, parsed.arguments());
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        Solar.instance
                .getLogger()
                .info("[PAPI DEBUG] onPlaceholderRequest params='" + params + "' player="
                        + (player == null ? "null" : player.getName()));

        if (player == null) {
            return null;
        }

        ParsedPlaceholder parsed = parse(params);

        Solar.instance
                .getLogger()
                .info("[PAPI DEBUG] identifier='" + parsed.identifier() + "' arguments='" + parsed.arguments() + "'");

        PapiHandler handler = handlers.get(parsed.identifier());

        Solar.instance
                .getLogger()
                .info("[PAPI DEBUG] handler="
                        + (handler == null ? "null" : handler.getClass().getSimpleName()));

        if (handler == null) {
            return null;
        }

        return handler.handlePlayer(player, parsed.arguments());
    }

    private ParsedPlaceholder parse(String params) {

        int separator = params.indexOf(':');

        if (separator == -1) {
            return new ParsedPlaceholder(params.toLowerCase(Locale.ROOT), "");
        }

        String identifier = params.substring(0, separator).toLowerCase(Locale.ROOT);

        String arguments = params.substring(separator + 1);

        return new ParsedPlaceholder(identifier, arguments);
    }

    private record ParsedPlaceholder(String identifier, String arguments) {}
}
