package org.mpdev.projects.tsreports.utils;

import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.objects.Node;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Luck {

    public static void addPermission(UUID userUuid, Node node) {
        // Load, modify, then save
        TSReports.getInstance().getApi().getUserManager().modifyUser(userUuid, user -> {
            // Add the permission
            user.data().add(PermissionNode.builder(node.permission()).build());
        });
    }

    public static CachedPermissionData getOfflineData(UUID uuid) {
        UserManager userManager = TSReports.getInstance().getApi().getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(uuid);
        QueryOptions queryOptions = TSReports.getInstance().getApi().getContextManager().getQueryOptions(userFuture);
        return (CachedPermissionData) userFuture.thenAcceptAsync(user -> user.getCachedData().getPermissionData(queryOptions));
    }

    public static CachedPermissionData getPermissionData(ProxiedPlayer player) {
        if (player.isConnected()) {
            User user = TSReports.getInstance().getApi().getPlayerAdapter(ProxiedPlayer.class).getUser(player);
            QueryOptions queryOptions = TSReports.getInstance().getApi().getContextManager().getQueryOptions(player);
            return user.getCachedData().getPermissionData(queryOptions);
        }
        return getOfflineData(player.getUniqueId());
    }

}
