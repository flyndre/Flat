<script setup lang="ts">
import { CollectionStats } from '@/types/stats/CollectionStats';
import { computed } from 'vue';
import MapWithControls from '../map/MapWithControls.vue';
import { formatDateRange, getDuration } from '@/util/datetimeUtils';
import { useI18n } from 'vue-i18n';
import Panel from 'primevue/panel';
import { mdiCalendar, mdiTextureBox, mdiTimer, mdiTimerOutline } from '@mdi/js';
import MdiTextButtonIcon from '../icons/MdiTextButtonIcon.vue';

const props = defineProps<{
    stats?: CollectionStats;
}>();
const statsPresent = computed(() => props.stats != null);

const { locale } = useI18n();
const dateRange = computed(() =>
    formatDateRange(props.stats.startDate, props.stats.finishDate, locale.value)
);

const properties = computed<
    {
        messageCode: string;
        icon: string;
        value: string;
    }[]
>(() => [
    {
        icon: mdiCalendar,
        messageCode: 'components.stats_display.date',
        value: dateRange.value,
    },
    {
        icon: mdiTimerOutline,
        messageCode: 'components.stats_display.duration',
        value: getDuration(props.stats.startDate, props.stats.finishDate),
    },
    {
        icon: mdiTextureBox,
        messageCode: 'components.stats_display.area_total',
        value: `${props.stats.converedArea} m²`,
    },
]);
</script>

<template>
    <div>
        <span v-if="!statsPresent"> No Stats :/ </span>
        <div
            v-else
            class="flex flex-col items-stretch justify-start gap-2 relative"
        >
            <header class="w-full absolute z-50 flex flex-col items-center">
                <h2 class="rounded-md backdrop-blur-lg m-2 py-2 px-4">
                    {{ stats.name }}
                </h2>
            </header>
            <MapWithControls
                class="min-h-[30vh] [&>*]:rounded-lg"
                controls="none"
                center="area"
                :divisions="stats.divisionStats"
                locked
            />
            <Panel :header="$t('components.stats_display.title_general')">
                <div class="grid grid-cols-[max-content_1fr] gap-3">
                    <div
                        v-for="stat of properties"
                        :key="stat.messageCode"
                        class="grid grid-cols-subgrid col-span-2"
                    >
                        <span>
                            <MdiTextButtonIcon :icon="stat.icon" />
                            {{ $t(stat.messageCode) }}
                        </span>
                        <span>{{ stat.value }}</span>
                    </div>
                </div>
            </Panel>
        </div>
    </div>
</template>
