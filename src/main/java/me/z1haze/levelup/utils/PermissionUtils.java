package me.z1haze.levelup.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.track.Track;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PermissionUtils {
    public static Map<String, Group> getUsersGroupsFromTrack(LuckPerms luckperms, User user, String trackName) {
        Track track = luckperms.getTrackManager().getTrack(trackName);

        if (track == null) return null;

        return user.getNodes(NodeType.INHERITANCE).stream()
                .filter(n -> track.containsGroup(n.getGroupName()))
                .distinct()
                .map(n -> luckperms.getGroupManager().getGroup(n.getGroupName()))
                .collect(Collectors.toMap(Group::getName, Function.identity()));
    }

    public static Group getCurrentGroupOnTrackForUser(LuckPerms luckperms, User user, String trackName) {
        Track track = luckperms.getTrackManager().getTrack(trackName);

        if (track == null) {
            return null;
        }

        List<Group> groups = user.getNodes(NodeType.INHERITANCE).stream()
                .filter(n -> track.containsGroup(n.getGroupName()))
                .distinct()
                .map(n -> luckperms.getGroupManager().getGroup(n.getGroupName()))
                .collect(Collectors.toList());

        if (groups.size() == 0) {
            return null;
        }

        return groups.get(0);
    }
}
