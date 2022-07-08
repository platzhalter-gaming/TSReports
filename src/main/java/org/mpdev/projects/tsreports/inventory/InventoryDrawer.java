package org.mpdev.projects.tsreports.inventory;

import dev.simplix.protocolize.api.Location;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.data.Sound;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.TSReports;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryDrawer {

    private static final TSReports plugin = TSReports.getInstance();
    private static final ConcurrentHashMap<UUID, UIFrame> OPENING = new ConcurrentHashMap<>();

    public static void open(UIFrame frame) {
        if (frame == null) {
            return;
        }
        UUID uuid = frame.getViewer().getUniqueId();
        if (frame.equals(OPENING.get(uuid))) {
            return;
        }

        OPENING.put(uuid, frame);
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            Inventory inventory = prepareInventory(frame);

            if (!frame.equals(OPENING.get(uuid))) {
                return;
            }
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                Protocolize.playerProvider().player(frame.getViewer().getUniqueId()).openInventory(inventory);
                InventoryController.register(frame);
                OPENING.remove(uuid);
            });
        });
    }

    public static void close(UIFrame frame) {
        if (frame == null) {
            return;
        }
        UUID uuid = frame.getViewer().getUniqueId();

        OPENING.remove(uuid);
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            Protocolize.playerProvider().player(frame.getViewer().getUniqueId()).closeInventory();
        });
    }

    public static void playSound(ProxiedPlayer player, Sound sound, SoundCategory category) {
        Location location = Protocolize.playerProvider().player(player.getUniqueId()).location();
        Protocolize.playerProvider().player(player.getUniqueId())
                .playSound(location, sound, category, 1, 1);
    }

    private static Inventory prepareInventory(UIFrame frame) {
        Inventory inventory = new Inventory(InventoryType.chestInventoryWithSize(frame.getSize()));
        inventory.title(frame.getTitle());
        long start = System.currentTimeMillis();
        setComponents(inventory, frame);

        TSReports.getInstance().debug(String.format("It took %s millisecond(s) to load the frame %s for %s",
                System.currentTimeMillis() - start, frame.getTitle(), frame.getViewer().getName()));

        inventory.onClick(click -> plugin.getInventoryController().onInteract(click));
        inventory.onClose(inventoryClose -> plugin.getInventoryController().onClose(inventoryClose));

        return inventory;
    }

    private static void setComponents(Inventory inventory, UIFrame frame) {
        frame.clear();
        try {
            frame.createComponents();
        } catch (NoSuchFieldError ex) {
            return;
        }

        Set<UIComponent> components = frame.getComponents();
        if (components.isEmpty()) {
            plugin.getLogger().warning(String.format("Frame %s has no components", frame.getTitle()));
            return;
        }
        for (UIComponent c : frame.getComponents()) {
            if (c.getSlot() >= frame.getSize()) {
                continue;
            }
            inventory.item(c.getSlot(), c.getItem());
        }
    }
}
