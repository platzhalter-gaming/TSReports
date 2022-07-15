package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.inventory.*;
import org.mpdev.projects.tsreports.managers.ConfigManager;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LangSelector extends UIFrame {

    TSReports plugin = TSReports.getInstance();
    ConfigManager configManager = plugin.getConfigManager();

    public LangSelector(UIFrame parent, ProxiedPlayer viewer) {
        super(parent, viewer);
    }

    @Override
    public String getTitle() {
        Locale viewerLocale = plugin.getOfflinePlayers().get(getViewer().getName()).getLocale();
        return Messages.GUI_LANGUAGESELECTOR_TITLE.getString(getViewer().getName())
                .replace("{0}", viewerLocale.toString());    }

    @Override
    public int getSize() {
        return 9*6;
    }

    @Override
    public void createComponents() {
        add(Components.getBackComponent(getParent(), 53, getViewer()));

        List<String> localeNames = configManager.getAvailableLocales().stream().map(Locale::toString).sorted().collect(Collectors.toList());
        for (int i = 0; i < localeNames.size(); i++){
            UIComponent component = new UIComponentImpl.Builder(ItemType.PAPER)
                    .name(localeNames.get(i))
                    .slot(i)
                    .build();
            component.setListener(ClickType.LEFT_CLICK, () -> {
                String name = ChatColor.stripColor(component.getItem().displayName(true));
                Locale newLocale = Utils.stringToLocale(name);
                plugin.getStorageManager().updateLanguage(getViewer().getUniqueId(), newLocale);
                plugin.getOfflinePlayers().get(getViewer().getName()).setLocale(newLocale);
                Utils.sendText(getViewer(), "command-messages.interlanguage", message -> message.replace("%language%", newLocale.toString()));
                updateFrame();
            });
            add(component);
        }
    }

    private void updateFrame() {
        InventoryDrawer.open(this);
    }

}
