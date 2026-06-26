<script setup lang="ts">
import { mdiArrowLeft, mdiCheck, mdiClose, mdiCloseBox, mdiCrosshairs, mdiCrosshairsGps, mdiCubeOutline, mdiDeleteForever, mdiDotsHorizontal, mdiEyeOff, mdiFitToScreen, mdiInformation, mdiMagnify, mdiMap, mdiPalette, mdiTableColumn, mdiTableColumnRemove, mdiTextureBox, mdiVectorSquareEdit } from '@mdi/js';
import Button from 'primevue/button';
import Sidebar from 'primevue/sidebar';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { useI18n } from 'vue-i18n';
import { useSessionStorage } from '@vueuse/core';
import { ref } from 'vue';
import Message from 'primevue/message';
import Dialog from 'primevue/dialog';
import { isOnMobile } from '@/util/mobileDetection';

const { t } = useI18n();
const visible = ref(false);
const demoInfoDismissed = useSessionStorage('demo-info-dismissed', false);
</script>

<template>
    <Message
        v-show="!demoInfoDismissed"
        class="max-w-[min(300px,90dvw)]"
        severity="secondary"
        @close="demoInfoDismissed = true"
    >
        <template #messageicon>
            <MdiTextButtonIcon :icon="mdiInformation" />
        </template>
        {{ t('home.demo.info') }}
        <Button class="p-0 rounded-none" link @click="visible = true">
            {{ t('home.demo.more') }}
        </Button>
    </Message>
    <Dialog
        v-model:visible="visible"
        :draggable="false"
        :closable="false"
        modal
        dismissable-mask
        :position="isOnMobile ? 'bottom' : 'top'"
        class="overflow-hidden max-w-md"
        :header="t('home.demo.title')"
    >
        {{ t('home.demo.explanation') }}
        <template #footer>
            <div
                class="w-full flex flex-row justify-stretch gap-2 [&>*]:grow"
            >
                <Button
                    :label="t('universal.close')""
                    @click="visible = false"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiClose" />
                    </template>
                </Button>
                <Button
                    :label="t('home.demo.hide')"
                    severity="secondary"
                    @click="visible = false; demoInfoDismissed = true"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiEyeOff" />
                    </template>
                </Button>
            </div>
        </template>
    </Dialog>
</template>
