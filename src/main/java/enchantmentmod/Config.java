package enchantmentmod;

import necesse.engine.GlobalData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

import java.io.File;

public class Config {
    private Integer minAmount = 8;
    private Integer maxAmount = 40;
    private Integer minBossAmount = 16;
    private Integer maxBossAmount = 80;
    private Integer oneOrbAreNShards = 100;
    private Integer enchantmentCosts = 10;
    private Integer luckyBoxMin = 50;
    private Integer luckyBoxMax = 100;
    private static final Config OBJ = new Config();
    private Config () {
        File file = new File(GlobalData.cfgPath() + "betterenchantment.cfg");
        if (!file.exists()) {
            SaveData saveFile = new SaveData("CONFIG");
            SaveData defaultSave = new SaveData("default");
            defaultSave.addInt("minAmount", this.minAmount);
            defaultSave.addInt("maxAmount", this.maxAmount);
            defaultSave.addInt("minBossAmount", this.minBossAmount);
            defaultSave.addInt("maxBossAmount", this.maxBossAmount);
            defaultSave.addInt("oneOrbAreNShards", this.oneOrbAreNShards);
            defaultSave.addInt("enchantmentCosts", this.enchantmentCosts);
            defaultSave.addInt("luckyBoxMin", this.luckyBoxMin);
            defaultSave.addInt("luckyBoxMax", this.luckyBoxMax);
            saveFile.addSaveData(defaultSave);
            saveFile.saveScript(file);
            return;
        }

        LoadData save = new LoadData(file);
        for (LoadData saveData : save.getLoadData()) {
            if (!"default".equals(saveData.getName())) {
                continue;
            }
            this.minAmount = saveData.getInt("minAmount", this.minAmount);
            this.maxAmount = saveData.getInt("maxAmount", this.maxAmount);
            this.minBossAmount = saveData.getInt("minBossAmount", this.minBossAmount);
            this.maxBossAmount = saveData.getInt("maxBossAmount", this.maxBossAmount);
            this.oneOrbAreNShards = saveData.getInt("oneOrbAreNShards", this.oneOrbAreNShards);
            this.enchantmentCosts = saveData.getInt("enchantmentCosts", this.enchantmentCosts);
            this.luckyBoxMin = saveData.getInt("luckyBoxMin", this.luckyBoxMin);
            this.luckyBoxMax = saveData.getInt("luckyBoxMax", this.luckyBoxMax);
            break;
        }
    }

    public static Config getInstance() {
        return OBJ;
    }

    public void setMaxAmount(int n) {
        this.maxAmount = n;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public void setMinAmount(int n) {
        this.minAmount = n;
    }

    public int getMinAmount() {
        return this.minAmount;
    }

    public void setMaxBossAmount(int n) {
        this.maxBossAmount = n;
    }

    public int getMaxBossAmount() {
        return this.maxBossAmount;
    }

    public void setMinBossAmount(int n) {
        this.minBossAmount = n;
    }

    public int getMinBossAmount() {
        return this.minBossAmount;
    }

    public void setOneOrbAreNShards(int n) {
        this.oneOrbAreNShards = n;
    }

    public int getOneOrbAreNShards() {
        return this.oneOrbAreNShards;
    }

    public void setEnchantmentCosts(int n) {
        this.enchantmentCosts = n;
    }

    public int getEnchantmentCosts() {
        return this.enchantmentCosts;
    }

    public int getLuckyBoxMin() {
        return this.luckyBoxMin;
    }

    public int getLuckyBoxMax() {
        return this.luckyBoxMax;
    }

}
