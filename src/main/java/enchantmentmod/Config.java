package enchantmentmod;

public class Config {
    private int minAmount = 8;
    private int maxAmount = 40;
    private int minBossAmount = 16;
    private int maxBossAmount = 80;
    private int oneOrbAreNShards = 100;
    private int enchantmentCosts = 10;
    private static final Config OBJ = new Config();
    private Config () {

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

}
