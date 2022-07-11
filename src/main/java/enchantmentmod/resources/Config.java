package enchantmentmod.resources;

import necesse.engine.GlobalData;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class Config {

    final String defaultConfig =
            "minLootAmount=2\n" +
            "maxLootAmount=10\n" +
            "minBossLootAmount=4\n" +
            "maxBossAmount=20\n" +
            "oneOrbAreNShards=100\n" +
            "enchantmentCosts=10\n";

    private int minAmount = 2;
    private int maxAmount = 10;
    private int minBossAmount = 4;
    private int maxBossAmount = 20;
    private int oneOrbAreNShards = 100;
    private int enchantmentCosts = 10;
    private static final Config OBJ = new Config();
    private Config () {
        System.out.println("Loading config...");
        String filename = GlobalData.rootPath() + "/settings/enchantmentmod/settings.cfg";
        System.out.println(filename);
        try {
            File file = new File(filename);
            if (!file.exists()) {
                createNewFile(file);
            }

            InputStreamReader isr = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            loadConfig(br);
            br.close();

        } catch (IOException ex) {
            ex.printStackTrace();
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

    private void loadConfig(BufferedReader br) throws IOException {
        String line;
        while((line = br.readLine()) != null) {
            if (line.length() == 0) {
                continue;
            }
            String[] temp = line.split("=");
            int value = Integer.parseInt(temp[1]);
            switch (temp[0]) {
                case "minLootAmount":
                    this.setMinAmount(value);
                    break;
                case "maxLootAmount":
                    this.setMaxAmount(value);
                    break;
                case "minBossLootAmount":
                    this.setMinBossAmount(value);
                    break;
                case "maxBossAmount":
                    this.setMaxBossAmount(value);
                    break;
                case "oneOrbAreNShards":
                    this.setOneOrbAreNShards(value);
                    break;
                case "enchantmentCosts":
                    this.setEnchantmentCosts(value);
                    break;
            }
        }
    }

    private void createNewFile(File file) throws IOException {
        if (!file.getParentFile().mkdirs()) {
            throw new IOException("Error creating directory: " + file.getParentFile().toPath());
        }
        if (!file.createNewFile()) {
            throw new IOException("Error creating file: " + file.toPath());
        }
        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))
        ) {
            writer.write(defaultConfig);
        }
    }

}
