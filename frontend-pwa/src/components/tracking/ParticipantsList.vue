<script setup lang="ts">
import { mdiAccountCircle, mdiClose } from '@mdi/js';
import Button from 'primevue/button';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { Division } from '@/types/Division';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import { Participant } from '@/types/Participant';
import { useI18n } from 'vue-i18n';

const props = withDefaults(
    defineProps<{
        participants?: Participant[];
        divisions?: Division[];
        adminMode?: boolean;
    }>(),
    {
        participants: () => [],
        divisions: () => [],
        adminMode: false,
    }
);

const emit = defineEmits<{
    assignDivision: [division: Division, participant: Participant];
    unassignDivision: [division: Division];
    kickParticipant: [participant: Participant];
}>();

const { t } = useI18n();

function kickParticipant(participant: Participant) {
    if (
        !confirm(
            t('components.participants_list.confirmation_message', {
                participantName: participant.name,
            })
        )
    )
        return;
    emit('kickParticipant', participant);
}
</script>

<template>
    <div class="flex flex-col gap-2 items-stretch justify-start">
        <InputGroup v-for="participant of participants" :key="participant.id">
            <InputGroupAddon
                class="grow justify-start items-center overflow-hidden text-ellipsis"
            >
                <MdiIcon
                    class="ml-2 mr-3"
                    :icon="mdiAccountCircle"
                    :style="{ color: participant.color }"
                />
                {{ participant.name }}
            </InputGroupAddon>
            <Button
                v-if="adminMode"
                class="shrink-0 w-16"
                @click="kickParticipant(participant)"
                severity="secondary"
            >
                <template #icon>
                    <MdiIcon :icon="mdiClose" />
                </template>
            </Button>
        </InputGroup>
    </div>
</template>
