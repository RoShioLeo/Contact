package cloud.lemonslice.contact.data.provider;

import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public final class AdvancementConsumer implements Consumer<Consumer<Advancement>>
{
    @Override
    public void accept(Consumer<Advancement> consumer)
    {
        Advancement root = Advancement.Builder.advancement()
                .display(ItemRegistry.MAIL.get(),
                        Component.translatable("advancements.contact.root.title"),
                        Component.translatable("advancements.contact.root.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK, true, true, false)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, "contact:root");
        Advancement receivePostcard = Advancement.Builder.advancement()
                .parent(root)
                .display(ItemRegistry.OPENED_MAIL.get(),
                        Component.translatable("advancements.contact.receive_postcard.title"),
                        Component.translatable("advancements.contact.receive_postcard.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, "contact:receive_postcard");
        Advancement sendPostcard = Advancement.Builder.advancement()
                .parent(root)
                .display(ItemRegistry.POSTCARD.get(),
                        Component.translatable("advancements.contact.send_postcard.title"),
                        Component.translatable("advancements.contact.send_postcard.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, "contact:send_postcard");
        Advancement sendInPerson = Advancement.Builder.advancement()
                .parent(sendPostcard)
                .display(ItemRegistry.WHITE_MAILBOX_ITEM.get(),
                        Component.translatable("advancements.contact.send_in_person.title"),
                        Component.translatable("advancements.contact.send_in_person.description"),
                        null, FrameType.GOAL, true, true, false)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, "contact:send_in_person");
        Advancement fromAnotherWorld = Advancement.Builder.advancement()
                .parent(receivePostcard)
                .display(ItemRegistry.PARCEL.get(),
                        Component.translatable("advancements.contact.from_another_world.title"),
                        Component.translatable("advancements.contact.from_another_world.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, "contact:from_another_world");
    }
}
