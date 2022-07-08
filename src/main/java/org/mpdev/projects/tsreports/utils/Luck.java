package org.mpdev.projects.tsreports.utils;

import net.luckperms.api.node.types.PermissionNode;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.objects.Node;

import java.util.UUID;

public class Luck {

    public static void addPermission(UUID userUuid, Node node) {
        // Load, modify, then save
        TSReports.getInstance().getApi().getUserManager().modifyUser(userUuid, user -> {
            // Add the permission
            user.data().add(PermissionNode.builder(node.permission()).build());
        });
    }

}
