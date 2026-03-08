// This file is intentionally empty - the class was moved to ExtendedGuiChest.java
package de.guntram.mcmod.easierchests;

/*
{
    private final int inventoryRows;
    private static final Identifier ICONS = Identifier.of(EasierChests.MODID, "textures/icons.png");
    private final Identifier background;
    private final Inventory containerInventory;
    private final boolean separateBlits;
    private TextFieldWidget searchWidget;
    private static String searchText;

    public ExtendedGuiChestOld(GenericContainerScreenHandler container, PlayerInventory lowerInv, Text title,
            int rows)
    {
        super(container, lowerInv, title);
        containerInventory = container.getInventory();
        this.inventoryRows = rows;
        backgroundHeight = 114 + rows * 18;
        background = Identifier.ofVanilla("textures/gui/container/generic_54.png");
        separateBlits = true;
    }

    public ExtendedGuiChestOld(ShulkerBoxScreenHandler container, PlayerInventory lowerInv, Text title) {
        super(container, lowerInv, title);
        containerInventory = ((InventoryExporter)container).getInventory();
        inventoryRows = 3;
        background = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
        separateBlits = false;
    }

    @Override
    public void init() {
        super.init();
        searchWidget = new TextFieldWidget(textRenderer, x + 80, y + 3, 80, 12, Text.literal("Search"));
        if (searchText != null) searchWidget.setText(searchText);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        super.render(context, mouseX, mouseY, partialTicks);
        if (ConfigurationHandler.enableSearch()) {
            searchWidget.render(context, mouseX, mouseY, 0);
        }
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
        context.drawText(this.textRenderer, this.title.getString(), 8, 6, 4210752, false);
        context.drawText(this.textRenderer, this.title.getString(), 8, this.backgroundHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (separateBlits) {
            context.drawTexture(background, x, y, 0, 0, this.backgroundWidth, this.inventoryRows * 18 + 17);
            context.drawTexture(background, x, y + this.inventoryRows * 18 + 17, 0, 126, this.backgroundWidth, 96);
        } else {
            context.drawTexture(background, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        }

        RenderSystem.enableBlend();

        for (int i = 0; i < 9; i++) {
            drawTexturedModalRectWithMouseHighlight(context, x + 7 + i * 18, y - 18,                         1 * 18, 2 * 18, 18, 18, mouseX, mouseY);
            drawTexturedModalRectWithMouseHighlight(context, x + 7 + i * 18, y + 40 + (this.inventoryRows + 4) * 18, 9 * 18, 2 * 18, 18, 18, mouseX, mouseY);
        }
        int rowsToDrawDownArrow = inventoryRows;
        if (inventoryRows > 6 && !ConfigurationHandler.allowExtraLargeChests())
            rowsToDrawDownArrow = 6;
        for (int i = 0; i < rowsToDrawDownArrow; i++) {
            drawTexturedModalRectWithMouseHighlight(context, x - 18, y + 17 + i * 18,                          1 * 18, 2 * 18, 18, 18, mouseX, mouseY);
        }
        for (int i = 0; i < 4; i++) {
            drawTexturedModalRectWithMouseHighlight(context, x - 18, y + 28 + (i + this.inventoryRows) * 18, 9 * 18, 2 * 18, 18, 18, mouseX, mouseY);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 36; i++) {
            if (!hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i)) {
                Slot slot = this.handler.slots.get(slotIndexFromPlayerInventoryIndex(i));
                context.drawTexture(ICONS, x + slot.x, y + slot.y, 7 * 18 + 1, 3 * 18 + 1, 16, 16);
            }
        }

        if (ConfigurationHandler.enableSearch()) {
            String search = searchWidget.getText().toLowerCase();
            if (!search.isEmpty()) {
                int highlight = (int) Long.parseLong(ConfigurationHandler.getHighlightColor().toUpperCase(), 16);
                for (int i = 0; i < this.handler.slots.size(); i++) {
                    Slot slot = this.handler.slots.get(i);
                    Item item = slot.getStack().getItem();
                    if (item == Items.AIR) continue;
                    if (item.getName().getString().toLowerCase().contains(search)) {
                        context.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, highlight);
                    }
                }
            }
        }
    }

    public static void drawChestInventoryBroom(DrawContext context, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(context, x, y, 11 * 18, 0, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.sortchest"));
    }

    public static void drawChestInventoryAllDown(DrawContext context, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(context, x, y, 0, 2 * 18, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.matchdown"));
    }

    public static void drawPlayerInventoryBroom(DrawContext context, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(context, x, y, 11 * 18, 0, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.sortplayer"));
    }

    public static void drawPlayerInventoryAllUp(DrawContext context, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(context, x, y, 8 * 18, 2 * 18, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.matchup"));
    }

    private static void drawTexturedModalRectWithMouseHighlight(DrawContext context, int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
        drawTexturedModalRectWithMouseHighlight(context, screenx, screeny, textx, texty, sizex, sizey, mousex, mousey, false);
    }

    private static void drawTexturedModalRectWithMouseHighlight(DrawContext context, int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey, boolean ignored) {
        if (mousex >= screenx && mousex < screenx + sizex && mousey >= screeny && mousey < screeny + sizey) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            context.drawTexture(ICONS, screenx, screeny, textx, texty, sizex, sizey);
        } else {
            if (ConfigurationHandler.toneDownButtons()) {
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.3f);
            }
            if (ConfigurationHandler.halfSizeButtons()) {
                context.getMatrices().push();
                context.getMatrices().scale(0.5f, 0.5f, 0.5f);
                context.drawTexture(ICONS, screenx * 2 + sizex / 2, screeny * 2 + sizey / 2, textx, texty, sizex, sizey);
                context.getMatrices().pop();
            } else {
                context.drawTexture(ICONS, screenx, screeny, textx, texty, sizex, sizey);
            }
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void myTooltip(DrawContext context, int screenx, int screeny, int sizex, int sizey, int mousex, int mousey, Text tooltip) {
        if (tooltip != null && mousex >= screenx && mousex <= screenx + sizex && mousey >= screeny && mousey <= screeny + sizey) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mousex, mousey);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, final int mouseButton) {
        if (ConfigurationHandler.enableSearch() && searchWidget.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            checkForMyButtons(mouseX, mouseY);
        }
        if (mouseButton == 2) {
            checkForToggleFrozen(mouseX, mouseY);
        }
        return true;
    }

    void checkForMyButtons(double mouseX, double mouseY) {
        if (mouseX >= x - 18 && mouseX <= x) {
            int deltay = (int)mouseY - y;
            if (deltay < this.inventoryRows * 18 + 17)
                clickSlotsInRow((deltay - 17) / 18);
            else if (deltay < (this.inventoryRows + 4) * 18 + 28) {
                clickSlotsInRow((deltay - 28) / 18);
            }
        } else if (mouseX > x + 7 && mouseX < x + 7 + 9 * 18) {
            boolean isChest;
            if (mouseY > y - 18 && mouseY < y)
                isChest = true;
            else if (mouseY > y + 40 + (this.inventoryRows + 4) * 18 && mouseY < y + 40 + (this.inventoryRows + 4) * 18 + 18)
                isChest = false;
            else
                return;
            int column = ((int)mouseX - x - 7) / 18;
            clickSlotsInColumn(column, isChest);
        }
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        if (keycode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keycode, scancode, modifiers);
        }
        if (ConfigurationHandler.enableSearch() && searchWidget.isActive()) {
            return searchWidget.keyPressed(keycode, scancode, modifiers);
        }
        return super.keyPressed(keycode, scancode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (ConfigurationHandler.enableSearch() && searchWidget.isActive()) {
            return searchWidget.charTyped(chr, keyCode);
        }
        return super.charTyped(chr, keyCode);
    }

    @Override
    public void close() {
        searchText = searchWidget != null ? searchWidget.getText() : "";
        super.close();
    }

    void checkForToggleFrozen(double mouseX, double mouseY) {
        for (int i = 0; i < this.handler.slots.size(); ++i) {
            int invIndex = this.playerInventoryIndexFromSlotIndex(i);
            if (invIndex == -1) continue;
            Slot slot = this.handler.slots.get(i);
            if (isPointWithinBounds(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                FrozenSlotDatabase.setSlotFrozen(invIndex, !FrozenSlotDatabase.isSlotFrozen(invIndex));
            }
        }
    }

    private void clickSlotsInRow(int row) {
        for (int slot = row * 9; slot <= row * 9 + 8; slot++)
            if (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
    }

    private void clickSlotsInColumn(int column, boolean isChest) {
        int first = (isChest ? column : inventoryRows * 9 + column);
        int count = (isChest ? inventoryRows : 4);
        for (int i = 0; i < count; i++) {
            int slot = first + i * 9;
            if (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
        }
    }

    private void sortInventory(boolean isChest) {
        Inventory inv = (isChest ? containerInventory : client.player.getInventory());
        sortInventory((SlotClicker) this, isChest, inv);
    }

    public static void sortInventory(SlotClicker screen, boolean isChest, Inventory inv) {
        int size = isChest ? inv.size() : 36;
        if (size > 9 * 6 && !ConfigurationHandler.allowExtraLargeChests())
            size = 9 * 6;
        for (int toSlot = 0; toSlot < size; toSlot++) {
            ItemStack toStack = inv.getStack(toSlot);
            String targetItemName = toStack.getTranslationKey();
            if (toStack.getItem() == Items.AIR) {
                if (!isChest && toSlot < 9)
                    continue;
                targetItemName = "\u00a7\u00a7\u00a7";
            }

            if (isChest || toSlot >= 9 && (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(toSlot))) {
                for (int fromSlot = toSlot + 1; fromSlot < size; fromSlot++) {
                    if (!isChest && !hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(fromSlot))
                        continue;
                    ItemStack slotStack = inv.getStack(fromSlot);
                    if (slotStack.getItem() == Items.AIR) continue;
                    String slotItem = inv.getStack(fromSlot).getTranslationKey();
                    if (slotItem.compareToIgnoreCase(targetItemName) < 0) {
                        targetItemName = slotItem;
                    }
                }
            } else {
                if (toStack.getCount() >= toStack.getMaxCount()) continue;
            }

            for (int fromSlot = toSlot + 1; fromSlot < size; fromSlot++) {
                if (!isChest && !hasShiftDown()) {
                    if (FrozenSlotDatabase.isSlotFrozen(fromSlot)) continue;
                }
                toStack = inv.getStack(toSlot);
                ItemStack fromStack = inv.getStack(fromSlot);
                if (fromStack.getTranslationKey().equals(targetItemName)
                && (!toStack.getTranslationKey().equals(targetItemName)
                    || stackShouldGoBefore(fromStack, toStack))) {
                    screen.EasierChests$onMouseClick(null, isChest ? fromSlot : screen.EasierChests$slotIndexfromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);
                    screen.EasierChests$onMouseClick(null, isChest ? toSlot   : screen.EasierChests$slotIndexfromPlayerInventoryIndex(toSlot),   0, SlotActionType.PICKUP);
                    screen.EasierChests$onMouseClick(null, isChest ? fromSlot : screen.EasierChests$slotIndexfromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);
                }
            }
        }
    }

    private static ItemEnchantmentsComponent getEnchantmentsComponent(ItemStack stack) {
        if (stack.isOf(Items.ENCHANTED_BOOK)) {
            ItemEnchantmentsComponent stored = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
            return stored != null ? stored : ItemEnchantmentsComponent.DEFAULT;
        }
        return stack.getEnchantments();
    }

    private static boolean stackShouldGoBefore(ItemStack replacement, ItemStack original) {
        String replacementName = replacement.getName().getString();
        String originalName    = original.getName().getString();

        if (replacementName.compareToIgnoreCase(originalName) > 0) return false;

        if (replacement.isDamageable() && original.isDamageable()
        &&  replacement.getDamage() > original.getDamage()) {
            return false;
        }

        ItemEnchantmentsComponent origEnchs = getEnchantmentsComponent(original);
        ItemEnchantmentsComponent replEnchs = getEnchantmentsComponent(replacement);

        int replSize = replEnchs.getEnchantments().size();
        int origSize = origEnchs.getEnchantments().size();

        if (replSize == 0) {
            if (origSize == 0) {
                return original.getCount() != original.getMaxCount();
            }
            return true;
        }
        if (origSize == 0) return false;
        if (replSize < origSize) return true;
        if (replSize > origSize) return false;
        return false;
    }

    public static void moveMatchingItems(HandledScreen screen, boolean isChestToPlayer) {
        Inventory from, to;
        int fromSize, toSize;
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Inventory containerInventory = screen.getScreenHandler().getSlot(0).inventory;

        if (isChestToPlayer) {
            from = containerInventory;                    fromSize = from.size();
            to   = minecraft.player.getInventory();       toSize   = 36;
        } else {
            from = minecraft.player.getInventory();       fromSize = 36;
            to   = containerInventory;                    toSize   = to.size();
        }
        if (!ConfigurationHandler.allowExtraLargeChests()) {
            if (fromSize > 9 * 6) fromSize = 9 * 6;
            if (toSize   > 9 * 6) toSize   = 9 * 6;
        }
        for (int i = 0; i < fromSize; i++) {
            if (!isChestToPlayer && !hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i))
                continue;
            ItemStack fromStack = from.getStack(i);
            int slot = isChestToPlayer ? i : ((SlotClicker)screen).EasierChests$slotIndexfromPlayerInventoryIndex(i);
            for (int j = 0; j < toSize; j++) {
                ItemStack toStack = to.getStack(j);
                if (ItemStack.areItemsAndComponentsEqual(fromStack, toStack)) {
                    ((SlotClicker)screen).EasierChests$onMouseClick(null, slot, 0, SlotActionType.QUICK_MOVE);
                }
            }
        }
    }

    private void slotClick(int slot, int mouseButton, SlotActionType clickType) {
        ((SlotClicker)this).EasierChests$onMouseClick(null, slot, mouseButton, clickType);
    }

    private int playerInventoryIndexFromSlotIndex(int slot) {
        return ((SlotClicker)this).EasierChests$playerInventoryIndexFromSlotIndex(slot);
    }

    private int slotIndexFromPlayerInventoryIndex(int idx) {
        return ((SlotClicker)this).EasierChests$slotIndexfromPlayerInventoryIndex(idx);
    }
}
*/
