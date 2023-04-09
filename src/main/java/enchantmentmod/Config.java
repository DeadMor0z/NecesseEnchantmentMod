package enchantmentmod;

import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.GlobalData;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

import java.io.File;

public class Config {
    public static Integer minAmount = 8;
    public static Integer maxAmount = 40;
    public static Integer minBossAmount = 16;
    public static Integer maxBossAmount = 80;
    public static Integer oneOrbAreNShards = 100;
    public static Integer enchantmentCosts = 10;
    public static Integer luckyBoxMin = 50;
    public static Integer luckyBoxMax = 100;
    public static final File file = new File(GlobalData.cfgPath() + "betterenchantment.cfg");
    public static String serverName = "default";
    private static final Config OBJ = new Config();
    private Config () {
        Config.loadAndSave(false);

        GameEvents.addListener(ServerStartEvent.class, new GameEventListener<ServerStartEvent>() {
            @Override
            public void onEvent(ServerStartEvent e) {
                Config.serverName = e.server.world.displayName + "@" + Long.toHexString(e.server.world.getUniqueID());
                Config.loadAndSave(false);
            }
        });

        GameEvents.addListener(ServerStopEvent.class, new GameEventListener<ServerStopEvent>() {
            @Override
            public void onEvent(ServerStopEvent e) {
                Config.serverName = "default";
                Config.loadAndSave(false);
            }
        });
    }

    public static void loadAndSave(boolean skipLoading) {
        SaveData saveFile = new SaveData("DEFAULT");
        if (Config.file.exists()) {
            LoadData save = new LoadData(Config.file);
            for (LoadData saveData : save.getLoadData()) {
                if (!Config.serverName.equals(saveData.getName())) {
                    saveFile.addSaveData(saveData.toSaveData());
                    continue;
                }
                if (skipLoading) {
                    continue;
                }
                Config.minAmount = saveData.getInt("minAmount", Config.minAmount);
                Config.maxAmount = saveData.getInt("maxAmount", Config.maxAmount);
                Config.minBossAmount = saveData.getInt("minBossAmount", Config.minBossAmount);
                Config.maxBossAmount = saveData.getInt("maxBossAmount", Config.maxBossAmount);
                Config.oneOrbAreNShards = saveData.getInt("oneOrbAreNShards", Config.oneOrbAreNShards);
                Config.enchantmentCosts = saveData.getInt("enchantmentCosts", Config.enchantmentCosts);
                Config.luckyBoxMin = saveData.getInt("luckyBoxMin", Config.luckyBoxMin);
                Config.luckyBoxMax = saveData.getInt("luckyBoxMax", Config.luckyBoxMax);
            }
        }

        SaveData defaultSave = new SaveData(Config.serverName);
        defaultSave.addInt("minAmount", Config.minAmount);
        defaultSave.addInt("maxAmount", Config.maxAmount);
        defaultSave.addInt("minBossAmount", Config.minBossAmount);
        defaultSave.addInt("maxBossAmount", Config.maxBossAmount);
        defaultSave.addInt("oneOrbAreNShards", Config.oneOrbAreNShards);
        defaultSave.addInt("enchantmentCosts", Config.enchantmentCosts);
        defaultSave.addInt("luckyBoxMin", Config.luckyBoxMin);
        defaultSave.addInt("luckyBoxMax", Config.luckyBoxMax);
        saveFile.addSaveData(defaultSave);
        saveFile.saveScript(Config.file);
    }

    public static Config getInstance() {
        return OBJ;
    }

    public void setMaxAmount(int n) {
        Config.maxAmount = n;
    }

    public int getMaxAmount() {
        return Config.maxAmount;
    }

    public void setMinAmount(int n) {
        Config.minAmount = n;
    }

    public int getMinAmount() {
        return Config.minAmount;
    }

    public void setMaxBossAmount(int n) {
        Config.maxBossAmount = n;
    }

    public int getMaxBossAmount() {
        return Config.maxBossAmount;
    }

    public void setMinBossAmount(int n) {
        Config.minBossAmount = n;
    }

    public int getMinBossAmount() {
        return Config.minBossAmount;
    }

    public void setOneOrbAreNShards(int n) {
        Config.oneOrbAreNShards = n;
    }

    public int getOneOrbAreNShards() {
        return Config.oneOrbAreNShards;
    }

    public void setEnchantmentCosts(int n) {
        Config.enchantmentCosts = n;
    }

    public int getEnchantmentCosts() {
        return Config.enchantmentCosts;
    }

    public int getLuckyBoxMin() {
        return Config.luckyBoxMin;
    }

    public int getLuckyBoxMax() {
        return Config.luckyBoxMax;
    }

}
