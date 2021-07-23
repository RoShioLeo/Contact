package cloud.lemonslice.intercourse.data.provider;

import cloud.lemonslice.intercourse.common.block.BlockRegistry;
import cloud.lemonslice.intercourse.common.item.ItemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;

public final class AdvancementConsumer implements Consumer<Consumer<Advancement>>
{
    @Override
    public void accept(Consumer<Advancement> consumer)
    {
        Advancement root = Advancement.Builder.builder()
                .withDisplay(ItemRegistry.MAIL,
                        new TranslationTextComponent("advancements.intercourse.root.title"),
                        new TranslationTextComponent("advancements.intercourse.root.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK, true, true, false)
                .withCriterion("impossible", new ImpossibleTrigger.Instance())
                .register(consumer, "intercourse:root");
        Advancement receivePostcard = Advancement.Builder.builder()
                .withParent(root)
                .withDisplay(ItemRegistry.OPENED_MAIL,
                        new TranslationTextComponent("advancements.intercourse.receive_postcard.title"),
                        new TranslationTextComponent("advancements.intercourse.receive_postcard.description"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("impossible", new ImpossibleTrigger.Instance())
                .register(consumer, "intercourse:receive_postcard");
        Advancement sendPostcard = Advancement.Builder.builder()
                .withParent(root)
                .withDisplay(ItemRegistry.POSTCARD,
                        new TranslationTextComponent("advancements.intercourse.send_postcard.title"),
                        new TranslationTextComponent("advancements.intercourse.send_postcard.description"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("impossible", new ImpossibleTrigger.Instance())
                .register(consumer, "intercourse:send_postcard");
        Advancement sendInPerson = Advancement.Builder.builder()
                .withParent(sendPostcard)
                .withDisplay(BlockRegistry.WHITE_MAILBOX_ITEM,
                        new TranslationTextComponent("advancements.intercourse.send_in_person.title"),
                        new TranslationTextComponent("advancements.intercourse.send_in_person.description"),
                        null, FrameType.GOAL, true, true, false)
                .withCriterion("impossible", new ImpossibleTrigger.Instance())
                .register(consumer, "intercourse:send_in_person");
        Advancement fromAnotherWorld = Advancement.Builder.builder()
                .withParent(receivePostcard)
                .withDisplay(ItemRegistry.PARCEL,
                        new TranslationTextComponent("advancements.intercourse.from_another_world.title"),
                        new TranslationTextComponent("advancements.intercourse.from_another_world.description"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("impossible", new ImpossibleTrigger.Instance())
                .register(consumer, "intercourse:from_another_world");
    }
}
