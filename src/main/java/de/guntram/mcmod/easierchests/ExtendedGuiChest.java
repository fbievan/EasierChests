package de.guntram.mcmod.easierchests;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/*
 * Warning - this code should extend ContainerScreen54 AND ShulkerBoxScreen,
 * which it can't. So we extend the superclass, and implement the few methods
 * that are in those classes (and are identical ...) ourselves. Doh.
 */

public class ExtendedGuiChest extends HandledScreen
{
    private final int inventoryRows;
    private static final Identifier ICONS = Identifier.of(EasierChests.MODID, "textures/icons.png");
    private final Identifier background;
    private final Inventory containerInventory;
    private final boolean separateBlits;
    private TextFieldWidget searchWidget;
    private static String searchText;
    private final PlayerInventory ownPlayerInventory;

    public ExtendedGuiChest(GenericContainerScreenHandler container, PlayerInventory lowerInv, Text title,
            int rows)
    {
        super(container, lowerInv, title);
        ownPlayerInventory = lowerInv;
        // ToDo: make container a Container again; can only
        // use getInventory() on GenericContainer though. Need to
        // find out how to access the inventory in the shulker box case.
        containerInventory = container.getInventory();
        this.inventoryRows=rows;
        backgroundHeight = 114 + rows * 18;
        background = Identifier.ofVanilla("textures/gui/container/generic_54.png");
        separateBlits=true;
    }
    
    public ExtendedGuiChest(ShulkerBoxScreenHandler container, PlayerInventory lowerInv, Text title) {
        super(container, lowerInv, title);
        ownPlayerInventory = lowerInv;
        containerInventory = ((InventoryExporter)container).getInventory();
        inventoryRows = 3;
        background = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
        separateBlits=false;
    }
    
    @Override
    public void init() {
        super.init();
        searchWidget = new TextFieldWidget(textRenderer, x+80, y+3, 80, 12, Text.literal("Search"));
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
        context.drawText(this.textRenderer, this.ownPlayerInventory.getDisplayName().getString(), 8, this.backgroundHeight - 96 + 2, 4210752, false);
    }

