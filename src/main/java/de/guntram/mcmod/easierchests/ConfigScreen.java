package de.guntram.mcmod.easierchests;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

        int cx    = this.width / 2;
        int startY = 48;
        int rowH   = 26;
        // Labels start at cx-130; controls start at cx+60 (190px gap for labels)
        int ctrlX  = cx + 60;
        int ctrlW  = 70;

        // Row 0 – Allow Extra Large Chests
        addDrawableChild(ButtonWidget.builder(
                onOffText(extraLargeChests),
                btn -> {
                    extraLargeChests = !extraLargeChests;
                    btn.setMessage(onOffText(extraLargeChests));
                })
            .dimensions(ctrlX, startY, ctrlW, 20)
            .build());

        // Row 1 – Half Button Size
        addDrawableChild(ButtonWidget.builder(
                onOffText(halfSizeButtons),
                btn -> {
                    halfSizeButtons = !halfSizeButtons;
                    btn.setMessage(onOffText(halfSizeButtons));
                })
            .dimensions(ctrlX, startY + rowH, ctrlW, 20)
            .build());

        // Row 2 – Highlight Color (text field)
        colorField = new TextFieldWidget(
                textRenderer, ctrlX, startY + rowH * 2, ctrlW, 20,
                Text.translatable("easierchests.config.highlight"));
        colorField.setText(highlightColor);
        colorField.setMaxLength(8);
        addDrawableChild(colorField);

        // Row 3 – Transparent Buttons
        addDrawableChild(ButtonWidget.builder(
                onOffText(toneDownButtons),
                btn -> {
                    toneDownButtons = !toneDownButtons;
                    btn.setMessage(onOffText(toneDownButtons));
                })
            .dimensions(ctrlX, startY + rowH * 3, ctrlW, 20)
            .build());

        // Row 4 – Enable Search
        addDrawableChild(ButtonWidget.builder(
                onOffText(enableSearch),
                btn -> {
                    enableSearch = !enableSearch;
                    btn.setMessage(onOffText(enableSearch));
                })
            .dimensions(ctrlX, startY + rowH * 4, ctrlW, 20)
            .build());

        // Done
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), btn -> close())
            .dimensions(cx - 50, startY + rowH * 5 + 10, 100, 20)
            .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int cx     = this.width / 2;
        int startY = 48;
        int rowH   = 26;
        int labelX = cx - 130;
        int labelColor = 0xFFFFFFFF;

        // Title
        context.drawText(textRenderer, title,
                (this.width - textRenderer.getWidth(title)) / 2, 15, labelColor, true);

        // Row labels (vertically centred: +6 offset inside a 20px row)
        context.drawText(textRenderer, Text.translatable("easierchests.config.largechests"),  labelX, startY + 6,           labelColor, true);
        context.drawText(textRenderer, Text.translatable("easierchests.config.halfsize"),     labelX, startY + rowH + 6,    labelColor, true);
        context.drawText(textRenderer, Text.translatable("easierchests.config.highlight"),    labelX, startY + rowH * 2 + 6, labelColor, true);
        context.drawText(textRenderer, Text.translatable("easierchests.config.transparent"),  labelX, startY + rowH * 3 + 6, labelColor, true);
        context.drawText(textRenderer, Text.translatable("easierchests.config.enablesearch"), labelX, startY + rowH * 4 + 6, labelColor, true);
    }

    private Text onOffText(boolean value) {
        return Text.translatable(value ? "easierchests.config.true" : "easierchests.config.false")
                .formatted(value ? Formatting.GREEN : Formatting.RED);
    }

    private Text toggleText(String key, boolean value) {
        return Text.translatable(key)
                .append(Text.literal(": "))
                .append(Text.translatable(value ? "options.on" : "options.off")
                        .formatted(value ? Formatting.GREEN : Formatting.RED));
    }

    @Override
    public void close() {
        ConfigurationHandler.setAllOptions(
                extraLargeChests, halfSizeButtons, toneDownButtons, enableSearch,
                colorField.getText().trim());
        this.client.setScreen(parent);
    }
}
