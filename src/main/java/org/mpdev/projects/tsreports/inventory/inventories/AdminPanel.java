package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.inventory.*;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class AdminPanel extends UIFrame {

    public AdminPanel(UIFrame parent, ProxiedPlayer viewer) {
        super(parent, viewer);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_ADMINPANEL_TITLE.getString(getViewer().getName());
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @Override
    public void createComponents() {
        add(Components.getBackComponent(getParent(), 26, getViewer()));
        addReloadButton();
        addStatusButton();
        addClearDatabaseButton();
    }

    private void addStatusButton() {
        List<String> lore = Messages.GUI_ADMINPANEL_STATUS_LORE.getStringList(getViewer().getName())
                .stream().map(s -> Utils.replaceStatusPlaceholders(s, getViewer()))
                .collect(Collectors.toList());
        UIComponent component = new UIComponentImpl.Builder(ItemType.GREEN_STAINED_GLASS_PANE)
                .lore(lore)
                .name(Messages.GUI_ADMINPANEL_STATUS_NAME.getString(getViewer().getName()))
                .slot(13)
                .build();
        add(component);
    }

    private void addClearDatabaseButton() {
        UIComponent component = new UIComponentImpl.Builder(ItemType.RED_DYE)
                .name(Messages.GUI_ADMINPANEL_CLEAR_DATABASE_NAME.getString(getViewer().getName()))
                .slot(15).build();
        component.setPermission(ClickType.LEFT_CLICK, "tsreports.clear");
        component.setConfirmationRequired(ClickType.LEFT_CLICK);
        component.setListener(ClickType.LEFT_CLICK, () -> {
            InventoryController.runCommand(getViewer(), "reports", true, "clear");
        });
        add(component);
    }

    private void addReloadButton() {
        UIComponent reloadButton = new UIComponentImpl.Builder(ItemType.LIME_DYE)
                .name(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(getViewer().getName()))
                .slot(11).build();
        add(reloadButton);
        reloadButton.setPermission(ClickType.LEFT_CLICK,"tsreports.reload");
        reloadButton.setListener(ClickType.LEFT_CLICK, ()-> {
            InventoryController.runCommand(getViewer(), "reports", true, "reload");
        });
    }

}