    /*
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (separateBlits) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, background, x, y, 0.0f, 0.0f, this.backgroundWidth, this.inventoryRows * 18 + 17, 256, 256);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, background, x, y + this.inventoryRows * 18 + 17, 0.0f, 126.0f, this.backgroundWidth, 96, 256, 256);
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, background, x, y, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        }

        for (int i=0; i<9; i++) {
            this.drawTexturedModalRectWithMouseHighlight(context, x+7+i*18,    y-18,                          1*18, 2*18, 18, 18, mouseX, mouseY);
            this.drawTexturedModalRectWithMouseHighlight(context, x+7+i*18,    y+40+(this.inventoryRows+4)*18, 9*18, 2*18, 18, 18, mouseX, mouseY);
        }
        int rowsToDrawDownArrow=inventoryRows;
        if (inventoryRows>6 && !ConfigurationHandler.allowExtraLargeChests())
            rowsToDrawDownArrow=6;
        for (int i=0; i<rowsToDrawDownArrow; i++) {
            this.drawTexturedModalRectWithMouseHighlight(context, x-18, y+17+i*18, 1*18, 2*18, 18, 18, mouseX, mouseY);
        }
        for (int i=0; i<4; i++) {
            this.drawTexturedModalRectWithMouseHighlight(context, x-18, y+28+(i+this.inventoryRows)*18, 9*18, 2*18, 18, 18, mouseX, mouseY);
        }

        for (int i=0; i<36; i++) {
            if (!hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i)) {
                Slot slot = this.handler.slots.get(slotIndexFromPlayerInventoryIndex(i));
                context.drawTexture(RenderPipelines.GUI_TEXTURED, ICONS, x+slot.x, y+slot.y, (float)(7*18+1), (float)(3*18+1), 16, 16, 256, 256);
            }
        }

        if (ConfigurationHandler.enableSearch()) {
            String search = searchWidget.getText().toLowerCase();
            if (!search.isEmpty()) {
                int highlight = (int) Long.parseLong(ConfigurationHandler.getHighlightColor().toUpperCase(), 16);
                for (int i=0; i<this.handler.slots.size(); i++) {
                    Slot slot = this.handler.slots.get(i);
                    Item item = slot.getStack().getItem();
                    if (item == Items.AIR) continue;
                    if (item.getName().getString().toLowerCase().contains(search)) {
                        context.fill(x+slot.x-1, y+slot.y-1, x+slot.x+17, y+slot.y+17, highlight);
                    }
                }
            }
        }
    }
    public static void drawChestInventoryBroom(DrawContext context, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(context, x, y, 11*18, 0, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.sortchest"));
    }

    public static void drawChestInventoryAllDown(DrawContext context, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(context, x, y, 0, 2*18, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.matchdown"));
    }

    public static void drawPlayerInventoryBroom(DrawContext context, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(context, x, y, 11*18, 0, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.sortplayer"));
    }

    public static void drawPlayerInventoryAllUp(DrawContext context, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(context, x, y, 8*18, 2*18, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.matchup"));
    }

    static void drawTexturedModalRectWithMouseHighlight(DrawContext context, int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
        boolean hovering = (mousex >= screenx && mousex < screenx+sizex && mousey >= screeny && mousey < screeny+sizey);
        if (!hovering && ConfigurationHandler.halfSizeButtons()) {
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(0.5f, 0.5f);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, ICONS, screenx*2+sizex/2, screeny*2+sizey/2, (float)textx, (float)texty, sizex, sizey, 256, 256);
            context.getMatrices().popMatrix();
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, ICONS, screenx, screeny, (float)textx, (float)texty, sizex, sizey, 256, 256);
        }
    }

    private static void myTooltip(DrawContext context, int screenx, int screeny, int sizex, int sizey, int mousex, int mousey, Text tooltip) {
        if (tooltip != null && mousex >= screenx && mousex <= screenx+sizex && mousey >= screeny && mousey <= screeny+sizey) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mousex, mousey);
        }
    }


    private static boolean hasShiftDown() {
        long win = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetKey(win, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(win, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean mouseClicked(Click click, boolean handled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();
        if (ConfigurationHandler.enableSearch()) {
            boolean clickedOnWidget = searchWidget.mouseClicked(click, handled);
            searchWidget.setFocused(clickedOnWidget);
            if (clickedOnWidget) {
                return true;
            }
        }
        super.mouseClicked(click, handled);
        if (mouseButton==0) {
            checkForMyButtons(mouseX, mouseY);
        }
        
        if (mouseButton==2) {
            checkForToggleFrozen(mouseX, mouseY);
        }
        return true;
    }
        
    void checkForMyButtons(double mouseX, double mouseY) {
        if (mouseX>=x-18 && mouseX<=x) {                                        // left buttons
            int deltay = (int)mouseY-y;
            if (deltay < this.inventoryRows*18+17)
                clickSlotsInRow((deltay-17)/18);
            else if (deltay < (this.inventoryRows + 4 ) * 18 + 28) {
                clickSlotsInRow((deltay-28)/18);
            }
        } else if (mouseX>x+this.backgroundWidth && mouseX <= x+this.backgroundWidth+18) {   // right buttons
            /* if (mouseY>y+17 && mouseY<y+17+18)
                sortInventory(true);
            else if (mouseY > y+17+18 && mouseY < y+17+36)
                moveMatchingItems(true);
            else if (mouseY>y+28+(this.inventoryRows)*18 && mouseY<y+28+(this.inventoryRows)*18+18)
                sortInventory(false);
            else if (mouseY>y+28+(this.inventoryRows)*18+18 && mouseY<y+28+(this.inventoryRows)*18+36)
                moveMatchingItems(false);                       */
        } else if (mouseX>x+7 && mouseX<x+7+9*18) {                             // top/bottom buttons
            boolean isChest;
            if (mouseY>y-18 && mouseY<y)
                isChest=true;
            else if (mouseY>y+40+(this.inventoryRows+4)*18 && mouseY<y+40+(this.inventoryRows+4)*18+18)
                isChest=false;
            else
                return;
            int column=((int)mouseX-x-7)/18;
            clickSlotsInColumn(column, isChest);
        }
    }
    
    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(input);
        }
        if (ConfigurationHandler.enableSearch() && searchWidget.isActive()) {
            return searchWidget.keyPressed(input);
        }
        return super.keyPressed(input);
    }
    
    @Override
    public boolean charTyped(CharInput input) {
        if (ConfigurationHandler.enableSearch() && searchWidget.isActive()) {
            return searchWidget.charTyped(input);
        }
        return super.charTyped(input);
    }
    
    public String getSearchText() {
        return (ConfigurationHandler.enableSearch() && searchWidget != null) ? searchWidget.getText() : "";
    }

    @Override
    public void close() {
        searchText=searchWidget.getText();
        super.close();
    }
    
    void checkForToggleFrozen(double mouseX, double mouseY) {
        for (int i = 0; i < this.handler.slots.size(); ++i) {
            int invIndex=this.playerInventoryIndexFromSlotIndex(i);
            if (invIndex==-1)
                continue;
            Slot slot = this.handler.slots.get(i);
            if (isPointWithinBounds(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                FrozenSlotDatabase.setSlotFrozen(invIndex, !FrozenSlotDatabase.isSlotFrozen(invIndex));
            }
        }
    }
    
    private void clickSlotsInRow(int row) {
        for (int slot=row*9; slot<=row*9+8; slot++)
            if (hasShiftDown()|| !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
            slotClick(slot, 0, SlotActionType.QUICK_MOVE);
    }

    private void clickSlotsInColumn(int column, boolean isChest) {
        int first=(isChest ? column : inventoryRows*9+column);
        int count=(isChest ? inventoryRows : 4);
        for (int i=0; i<count; i++) {
            int slot=first+i*9;
            if (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
        }
    }
    
    private void sortInventory(boolean isChest) {
        Inventory inv=(isChest ? containerInventory : client.player.getInventory());
        sortInventory((SlotClicker) this, isChest, inv);
    }

    public static void sortInventory(SlotClicker screen, boolean isChest, Inventory inv) {
        int size=isChest ? inv.size() : 36;     // player's Inventory has 41 items which includes armor and left hand, but we don't want these.
        if (size>9*6 && !ConfigurationHandler.allowExtraLargeChests())
            size=9*6;
        for (int toSlot=0; toSlot<size; toSlot++) {
            ItemStack toStack=inv.getStack(toSlot);
            String targetItemName=toStack.getItem().getTranslationKey();
            if (toStack.getItem() == Items.AIR) {
                if (!isChest && toSlot<9)
                    continue;                   // Don't move stuff into empty player hotbar slots
                targetItemName="§§§";           // make sure it is highest so gets sorted last
            }
            
            // First, find an item that fits better into the current slot, but
            // don't remove hotbar things and don't
            // pull things from frozen slots unless Shift is pressed
            if (isChest || toSlot>=9 && (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(toSlot))) {
                for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                    if (!isChest && !hasShiftDown()&& FrozenSlotDatabase.isSlotFrozen(fromSlot))
                        continue;
                    ItemStack slotStack=inv.getStack(fromSlot);
                    if (slotStack.getItem()==Items.AIR)
                        continue;
                    String slotItem=inv.getStack(fromSlot).getItem().getTranslationKey();
                    if (slotItem.compareToIgnoreCase(targetItemName)<0) {
                        targetItemName=slotItem;
                    }
                }
            } else {
                // Hotbar slots: allow filling them up but not replacing armor/weapon
                if (toStack.getCount() >= toStack.getMaxCount()) {
                    continue;
                }
            }
            
            // Next, check for items that we can merge into the current item,
            // or that have the same name but are lower in some respect
            // ( display name, number of enchantments, name of first enchantment, damage ...)

            for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                if (!isChest && !hasShiftDown()) {
                    if (FrozenSlotDatabase.isSlotFrozen(fromSlot)) {
                        continue;
                    }
                }
                toStack=inv.getStack(toSlot);
                ItemStack fromStack=inv.getStack(fromSlot);
                if (fromStack.getItem().getTranslationKey().equals(targetItemName)
                &&  (!toStack.getItem().getTranslationKey().equals(targetItemName)
                    ||    stackShouldGoBefore(fromStack, toStack))) {
                    screen.EasierChests$onMouseClick (null, isChest ? fromSlot : screen.EasierChests$slotIndexfromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);
                    screen.EasierChests$onMouseClick (null, isChest ? toSlot   : screen.EasierChests$slotIndexfromPlayerInventoryIndex(toSlot)  , 0, SlotActionType.PICKUP);
                    screen.EasierChests$onMouseClick (null, isChest ? fromSlot : screen.EasierChests$slotIndexfromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);                    
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
        &&  replacement.getDamage() > original.getDamage()) return false;

        ItemEnchantmentsComponent origEnchs = getEnchantmentsComponent(original);
        ItemEnchantmentsComponent replEnchs = getEnchantmentsComponent(replacement);

        int replSize = replEnchs.getEnchantments().size();
        int origSize = origEnchs.getEnchantments().size();

        if (replSize == 0) {
            if (origSize == 0) return original.getCount() != original.getMaxCount();
            return true;
        }
        if (origSize == 0) return false;
        if (replSize < origSize) return true;
        if (replSize > origSize) return false;
        return false;
    }
    
    private void moveMatchingItems(boolean isChest) {
        moveMatchingItems(this, isChest);
    }
    
    public static void moveMatchingItems(HandledScreen screen, boolean isChestToPlayer) {
        String searchFilter = "";
        if (screen instanceof ExtendedGuiChest) {
            searchFilter = ((ExtendedGuiChest) screen).getSearchText().toLowerCase();
        }

        Inventory from, to;
        int fromSize, toSize;
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Inventory containerInventory = screen.getScreenHandler().getSlot(0).inventory;

        // use 36 for player inventory size so we won't use armor/2h slots
        if (isChestToPlayer) {
            from = containerInventory;            fromSize=from.size();
            to   = minecraft.player.getInventory();    toSize  =36;
        } else {
            from = minecraft.player.getInventory();    fromSize=36;
            to   = containerInventory;            toSize  =to.size();
        }
        if (!ConfigurationHandler.allowExtraLargeChests()) {
            if (fromSize>9*6)   fromSize=9*6;
            if (toSize  >9*6)   toSize=9*6;
        }
        for (int i=0; i<fromSize; i++) {
            if (!isChestToPlayer && !hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i))
                continue;
            ItemStack fromStack = from.getStack(i);
            if (fromStack.getItem() == Items.AIR) continue;
            int slot;
            if (isChestToPlayer) {
                slot=i;
            } else  {
                slot=((SlotClicker)screen).EasierChests$slotIndexfromPlayerInventoryIndex(i);
            }
            if (!searchFilter.isEmpty()) {
                // Search mode: move items whose name matches the active search text
                if (fromStack.getItem().getName().getString().toLowerCase().contains(searchFilter)) {
                    ((SlotClicker)screen).EasierChests$onMouseClick(null, slot, 0, SlotActionType.QUICK_MOVE);
                }
            } else {
                // Default: move items that have a matching counterpart in the destination
                for (int j=0; j<toSize; j++) {
                    ItemStack toStack = to.getStack(j);
                    if (ItemStack.areItemsAndComponentsEqual(fromStack, toStack)) {
                        ((SlotClicker)screen).EasierChests$onMouseClick(null, slot, 0, SlotActionType.QUICK_MOVE);
                    }
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