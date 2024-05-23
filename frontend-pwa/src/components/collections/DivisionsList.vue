<script setup lang="ts">
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { Division } from '@/types/Division';
import { mdiPencil, mdiPencilPlus, mdiTextureBox } from '@mdi/js';
import Button from 'primevue/button';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import Panel from 'primevue/panel';
import { computed } from 'vue';

const divisions = defineModel<Division[]>();
const props = withDefaults(
    defineProps<{
        editDivisionsHandler: () => any;
    }>(),
    {
        editDivisionsHandler: () => {},
    }
);

const divisionsPresent = computed(() => divisions.value?.length > 0);
</script>

<template>
    <Panel
        :header="$t('components.divisions_preview.header')"
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
                :placeholder="$t('components.divisions_preview.division_name')"
            />
        </IconField>
        <Button
            class="justify-center"
            :severity="divisionsPresent ? 'secondary' : 'primary'"
            @click="editDivisionsHandler"
        >
            <template #default>
                <MdiTextButtonIcon
                    :icon="divisionsPresent ? mdiPencil : mdiPencilPlus"
                />
                {{
                    $t(
                        'components.divisions_preview.' +
                            (divisionsPresent
                                ? 'edit_divisions'
                                : 'add_divisions')
                    )
                }}
            </template>
        </Button>
    </Panel>
</template>
