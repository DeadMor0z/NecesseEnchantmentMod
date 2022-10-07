package enchantmentmod.objects.network;

import necesse.engine.Settings;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketOEInventoryNameUpdate;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.*;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.object.OEInventoryContainer;

import java.awt.*;


public class EnchantmentTableContainerForm<T extends EnchantmentTableContainer> extends ContainerFormSwitcher<T> {
    public Form inventoryForm;
    public SettlementObjectStatusFormManager settlementObjectFormManager;
    public FormLabelEdit label;
    public FormContentIconButton edit;
    public FormContainerSlot[] slots;
    public LocalMessage renameTip;
    public FormLocalTextButton enchantButton;

    public FormLocalLabel costLabel;
    public FormItemPreview preview;

    public FormLabel costText;

    public static TypeParser<?>[] getParsers(FontOptions fontOptions) {
        return new TypeParser[]{
            TypeParsers.GAME_COLOR,
            TypeParsers.REMOVE_URL,
            TypeParsers.URL_OPEN,
            TypeParsers.ItemIcon(fontOptions.getSize()),
            TypeParsers.InputIcon(fontOptions)
        };
    }

    protected EnchantmentTableContainerForm(Client client, T container, int height) {
        super(client, container);

        this.inventoryForm = this.addComponent(new Form(408, height), (form, active) -> {
            if (active) {
                return;
            }
            this.label.setTyping(false);
            this.runEditUpdate();
        });

        OEInventory oeInventory = container.oeInventory;
        FontOptions labelOptions = new FontOptions(20);
        this.label = this.inventoryForm.addComponent(
            new FormLabelEdit(
                "",
                labelOptions,
                Settings.UI.activeTextColor,
                4,
                4,
                4,
                50
            ),
            -1000
        );
        this.label.onMouseChangedTyping((e) -> this.runEditUpdate());
        this.label.onSubmit((e) -> this.runEditUpdate());
        this.label.allowCaretSetTyping = oeInventory.canSetInventoryName();
        this.label.allowItemAppend = true;
        this.label.setParsers(getParsers(labelOptions));
        this.label.setText(oeInventory.getInventoryName().translate());

        FormFlow iconFlow = new FormFlow(this.inventoryForm.getWidth() - 4);
        this.renameTip = new LocalMessage("ui", "renamebutton");
        if (oeInventory.canSetInventoryName()) {
            this.edit = (FormContentIconButton)this.inventoryForm.addComponent(
                new FormContentIconButton(
                    iconFlow.next(-26) - 24,
                    4,
                    FormInputSize.SIZE_24,
                    ButtonColor.BASE,
                    Settings.UI.container_rename,
                    new GameMessage[]{
                        this.renameTip
                    }
                )
            );
            this.edit.onClicked((e) -> {
                this.label.setTyping(!this.label.isTyping());
                this.runEditUpdate();
            });
        }

        this.costLabel = this.inventoryForm.addComponent(
            new FormLocalLabel("ui", "magecost", new FontOptions(16), -1, 56, 45)
        );

        this.preview = this.inventoryForm.addComponent(
            new FormItemPreview(
                this.costLabel.getX() + 100,
                35,
                "enchantmentorb"
            )
        );

        this.costText = this.inventoryForm.addComponent(
            new FormLabel(
                "x " + container.getEnchantCost(),
                new FontOptions(16),
                -1,
                this.preview.getX() + 35,
                45
            )
        );

        this.enchantButton = this.inventoryForm.addComponent(
            new FormLocalTextButton(
                "ui",
                "mageconfirm",
                240,
                40,
                150,
                FormInputSize.SIZE_20,
                ButtonColor.BASE
            )
        );
        this.enchantButton.onClicked((e) -> container.enchantButton.runAndSend());
        enchantButton.setCooldown(500);

        FormContentIconButton lootAllButton = this.inventoryForm.addComponent(
            new FormContentIconButton(
                iconFlow.next(-26) - 24,
                4,
                FormInputSize.SIZE_24,
                ButtonColor.BASE,
                Settings.UI.container_loot_all,
                new GameMessage[]{
                        new LocalMessage("ui", "inventorylootall")
                }
            )
        );
        lootAllButton.onClicked((e) -> container.lootButton.runAndSend());
        lootAllButton.setCooldown(500);
        if (oeInventory.canSortInventory()) {
            FormContentIconButton sortButton = (FormContentIconButton)this.inventoryForm.addComponent(
                new FormContentIconButton(
                    iconFlow.next(-26) - 24,
                    4,
                    FormInputSize.SIZE_24,
                    ButtonColor.BASE,
                    Settings.UI.inventory_sort,
                    new GameMessage[]{
                        new LocalMessage("ui", "inventorysort")
                    }
                )
            );
            sortButton.onClicked((e) -> container.sortButton.runAndSend());
            sortButton.setCooldown(500);
        }

        this.settlementObjectFormManager = container.settlementObjectManager.getFormManager(
            this,
            this.inventoryForm
        );

        this.settlementObjectFormManager.addConfigButtonRow(this.inventoryForm, iconFlow, 4, -1);
        this.label.setWidth(iconFlow.next() - 8);
        this.addSlots();
        this.makeCurrent(this.inventoryForm);
        this.updateEnchantActive();
    }

