package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.inventory.InventoryDrawer;
import org.mpdev.projects.tsreports.inventory.UIComponent;
import org.mpdev.projects.tsreports.inventory.UIComponentImpl;
import org.mpdev.projects.tsreports.inventory.UIFrame;
import org.mpdev.projects.tsreports.utils.Messages;

public class ConfirmationFrame extends UIFrame {

    private final Runnable listener;

    public ConfirmationFrame(UIFrame parent, ProxiedPlayer viewer, Runnable listener) {
        super(parent, viewer);
        this.listener = listener;
    }

    @Override
    public String getTitle() {
        return Messages.GUI_CONFIRMATION_TITLE.getString(getViewer().getName());
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public void createComponents() {
        UIComponent confirmComponent = new UIComponentImpl.Builder(ItemType.LIME_WOOL)
                .name(Messages.GUI_CONFIRMATION_CONFIRM_NAME.getString(getViewer().getName())).slot(12).build();
        confirmComponent.setListener(ClickType.LEFT_CLICK, listener);
        add(confirmComponent);

        UIComponent returnComponent = new UIComponentImpl.Builder(ItemType.RED_WOOL)
                .name(Messages.GUI_CONFIRMATION_RETURN_NAME.getString(getViewer().getName())).slot(14).build();
        returnComponent.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(getParent()));
        add(returnComponent);
    }

}
