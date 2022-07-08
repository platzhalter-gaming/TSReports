package org.mpdev.projects.tsreports.inventory;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Paginator;

public class Components {

    public static UIComponent getBarrierComponent(int slot, ProxiedPlayer viewer) {
        return new UIComponentImpl.Builder(ItemType.GRAY_STAINED_GLASS_PANE)
                .name(Messages.GUI_BARRIER_NAME.getString(viewer.getName()))
                .slot(slot)
                .build();
    }

    public static UIComponent getBackComponent(UIFrame parent, int slot, ProxiedPlayer viewer) {
        UIComponent back = new UIComponentImpl.Builder(ItemType.ARROW)
                .name(Messages.GUI_BACKBUTTON_NAME.getString(viewer.getName()))
                .slot(slot)
                .build();
        back.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(parent));
        return back;
    }

    public static UIComponent getAirComponent(int slot) {
        return new UIComponentImpl.Builder(ItemType.AIR)
                .name("")
                .slot(slot)
                .build();
    }

    public static UIComponent getPreviousPageComponent(int slot, Runnable listener, Paginator paginator, ProxiedPlayer viewer) {
        if (!paginator.hasPreviousPage()) {
            return getAirComponent(slot);
        }
        UIComponent c = new UIComponentImpl.Builder(ItemType.FEATHER)
                .name(Messages.GUI_MANAGEREPORTS_PREVIOUS_NAME.getString(viewer.getName()))
                .slot(slot)
                .build();
        setOneTimeUseListener(c, listener);
        return c;
    }

    public static UIComponent getNextPageComponent(int slot, Runnable listener, Paginator paginator, ProxiedPlayer viewer) {
        if (!paginator.hasNextPage()) {
            return getAirComponent(slot);
        }
        UIComponent c = new UIComponentImpl.Builder(ItemType.FEATHER)
                .name(Messages.GUI_MANAGEREPORTS_NEXT_NAME.getString(viewer.getName()))
                .slot(slot)
                .build();
        setOneTimeUseListener(c, listener);
        return c;
    }

    private static void setOneTimeUseListener(UIComponent c, Runnable listener) {
        c.setListener(ClickType.LEFT_CLICK, () -> {
            if (listener != null) {
                listener.run();
            }
            c.setListener(ClickType.LEFT_CLICK, null);
        });
    }

}
