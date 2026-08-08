package dragon.me.solar.configs;

import dragon.me.solar.Solar;

import dragon.me.solar.configs.utils.ItemStackSerializer;
import dragon.me.solar.configs.utils.PotionEffectSerializer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class ConfigLoader<T> {

    private final Class<T> type;
    private final Path path;

    private YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public ConfigLoader(Path path, Class<T> type) {
        this.path = path;
        this.type = type;
    }

    public T load(T defaults) throws ConfigurateException {

        TypeSerializerCollection serializers =
                TypeSerializerCollection.builder()
                        .registerAll(TypeSerializerCollection.defaults())
                        .register(ItemStack.class, new ItemStackSerializer())

                        .register(PotionEffect.class, new PotionEffectSerializer())
                        .build();

        loader =
                YamlConfigurationLoader.builder()
                        .path(path)
                        .indent(4)
                        .nodeStyle(NodeStyle.BLOCK)
                        .defaultOptions(op -> op.serializers(serializers))
                        .build();

        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            root = loader.createNode();
            root.set(type, defaults);
            loader.save(root);

            return defaults;
        }

        root = loader.load();

        T config = root.get(type);

        if (config == null) {
            config = defaults;
            root.set(type, config);
            loader.save(root);
        }

        return config;
    }

    public void save(T config) throws ConfigurateException {
        root.set(type, config);
        loader.save(root);
        Solar.instance.getLogger().info("Saved config file: " + path.getFileName());
    }
}
