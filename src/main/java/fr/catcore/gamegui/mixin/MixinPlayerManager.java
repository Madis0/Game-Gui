package fr.catcore.gamegui.mixin;

import com.mojang.brigadier.tree.CommandNode;
import fr.catcore.gamegui.GameGuiCustomItems;
import fr.catcore.gamegui.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Shadow @Final private MinecraftServer server;

    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    private void addCustomItem(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ItemStack openGame = new ItemStack(GameGuiCustomItems.OPEN_GAME);
        if (!player.inventory.contains(openGame) && Utils.hasOpenPermission(player)) {
            player.inventory.insertStack(openGame);
        }

        ItemStack joinGame = new ItemStack(GameGuiCustomItems.JOIN_GAME);
        if (!player.inventory.contains(joinGame)) {
            player.inventory.insertStack(joinGame);
        }
    }
}
