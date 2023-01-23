package cloud.lemonslice.contact.data.provider;

import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public final class AdvancementConsumer implements Consumer<Consumer<Advancement>>
{
    @Override
    public void accept(Consumer<Advancement> consumer)
    {
        Advancement root = Advancement.Builder.create()
                .display(ItemRegistry.LETTER,
                        Text.translatable("advancements.contact.root.title"),
                        Text.translatable("advancements.contact.root.description"),
                        new Identifier("textures/gui/advancements/backgrounds/stone.png"),
                        AdvancementFrame.TASK, true, true, false)
                .criterion("impossible", new ImpossibleCriterion.Conditions())
                .build(consumer, "contact:root");
        Advancement receivePostcard = Advancement.Builder.create()
                .parent(root)
                .display(ItemRegistry.ENVELOPE,
                        Text.translatable("advancements.contact.receive_postcard.title"),
                        Text.translatable("advancements.contact.receive_postcard.description"),
                        null, AdvancementFrame.TASK, true, true, false)
                .criterion("impossible", new ImpossibleCriterion.Conditions())
                .build(consumer, "contact:receive_postcard");
        Advancement sendPostcard = Advancement.Builder.create()
                .parent(root)
                .display(ItemRegistry.POSTCARD,
                        Text.translatable("advancements.contact.send_postcard.title"),
                        Text.translatable("advancements.contact.send_postcard.description"),
                        null, AdvancementFrame.TASK, true, true, false)
                .criterion("impossible", new ImpossibleCriterion.Conditions())
                .build(consumer, "contact:send_postcard");
        Advancement sendInPerson = Advancement.Builder.create()
                .parent(sendPostcard)
                .display(ItemRegistry.WHITE_MAILBOX_ITEM,
                        Text.translatable("advancements.contact.send_in_person.title"),
                        Text.translatable("advancements.contact.send_in_person.description"),
                        null, AdvancementFrame.GOAL, true, true, false)
                .criterion("impossible", new ImpossibleCriterion.Conditions())
                .build(consumer, "contact:send_in_person");
        Advancement fromAnotherWorld = Advancement.Builder.create()
                .parent(receivePostcard)
                .display(ItemRegistry.PARCEL,
                        Text.translatable("advancements.contact.from_another_world.title"),
                        Text.translatable("advancements.contact.from_another_world.description"),
                        null, AdvancementFrame.TASK, true, true, false)
                .criterion("impossible", new ImpossibleCriterion.Conditions())
                .build(consumer, "contact:from_another_world");
    }
}
