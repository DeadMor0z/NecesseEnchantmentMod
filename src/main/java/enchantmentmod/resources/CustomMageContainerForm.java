package enchantmentmod.resources;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerEnchantSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

import java.awt.*;

public class CustomMageContainerForm<T extends CustomMageContainer> extends ShopContainerForm<T> {
    public Form enchantForm;
    public FormLabel costText;
    public FormLocalTextButton enchantButton;
    public FormLocalLabel costLabel;
    public FormItemPreview preview;

    public CustomMageContainerForm(Client client, T container, int width, int height, int maxExpeditionsHeight) {
        super(client, container, width, height, maxExpeditionsHeight);

        this.enchantForm = this.addComponent(
            new Form("enchant", width, height), (form, active) -> {
                //((CustomMageContainer)container).setIsEnchanting.runAndSend(active);
                container.setIsEnchanting.runAndSend(active);
            }
        );

        this.enchantForm.addComponent(
            new FormLocalTextButton(
                "ui",
                "backbutton",
                this.enchantForm.getWidth() - 104,
                4,
                100,
                FormInputSize.SIZE_20,
                ButtonColor.BASE
            )
        ).onClicked((e) -> this.makeCurrent(this.dialogueForm));

        this.enchantForm.addComponent(
            new FormLocalLabel(
                "ui",
                "mageenchant",
                new FontOptions(20),
                -1,
                4,
                4
            )
        );

        this.enchantForm.addComponent(
            (
                new FormFairTypeLabel(
                    new LocalMessage("ui", "mageenchanttip"),
                    this.enchantForm.getWidth() / 2, 100
                )
            )
                .setFontOptions(new FontOptions(16))
                .setTextAlign(FairType.TextAlign.CENTER)
                .setMaxWidth(this.enchantForm.getWidth() - 20)
        );

        this.enchantForm.addComponent(
                new FormContainerEnchantSlot(client, container.ENCHANT_SLOT, 40, 50)
        );

        this.enchantButton = this.enchantForm.addComponent(
            new FormLocalTextButton(
                "ui",
                "mageconfirm",
                90,
                60,
                150,
                FormInputSize.SIZE_20,
                ButtonColor.BASE
            )
        );

        this.enchantButton.onClicked((e) -> {
            container.enchantButton.runAndSend();
        });

        this.costLabel = this.enchantForm.addComponent(
            new FormLocalLabel(
                "ui",
                "magecost",
                new FontOptions(16),
                -1,
                260,
                46
            )
        );

        this.preview = this.enchantForm.addComponent(new FormItemPreview(250, 60, "enchantmentorb"));

        this.costText = this.enchantForm.addComponent(
            new FormLabel(
                "x " + container.getEnchantCost(),
                new FontOptions(16), -1, this.preview.getX() + 30, 70
            )
        );

        this.updateEnchantActive();
    }

    public CustomMageContainerForm(Client client, T container) {
        this(client, container, 408, 170, 240);
    }

    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener() {
            public void onChange(Language language) {
                CustomMageContainerForm.this.preview.setX(
                    CustomMageContainerForm.this.costLabel.getX() +
                    CustomMageContainerForm.this.costLabel.getBoundingBox().width
                );
                CustomMageContainerForm.this.costText.setX(CustomMageContainerForm.this.preview.getX() + 30);
            }

            public boolean isDisposed() {
                return CustomMageContainerForm.this.isDisposed();
            }
        });
    }

    protected void setupExtraDialogueOptions() {
        super.setupExtraDialogueOptions();

        if (!(this.container.humanShop instanceof MageHumanMob) || this.container.items == null) {
            return;
        }

        this.dialogueForm.addDialogueOption(
            new LocalMessage("ui", "magewantenchant"),
            () -> this.makeCurrent(this.enchantForm)
        );
    }

    private void updateEnchantActive() {
        this.costText.setText("x " + (this.container).getEnchantCost());
        this.enchantButton.setActive((this.container).canEnchant());
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isCurrent(this.enchantForm)) {
            this.updateEnchantActive();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    public void setDefaultPos() {
        super.setDefaultPos();
        ContainerComponent.setPosFocus(this.enchantForm);
    }
}