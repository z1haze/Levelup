package me.z1haze.levelup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.z1haze.levelup.storage.DataAccessor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final LevelUp instance = LevelUp.getInstance();
    private final Map<UUID, LevelUpPlayer> playerData = new HashMap<>();
    private final String directory = "playerdata";
    private final DataAccessor da;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public PlayerDataManager() {
        this.da = new DataAccessor(instance.getDataFolder().getPath());
        da.makeDirectory(directory);
    }

    public LevelUpPlayer getPlayerData(UUID uuid) {
        LevelUpPlayer lplayer = playerData.get(uuid);
        if (lplayer != null) return lplayer;

        if (!da.exists(directory + File.separator + uuid + ".json")) return null;

        return gson.fromJson(da.read(directory + File.separator + uuid + ".json"), LevelUpPlayer.class);
    }

    public void savePlayerData(LevelUpPlayer lplayer) {
        String path = directory + File.separator + lplayer.uuid + ".json";

        da.deleteFile(path);
        da.write(path, gson.toJson(lplayer));
    }

    public void playerJoined(Player p) {
        LevelUpPlayer lplayer;

        if (da.exists(directory + File.separator + p.getUniqueId() + ".json")) {
            lplayer = gson.fromJson(da.read(directory + File.separator + p.getUniqueId() + ".json"), LevelUpPlayer.class);

            // keep name updated
            if (!p.getName().equals(lplayer.name)) {
                lplayer.name = p.getName();
                da.deleteFile(directory + File.separator + p.getUniqueId() + ".json");
                da.write(directory + File.separator + p.getUniqueId() + ".json", gson.toJson(lplayer));
            }
        } else {
            lplayer = new LevelUpPlayer(p, da.getFiles(directory).size() + 1);

            da.create(directory + File.separator + p.getUniqueId() + ".json");
            da.write(directory + File.separator + p.getUniqueId() + ".json", gson.toJson(lplayer));
        }

        playerData.put(p.getUniqueId(), lplayer);
    }

    public void playerQuit(Player p) {
        LevelUpPlayer lplayer = getPlayerData(p.getUniqueId());
        if (lplayer == null) return;

        // TODO: add last seen time to player profile
//        savePlayerData(lplayer);
        playerData.remove(p.getUniqueId());
    }
}
