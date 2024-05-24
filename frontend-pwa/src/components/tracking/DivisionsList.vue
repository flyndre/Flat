<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { Division } from '@/types/Division';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { mdiAccountCircle, mdiAccountOff, mdiTextureBox } from '@mdi/js';
import Dropdown from 'primevue/dropdown';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import { computed } from 'vue';

const props = withDefaults(
    defineProps<{
        participants?: ParticipantTrack[];
        divisions?: Division[];
        adminMode?: boolean;
    }>(),
    {
        participants: () => [],
        divisions: () => [],
        adminMode: false,
    }
);

const activeParticipants = computed(() =>
    props.participants.filter((p) => p.active !== false)
);

const emit = defineEmits<{
    assignDivision: [division: Division, participant: ParticipantTrack];
    unassignDivision: [division: Division];
}>();

function assignDivision(division: Division, participant: ParticipantTrack) {
    emit('assignDivision', division, participant);
}

function unassignDivision(division: Division) {
    emit('unassignDivision', division);
}

function updateAssignment(
    division: Division,
    participant: ParticipantTrack | null
) {
    if (participant == null) {
        unassignDivision(division);
    } else {
        assignDivision(division, participant);
    }
}

function getAssignedParticipant(division: Division) {
    return props.participants.find(
        (p) => p.id === division.clientId && p.active !== false
    );
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
                <span class="font-semibold">{{ division.name }}</span>
            </InputGroupAddon>
            <Dropdown
                class="basis-3/5"
                :model-value="getAssignedParticipant(division)"
                :options="activeParticipants"
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
                    {{
                        slotProps.value?.name ??
                        $t('components.division_assignment.unassigned')
                    }}
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
