package fr.catcore.gamegui.inventory;

import fr.catcore.gamegui.builder.OpenGameTypeBuilder;
import fr.catcore.gamegui.builder.OpenGameTypeEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.function.Consumer;

public class OpenGameTypeInventory implements Inventory {
    private static final int WIDTH = 9;
    private static final int PADDING = 1;
    private static final int PADDED_WIDTH = 7;
    private final OpenGameTypeEntry[] elements = new OpenGameTypeEntry[this.size()];
    private final ServerPlayerEntity player;
    private final Consumer<OpenGameTypeBuilder> builder;

    public OpenGameTypeInventory(ServerPlayerEntity player, Consumer<OpenGameTypeBuilder> builder) {
        this.player = player;
        this.builder = builder;
        this.buildGrid();
    }

    private void buildGrid() {
        OpenGameTypeBuilder builder = new OpenGameTypeBuilder();
        this.builder.accept(builder);
        this.buildGrid(builder.getElements());
    }

    private void buildGrid(OpenGameTypeEntry[] elements) {
        Arrays.fill(this.elements, (Object)null);
        int rows = MathHelper.ceil((double)elements.length / 7.0D);

        for(int row = 0; row < rows; ++row) {
            OpenGameTypeEntry[] resolved = this.resolveRow(elements, row);
            int minColumn = (9 - resolved.length) / 2;

            for(int column = 0; column < resolved.length; ++column) {
                OpenGameTypeEntry element = resolved[column];
                this.elements[column + minColumn + row * 9] = element;
            }
        }

    }

    private OpenGameTypeEntry[] resolveRow(OpenGameTypeEntry[] elements, int row) {
        int minId = 2147483647;
        int maxId = -2147483648;
        int rowStart = row * 7;
        int rowEnd = Math.min(rowStart + 7, elements.length);

        for(int idx = rowStart; idx < rowEnd; ++idx) {
            if (elements[idx] != null) {
                if (idx < minId) {
                    minId = idx;
                }

                if (idx > maxId) {
                    maxId = idx;
                }
            }
        }

        OpenGameTypeEntry[] resolved = new OpenGameTypeEntry[maxId - minId + 1];
        System.arraycopy(elements, minId, resolved, 0, resolved.length);
        return resolved;
    }

    public int size() {
        return 64;
    }

    public boolean isEmpty() {
        return false;
    }

    public int getMaxCountPerStack() {
        return 1;
    }

    public ItemStack getStack(int index) {
        OpenGameTypeEntry element = this.elements[index];
        return element == null ? ItemStack.EMPTY : element.createIcon(this.player);
    }

    public ItemStack removeStack(int index, int count) {
        this.handleElementClick(index);
        return ItemStack.EMPTY;
    }

    public ItemStack removeStack(int index) {
        this.handleElementClick(index);
        return ItemStack.EMPTY;
    }

    private void handleElementClick(int index) {
        this.player.inventory.setCursorStack(ItemStack.EMPTY);
        this.player.updateCursorStack();
        OpenGameTypeEntry element = this.elements[index];
        if (element != null) {
            element.onClick(this.player);
        }

        this.buildGrid();
        this.player.onHandlerRegistered(this.player.currentScreenHandler, this.player.currentScreenHandler.getStacks());
    }

    public void setStack(int slot, ItemStack stack) {
    }

    public void markDirty() {
    }

    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    public void clear() {
    }
}
