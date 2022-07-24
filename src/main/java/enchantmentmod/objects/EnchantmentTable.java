package enchantmentmod.objects;

import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DisplayStandObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class EnchantmentTable extends FurnitureObject {
    private GameTexture texture;
    protected int itemHeight;
    protected String textureName;
    static int containerIndex;

    public EnchantmentTable() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(150, 119, 70);
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.drawDmg = false;
        this.isLightTransparent = true;
    }
    public EnchantmentTable(int index) {
        super(new Rectangle(32, 32));
        EnchantmentTable.containerIndex = index;
        this.mapColor = new Color(150, 119, 70);
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.drawDmg = false;
        this.isLightTransparent = true;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        texture = GameTexture.fromFile("objects/enchantmenttable");
    }
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        final TextureDrawOptions base = this.texture.initDraw().sprite(
            rotation % 4,
            0,
            32,
            this.texture.getHeight()
        )
            .light(light)
            .pos(drawX, drawY - (this.texture.getHeight() - 32));
        ObjectEntity ent = level.entityManager.getObjectEntity(tileX, tileY);
        final DrawOptions item;
        if (ent != null && ent.implementsOEInventory()) {
            InventoryItem invItem = ((OEInventory)ent).getInventory().getItem(0);
            item = invItem != null ? invItem.getWorldDrawOptions(
                perspective,
                drawX + 16,
                drawY + 10 - this.itemHeight,
                light,
                0.0F,
                32
            ) : () -> {};
        } else {
            item = () -> {};
        }

        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                base.draw();
                item.draw();
            }
        });
    }
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(
            rotation % 4,
            0,
            32,
            this.texture.getHeight()
        )
            .alpha(alpha)
            .draw(
                drawX,
                drawY - (this.texture.getHeight() - 32
            )
        );
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel()) {
            OEInventoryContainer.openAndSendContainer(containerIndex, player.getServerClient(), level, x, y);
        }
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new DisplayStandObjectEntity(level, x, y);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "displaytip"));
        return tooltips;
    }
}
