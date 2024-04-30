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
import QRCodeVue3 from 'qrcode-vue3';
import InputText from 'primevue/inputtext';

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
            <template #title>Invite Participants</template>
            <template #action-right>
                <Button
                    v-if="shareSupported"
                    label="Share"
                    @click="shareInvitationLink"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiShare" />
                    </template>
                </Button>
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
            </template>
            <template #default>
                <div
                    class="h-full w-full flex flex-col justify-start items-center gap-4"
                >
                    <QRCodeVue3
                        imgclass="bg-white p-4"
                        :value="link"
                        :dots-options="{
                            type: 'square',
                        }"
                        :corners-square-options="{
                            type: undefined,
                        }"
                        :corners-dot-options="{
                            type: undefined,
                        }"
                    />
                    <InputText
                        class="w-full text-center"
                        readonly
                        auto-resize
                        :model-value="link"
                    />
                </div>
            </template>
        </DefaultLayout>
    </Sidebar>
</template>
