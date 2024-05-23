<script setup lang="ts">
import { CollectionStats } from '@/types/stats/CollectionStats';
import { mdiChevronRight } from '@mdi/js';
import { useRouteQuery } from '@vueuse/router';
import Button from 'primevue/button';
import { computed } from 'vue';
import MdiIcon from '../icons/MdiIcon.vue';
import StatsDialog from './StatsDialog.vue';
import { useI18n } from 'vue-i18n';
import { getDateTime } from '@/util/datetimeUtils';

const props = defineProps<{
    stats: CollectionStats;
}>();

const { locale } = useI18n();
const openStatsId = useRouteQuery('stats');
const dialogVisible = computed({
    get: () => openStatsId.value === props.stats.id,
    set: (v) => (openStatsId.value = v ? props.stats.id : undefined),
});
const displayedDate = computed(
    () =>
        getDateTime(props.stats.startDate, locale.value) ??
        getDateTime(props.stats.finishDate, locale.value)
);
</script>

<template>
    <Button
        class="w-full"
        severity="secondary"
        outlined
        @click="dialogVisible = true"
        v-bind="$attrs"
    >
        <template #default>
            <div
                class="w-full flex flex-row justify-between items-center gap-2"
            >
                <span class="text-left break-all">
                    {{ stats.name }}
                    <span class="opacity-50">
                        {{ displayedDate }}
                    </span>
                </span>
                <MdiIcon :icon="mdiChevronRight" />
            </div>
        </template>
    </Button>

    <StatsDialog v-model:visible="dialogVisible" :stats />
</template>
