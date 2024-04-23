<script setup lang="ts">
import {
    mdiArrowLeft,
    mdiCheck,
    mdiCloseBox,
    mdiCrosshairs,
    mdiCrosshairsGps,
    mdiDeleteForever,
    mdiDotsHorizontal,
    mdiFitToScreen,
    mdiMagnify,
    mdiMap,
    mdiPalette,
    mdiTableColumn,
    mdiTableColumnRemove,
    mdiTextureBox,
    mdiVectorSquareEdit,
} from '@mdi/js';
import Button from 'primevue/button';
import Sidebar from 'primevue/sidebar';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';

const visible = defineModel<boolean>('visible', {
    default: false,
});
const props = defineProps<{
    participants: any[];
    assignDivisionHandler: (participantId: string, divisionId: string) => any;
    unassignDivisionHandler: (participantId: string, divisionId: string) => any;
    removeParticipantHandler: (participantId: string) => any;
}>();

const participants = [
    {
        name: 'hello',
        id: 'sss',
        area: 'dsdssd',
    },
];
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
            <template #default> ... </template>
        </DefaultLayout>
    </Sidebar>
</template>
