package org.jurassicraft.server.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Predicate;

public class CustomSlot extends SlotItemHandler {
    private Predicate<ItemStack> item;

    public CustomSlot(IItemHandler inventory, int slotIndex, int xPosition, int yPosition, Predicate<ItemStack> item) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.item = item;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.item.test(stack);
    }
}
