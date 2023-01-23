package cloud.lemonslice.contact.mixin;

import cloud.lemonslice.contact.client.gui.tooltip.PackageTooltipComponent;
import cloud.lemonslice.contact.client.item.PackageTooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin
{
    @Inject(method = "of(Lnet/minecraft/client/item/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void injectOf(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir)
    {
        if (data instanceof PackageTooltipData)
        {
            cir.setReturnValue(new PackageTooltipComponent((PackageTooltipData) data));
        }
    }
}
