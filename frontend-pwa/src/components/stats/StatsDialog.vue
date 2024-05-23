<script setup lang="ts">
import { mdiArrowLeft, mdiDelete, mdiShare } from '@mdi/js';
import Button from 'primevue/button';
import Sidebar from 'primevue/sidebar';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import StatsDisplay from './StatsDisplay.vue';
import { ref } from 'vue';
import { CollectionStats } from '@/types/stats/CollectionStats';
import { useShare } from '@vueuse/core';
// import { getElementSnapshot } from '@/util/snapshotUtils';
import { useToast } from 'primevue/usetoast';
import { useI18n } from 'vue-i18n';
import { TOAST_LIFE } from '@/data/constants';
import { collectionStatsDB } from '@/data/collectionStats';

const visible = defineModel<boolean>('visible', {
    default: false,
});
const props = defineProps<{
    stats?: CollectionStats;
}>();

const statsElement = ref<HTMLDivElement>(undefined);

const { add } = useToast();
const { t } = useI18n();
const { share, isSupported: shareSupported } = useShare();

const shareLoading = ref(false);

async function shareNow() {
    shareLoading.value = true;
    try {
        // const snapshot = await getElementSnapshot(statsElement.value);
        const topParticipant = props.stats.participantStats.reduce((p, c) =>
            p.coveredDistance > c.coveredDistance ? p : c
        );
        await share({
            text: [
                t('components.stats_dialog.share_title', {
                    collectionName: props.stats.name,
                    startDay: props.stats.startDate.toLocaleDateString(),
                }),
                t('components.stats_dialog.share_text', {
                    totalArea: props.stats.converedArea,
                    topParticipant: topParticipant.name,
                    topDistance: topParticipant.coveredDistance,
                }),
            ].join('\n'),
            // files: [snapshot],
        });
    } catch (e) {
        console.log(e);
        add({
            closable: true,
            life: TOAST_LIFE,
            severity: 'error',
            summary: t('components.stats_dialog.share_failed'),
        });
    }
    shareLoading.value = false;
}

async function deleteStats() {
    try {
        await collectionStatsDB.delete(props.stats.id);
        visible.value = false;
        add({
            closable: true,
            life: TOAST_LIFE,
            severity: 'success',
            summary: t('components.stats_dialog.delete_success'),
        });
    } catch (e) {
        add({
            closable: true,
            life: TOAST_LIFE,
            severity: 'error',
            summary: t('components.stats_dialog.delete_failed'),
        });
    }
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
                {{ $t('components.stats_dialog.title') }}
            </template>
            <template #action-right>
                <Button
                    v-if="shareSupported"
                    :label="$t('universal.share')"
                    :loading="shareLoading"
                    @click="shareNow"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiShare" />
                    </template>
                </Button>
            </template>
            <template #default>
                <div class="flex flex-col items-stretch justify-start gap-2">
                    <div ref="statsElement">
                        <StatsDisplay :stats />
                    </div>
                    <div class="flex flex-row justify-center">
                        <Button
                            class="grow-0"
                            :label="$t('components.stats_dialog.delete')"
                            severity="danger"
                            text
                            @click="deleteStats"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiDelete" />
                            </template>
                        </Button>
                    </div>
                </div>
            </template>
        </DefaultLayout>
    </Sidebar>
</template>
