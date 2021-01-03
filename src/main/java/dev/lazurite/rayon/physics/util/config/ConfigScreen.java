package dev.lazurite.rayon.physics.util.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config;
    private ButtonListWidget list;
    private static final Option[] OPTIONS = new Option[] {
            ConfigOptions.GRAVITY,
            ConfigOptions.BLOCK_DISTANCE,
            ConfigOptions.ENTITY_DISTANCE,
            ConfigOptions.STEP_RATE
    };

    public ConfigScreen(Screen parent) {
        super(new TranslatableText("config.rayon.title"));
        this.parent = parent;
        this.config = Config.INSTANCE;
    }

    protected void init() {
        this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        this.list.addAll(OPTIONS);
        this.children.add(this.list);

        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (button) -> {
            this.config.save();
            this.client.openScreen(this.parent);
        }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        List<OrderedText> list = getHoveredButtonTooltip(this.list, mouseX, mouseY);
        if (list != null) {
            this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
        }
    }

    @Nullable
    public static List<OrderedText> getHoveredButtonTooltip(ButtonListWidget buttonList, int mouseX, int mouseY) {
        Optional<AbstractButtonWidget> optional = buttonList.getHoveredButton((double)mouseX, (double)mouseY);
        if (optional.isPresent() && optional.get() instanceof OrderableTooltip) {
            Optional<List<OrderedText>> optional2 = ((OrderableTooltip)optional.get()).getOrderedTooltip();
            return (List)optional2.orElse(null);
        } else {
            return null;
        }
    }

    public void removed() {
        this.config.save();
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }
}
