package eu.reinalter.noah.clearfix.mixin;

import eu.reinalter.noah.clearfix.Clearfix;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ClearCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

@Mixin(ClearCommand.class)
public abstract class ClearCommandMixin {
    @Unique
    private static HashMap<ServerPlayerEntity, Long> clearInventoryTriesHashMap = new HashMap<>();

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void injected(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Predicate<ItemStack> item, int maxCount, CallbackInfoReturnable<Integer> cir) {
        if (source.getPlayer() == null) {
            return;
        }
        if (clearInventoryTriesHashMap.containsKey(source.getPlayer()) && System.currentTimeMillis() - clearInventoryTriesHashMap.get(source.getPlayer()) < 10000) {
            clearInventoryTriesHashMap.remove(source.getPlayer());
        } else {
            clearInventoryTriesHashMap.put(source.getPlayer(), System.currentTimeMillis());
            cir.cancel();
            Clearfix.logger().info("Clear-fix stopped the execution of a clear command");
            source.sendFeedback(() -> Text.translatable("clear-fix.cancel"), false);
        }
    }
}