    public EnchantmentTableContainerForm(Client client, T container) {
        this(client, container, getContainerHeight(container.getOEInventory().getInventory().getSize(), 10));
    }

    protected static int getContainerHeight(int inventorySize, int columns) {
        return (inventorySize + columns - 1) / columns * 40 + 30 + 8;
    }

    public void setDefaultPos() {
        ContainerComponent.setPosFocus(this.inventoryForm);
        this.settlementObjectFormManager.setDefaultPositions();
    }

    public boolean shouldOpenInventory() {
        return true;
    }

    protected void addSlots() {
        this.slots = new FormContainerSlot[(this.container).INVENTORY_END - (this.container).INVENTORY_START + 1];

        for(int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + this.container.INVENTORY_START;
            int x = i % 10;
            int y = i / 10;
            this.slots[i] = this.inventoryForm.addComponent(
                new FormContainerSlot(this.client, slotIndex, 4 + x * 40, 4 + y * 40 + 30)
            );
        }
    }

    private void runEditUpdate() {
        OEInventory oeInventory = this.container.oeInventory;
        if (oeInventory.canSetInventoryName()) {
            if (this.label.isTyping()) {
                this.edit.setIcon(Settings.UI.container_rename_save);
                this.renameTip = new LocalMessage("ui", "savebutton");
            } else {
                if (!this.label.getText().equals(oeInventory.getInventoryName())) {
                    oeInventory.setInventoryName(this.label.getText());
                    this.client.network.sendPacket(new PacketOEInventoryNameUpdate(oeInventory, this.label.getText()));
                }

                this.edit.setIcon(Settings.UI.container_rename);
                this.renameTip = new LocalMessage("ui", "renamebutton");
                this.label.setText(oeInventory.getInventoryName().translate());
            }

            this.edit.setTooltips(this.renameTip);
        }
    }

    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener() {
            public void onChange(Language language) {
                EnchantmentTableContainerForm.this.preview.setX(
                    EnchantmentTableContainerForm.this.costLabel.getX() +
                    EnchantmentTableContainerForm.this.costLabel.getBoundingBox().width
                );
                EnchantmentTableContainerForm.this.costText.setX(
                    EnchantmentTableContainerForm.this.preview.getX() + 30
                );
            }

            public boolean isDisposed() {
                return EnchantmentTableContainerForm.this.isDisposed();
            }
        });
    }

    private void updateEnchantActive() {
        this.costText.setText("x " + this.container.getEnchantCost());
        this.enchantButton.setActive(this.container.canEnchant());
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.settlementObjectFormManager.updateButtons();
        super.draw(tickManager, perspective, renderBox);
        this.updateEnchantActive();
    }
}

