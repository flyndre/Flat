<script setup lang="ts">
import { mdiAccount, mdiArrowLeft, mdiClose } from '@mdi/js';
import Button from 'primevue/button';
import Sidebar from 'primevue/sidebar';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Division } from '@/types/Division';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import { Participant } from '@/types/Participant';

const visible = defineModel<boolean>('visible', {
    default: false,
});

const props = defineProps<{
    participants: Participant[];
    divisions: Division[];
}>();

const emit = defineEmits<{
    assignDivision: [division: Division, participant: Participant];
    unassignDivision: [division: Division];
    kickParticipant: [participant: Participant];
}>();

function assignDivision(division: Division, participant: Participant) {
    emit('assignDivision', division, participant);
}

function unassignDivision(division: Division) {
    emit('unassignDivision', division);
}

function kickParticipant(participant: Participant) {
    emit('kickParticipant', participant);
}
</script>

<template>
    <Sidebar
        class="w-full max-w-[787px] h-fit rounded-t-xl -bottom-px p-0 overflow-hidden"
        v-model:visible="visible"
        modal
        position="bottom"
        :block-scroll="true"
        :show-close-icon="false"
        :pt="{
            header: {
                class: 'hidden',
            },
            content: {
                class: 'h-full flex flex-col justify-stretch items-stretch p-0',
            },
        }"
    >
        <DefaultLayout height="80vh">
            <template #action-left>
                <Button
                    label="Back"
                    severity="secondary"
                    @click="visible = false"
                    text
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </template>
            <template #title>Manage Participants</template>
            <template #default>
                <div class="flex flex-col gap-2 items-stretch justify-start">
                    <InputGroup
                        v-for="participant of participants"
                        :key="participant.clientId"
                    >
                        <InputGroupAddon
                            class="grow justify-start items-center overflow-hidden text-ellipsis"
                        >
                            <MdiIcon class="ml-2 mr-3" :icon="mdiAccount" />
                            {{ participant.username }}
                        </InputGroupAddon>
                        <Button
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
        </DefaultLayout>
    </Sidebar>
</template>
