<script setup lang="ts">
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { Division } from '@/types/Division';
import { mdiPencil, mdiPencilPlus, mdiTextureBox } from '@mdi/js';
import Button from 'primevue/button';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import Panel from 'primevue/panel';

const divisions = defineModel<Division[]>();
const props = withDefaults(
    defineProps<{
        editDivisionsHandler: () => any;
    }>(),
    {
        editDivisionsHandler: () => {},
    }
);
</script>

<template>
    <Panel
        header="Divisions"
        :pt="{
            root: { class: 'border-none' },
            header: { class: 'px-0' },
            content: { class: 'flex flex-col gap-1.5 p-0' },
        }"
    >
        <IconField
            v-for="division of divisions"
            class="grow"
            icon-position="left"
        >
            <MdiInputIcon
                :style="{
                    color: division.color,
                }"
                :icon="mdiTextureBox"
            />
            <InputText
                class="w-full"
                v-model="division.name"
                placeholder="Area Name"
            />
        </IconField>
        <Button
            class="justify-center"
            severity="secondary"
            @click="editDivisionsHandler"
        >
            <template #default>
                <MdiTextButtonIcon
                    :icon="divisions?.length > 0 ? mdiPencil : mdiPencilPlus"
                />
                {{ (divisions?.length > 0 ? 'Edit' : 'Add') + ' Divisions' }}
            </template>
        </Button>
    </Panel>
</template>
