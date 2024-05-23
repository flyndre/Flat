<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { mdiArrowLeft, mdiContentCopy, mdiShare } from '@mdi/js';
import { useClipboard, useShare, watchOnce } from '@vueuse/core';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Sidebar from 'primevue/sidebar';
import { useToast } from 'primevue/usetoast';
import QRCodeVue3 from 'qrcode-vue3';
import { useI18n } from 'vue-i18n';

const visible = defineModel<boolean>('visible', {
    default: false,
});
const props = defineProps<{
    link: string;
    collectionName?: string;
}>();

const { add } = useToast();
const { t } = useI18n();

const { isSupported: copySupported, copy, copied } = useClipboard();
function copyInvitationLink() {
    copy(props.link);
    watchOnce(copied, (success) => {
        add({
            summary: success
                ? t('components.invitation_dialog.copy_success')
                : t('components.invitation_dialog.copy_failed'),
            severity: success ? 'success' : 'error',
            closable: true,
            life: TOAST_LIFE,
        });
    });
}

const { isSupported: shareSupported, share } = useShare({
    url: props.link,
    title: t('components.invitation_dialog.share_title', {
        collectionName: props.collectionName,
    }),
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
                    :label="$t('universal.back')"
                    severity="secondary"
                    @click="visible = false"
                    text
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </template>
            <template #title>
                {{ $t('components.invitation_dialog.title') }}
            </template>
            <template #action-right>
                <Button
                    v-if="shareSupported"
                    :label="$t('universal.share')"
                    @click="shareInvitationLink"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiShare" />
                    </template>
                </Button>
                <Button
                    v-if="copySupported"
                    :text="shareSupported"
                    :label="$t('universal.copy')"
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
