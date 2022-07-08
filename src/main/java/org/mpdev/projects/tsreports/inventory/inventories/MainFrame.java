package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.inventory.InventoryDrawer;
import org.mpdev.projects.tsreports.inventory.UIComponent;
import org.mpdev.projects.tsreports.inventory.UIComponentImpl;
import org.mpdev.projects.tsreports.inventory.UIFrame;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Utils;

public class MainFrame extends UIFrame {

    public MainFrame(ProxiedPlayer viewer) {
        super(null, viewer);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_MAIN_TITLE.getString(getViewer().getName());
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @Override
    public void createComponents() {
        addLanguageSelector();
        addManageReports();
        addAdminPanel();
    }

    private void addLanguageSelector() {
        if (!TSReports.getInstance().getConfig().getBoolean("gui.languageselector")) return;
        UIComponent languageSelector = new UIComponentImpl.Builder(ItemType.WHITE_BANNER)
                .name(Messages.GUI_MAIN_LANGUAGESELECTOR_NAME.getString(getViewer().getName()))
                .slot(11).build();
        languageSelector.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(new LangSelector(this, getViewer())));
        add(languageSelector);
    }

    private void addManageReports() {
        if (!TSReports.getInstance().getConfig().getBoolean("gui.managereports")) return;
        if (!Utils.hasPermission(getViewer(), "tsreports.gui.managereports")) return;
        UIComponent manageReports = new UIComponentImpl.Builder(ItemType.DIAMOND_AXE)
                .name(Messages.GUI_MAIN_MANAGEREPORTS_NAME.getString(getViewer().getName()))
                .slot(13).build();
        manageReports.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(new ManageReports(this, getViewer())));
        add(manageReports);
    }

    private void addAdminPanel() {
        if (!TSReports.getInstance().getConfig().getBoolean("gui.adminpanel")) return;
        if (!Utils.hasPermission(getViewer(), "tsreports.gui.admin")) return;
        UIComponent adminPanel = new UIComponentImpl.Builder(ItemType.COMMAND_BLOCK)
                .name(Messages.GUI_MAIN_ADMINPANEL_NAME.getString(getViewer().getName()))
                .slot(15).build();
        adminPanel.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(new AdminPanel(this, getViewer())));
        add(adminPanel);
    }

}
