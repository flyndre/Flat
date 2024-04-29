<script setup lang="ts">
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { mdiArrowLeft, mdiContentCopy, mdiShare } from '@mdi/js';
import { useClipboard, useShare, watchOnce } from '@vueuse/core';
import Button from 'primevue/button';
import Sidebar from 'primevue/sidebar';
import Textarea from 'primevue/textarea';
import { useToast } from 'primevue/usetoast';
import MdiTextButtonIcon from '../icons/MdiTextButtonIcon.vue';

const visible = defineModel<boolean>('visible', {
    default: false,
});
const props = defineProps<{
    link: string;
}>();

const { add } = useToast();

const { isSupported: copySupported, copy, copied } = useClipboard();
function copyInvitationLink() {
    copy(props.link);
    watchOnce(copied, () => {
        add({
            summary: 'Invitation link copied!',
            severity: 'success',
            closable: true,
            life: TOAST_LIFE,
        });
    });
}

const { isSupported: shareSupported, share } = useShare({
    url: props.link,
    title: `Join ${'<Collection Name>'}`,
    text: '...',
});
function shareInvitationLink() {
    share();
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
            <template #action-right>
                <Button
                    v-if="copySupported"
                    :text="shareSupported"
                    label="Copy"
                    @click="copyInvitationLink"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiContentCopy" />
                    </template>
                </Button>
                <Button
                    v-if="shareSupported"
                    label="Share"
                    @click="shareInvitationLink"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiShare" />
                    </template>
                </Button>
            </template>
            <template #default>
                <span>
                    To invite people to parttake in your collection campaign,
                    share this invitation link:
                </span>
                <Textarea
                    class="w-full text-center"
                    readonly
                    auto-resize
                    :model-value="link"
                />
            </template>
        </DefaultLayout>
    </Sidebar>
</template>
