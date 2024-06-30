package eu.reinalter.noah.clearfix.mixin;

import com.mojang.datafixers.util.Pair;
import eu.reinalter.noah.clearfix.Clearfix;
import net.minecraft.entity.Entity;
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

import java.util.*;
import java.util.function.Predicate;

@Mixin(ClearCommand.class)
public abstract class ClearCommandMixin {
    @Unique
    private static HashMap<Pair<UUID, List<UUID>>, Long> clearInventoryTriesHashMap = new HashMap<>();

    @Inject(method = "execute(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Ljava/util/function/Predicate;I)I", at = @At("HEAD"), cancellable = true)
    private static void injected(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Predicate<ItemStack> item, int maxCount, CallbackInfoReturnable<Integer> cir) {
        if (source.getPlayer() == null) {
            return;
        }

        List<UUID> targetUUIDs = targets.stream().map(Entity::getUuid).toList();
        Pair<UUID, List<UUID>> CallerAndTargetsPair = new Pair<>(source.getPlayer().getUuid(), targetUUIDs);

        if (clearInventoryTriesHashMap.containsKey(CallerAndTargetsPair) && System.currentTimeMillis() - clearInventoryTriesHashMap.get(CallerAndTargetsPair) < 10000) {
            clearInventoryTriesHashMap.remove(CallerAndTargetsPair);
        } else {
            clearInventoryTriesHashMap.put(CallerAndTargetsPair, System.currentTimeMillis());
            cir.cancel();
            Clearfix.logger().info("Clear-fix stopped the execution of a clear command");
            source.sendFeedback(() -> Text.translatableWithFallback("clear-fix.cancel", "Repeat the command again in the next 10s to execute"), false);
        }
    }
}
