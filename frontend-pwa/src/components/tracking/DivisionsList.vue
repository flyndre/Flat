<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { Division } from '@/types/Division';
import { Participant } from '@/types/Participant';
import { mdiAccountCircle, mdiAccountOff, mdiTextureBox } from '@mdi/js';
import Dropdown from 'primevue/dropdown';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';

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
}>();

function assignDivision(division: Division, participant: Participant) {
    emit('assignDivision', division, participant);
}

function unassignDivision(division: Division) {
    emit('unassignDivision', division);
}

function updateAssignment(division: Division, participant: Participant | null) {
    if (participant == null) {
        unassignDivision(division);
    } else {
        assignDivision(division, participant);
    }
}

function getAssignedParticipant(division: Division) {
    return props.participants.find((p) => p.id === division.clientId);
}
</script>

<template>
    <div class="flex flex-col gap-2 items-stretch justify-start">
        <InputGroup v-for="division of divisions" :key="division.id">
            <InputGroupAddon
                class="grow basis-2/5 justify-start items-center overflow-hidden text-ellipsis"
            >
                <MdiIcon
                    class="ml-2 mr-3"
                    :style="{
                        color: division.color,
                    }"
                    :icon="mdiTextureBox"
                />
                {{ division.name }}
            </InputGroupAddon>
            <Dropdown
                class="basis-3/5"
                :model-value="getAssignedParticipant(division)"
                :options="participants"
                @update:model-value="(v) => updateAssignment(division, v)"
                :disabled="!adminMode"
                show-clear
            >
                <template #value="slotProps">
                    <MdiTextButtonIcon
                        :icon="
                            slotProps.value == null
                                ? mdiAccountOff
                                : mdiAccountCircle
                        "
                        :style="{ color: slotProps.value?.color }"
                    />
                    {{ slotProps.value?.name ?? 'Unassigned' }}
                </template>
                <template #option="slotProps">
                    <MdiTextButtonIcon
                        :icon="mdiAccountCircle"
                        :style="{ color: slotProps.option.color }"
                    />
                    {{ slotProps.option.name }}
                </template>
            </Dropdown>
        </InputGroup>
    </div>
</template>
