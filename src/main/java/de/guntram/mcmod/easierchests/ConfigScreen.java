package de.guntram.mcmod.easierchests;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {

    private final Screen parent;

    private boolean extraLargeChests;
    private boolean halfSizeButtons;
    private boolean toneDownButtons;
    private boolean enableSearch;
    private String highlightColor;

    private TextFieldWidget colorField;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("easierchests.config.title"));
        this.parent = parent;
        this.extraLargeChests = ConfigurationHandler.allowExtraLargeChests();
        this.halfSizeButtons  = ConfigurationHandler.halfSizeButtons();
        this.toneDownButtons  = ConfigurationHandler.toneDownButtons();
        this.enableSearch     = ConfigurationHandler.enableSearch();
        this.highlightColor   = ConfigurationHandler.getHighlightColor();
    }

    @Override
    protected void init() {
        // Preserve color text across screen resizes
        if (colorField != null) {
            highlightColor = colorField.getText();
        }

        int cx = this.width / 2;
        int startY = 40;
        int rowH = 25;

        addDrawableChild(ButtonWidget.builder(
                toggleText("easierchests.config.largechests", extraLargeChests),
                btn -> {
                    extraLargeChests = !extraLargeChests;
                    btn.setMessage(toggleText("easierchests.config.largechests", extraLargeChests));
                })
            .dimensions(cx - 100, startY, 200, 20)
            .build());

        addDrawableChild(ButtonWidget.builder(
                toggleText("easierchests.config.halfsize", halfSizeButtons),
                btn -> {
                    halfSizeButtons = !halfSizeButtons;
                    btn.setMessage(toggleText("easierchests.config.halfsize", halfSizeButtons));
                })
            .dimensions(cx - 100, startY + rowH, 200, 20)
            .build());

        addDrawableChild(ButtonWidget.builder(
                toggleText("easierchests.config.transparent", toneDownButtons),
                btn -> {
                    toneDownButtons = !toneDownButtons;
                    btn.setMessage(toggleText("easierchests.config.transparent", toneDownButtons));
                })
            .dimensions(cx - 100, startY + rowH * 2, 200, 20)
            .build());

        addDrawableChild(ButtonWidget.builder(
                toggleText("easierchests.config.enablesearch", enableSearch),
                btn -> {
                    enableSearch = !enableSearch;
                    btn.setMessage(toggleText("easierchests.config.enablesearch", enableSearch));
                })
            .dimensions(cx - 100, startY + rowH * 3, 200, 20)
            .build());

        // Color input (ARGB hex, e.g. "4000ff00")
        colorField = new TextFieldWidget(
                textRenderer, cx - 100, startY + rowH * 4 + 14, 200, 20,
                Text.translatable("easierchests.config.highlight"));
        colorField.setText(highlightColor);
        colorField.setMaxLength(8);
        addDrawableChild(colorField);

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), btn -> close())
            .dimensions(cx - 100, startY + rowH * 5 + 22, 200, 20)
            .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        // Title
        context.drawText(textRenderer, title,
                (this.width - textRenderer.getWidth(title)) / 2, 15, 0xFFFFFF, true);
        // Label above color field
        context.drawText(textRenderer, Text.translatable("easierchests.config.highlight"),
                this.width / 2 - 100, 40 + 25 * 4 + 2, 0xA0A0A0, false);
    }

    private Text toggleText(String key, boolean value) {
        return Text.translatable(key)
                .append(Text.literal(": "))
                .append(Text.translatable(value ? "options.on" : "options.off"));
    }

    @Override
    public void close() {
        ConfigurationHandler.setAllOptions(
                extraLargeChests, halfSizeButtons, toneDownButtons, enableSearch,
                colorField.getText().trim());
        this.client.setScreen(parent);
    }
}
