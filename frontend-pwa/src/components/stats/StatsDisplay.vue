<script setup lang="ts">
import { CollectionStats } from '@/types/stats/CollectionStats';
import {
    formatDateRange,
    getDateTime,
    getDuration,
} from '@/util/datetimeUtils';
import { getParticipantColor } from '@/util/trackingUtils';
import {
    mdiAccountCircle,
    mdiCalendar,
    mdiTextureBox,
    mdiTimerOutline,
} from '@mdi/js';
import Panel from 'primevue/panel';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import MdiTextButtonIcon from '../icons/MdiTextButtonIcon.vue';
import MapWithControls from '../map/MapWithControls.vue';
import { NUMBER_FORMAT_OPTIONS } from '@/data/constants';

const props = defineProps<{
    stats?: CollectionStats;
}>();
const statsPresent = computed(() => props.stats != null);

const { locale } = useI18n();
const dateRange = computed(() =>
    props.stats.finishDate == null
        ? '-'
        : props.stats.startDate == null
          ? getDateTime(props.stats.finishDate, locale.value)
          : formatDateRange(
                props.stats.startDate,
                props.stats.finishDate,
                locale.value
            )
);
const duration = computed(() =>
    props.stats.startDate == null || props.stats.finishDate == null
        ? '-'
        : getDuration(props.stats.startDate, props.stats.finishDate)
);

const generalStats = computed<
    {
        icon: string;
        messageCode: string;
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
        value: duration.value,
    },
    {
        icon: mdiTextureBox,
        messageCode: 'components.stats_display.area_total',
        value: `ca. ${props.stats.converedArea?.toLocaleString(locale.value, NUMBER_FORMAT_OPTIONS)} km²`,
    },
]);

const divisionStats = computed<
    {
        id: string;
        icon: string;
        label: string;
        color: string;
        value: string;
    }[]
>(() =>
    props.stats.divisionStats.map((d) => ({
        id: d.id,
        icon: mdiTextureBox,
        label: d.name,
        color: d.color,
        value: `ca. ${d.coveredArea?.toLocaleString(locale.value, NUMBER_FORMAT_OPTIONS)} km²`,
    }))
);

const participantStats = computed<
    {
        id: string;
        icon: string;
        label: string;
        color: string;
        value: string;
    }[]
>(() =>
    props.stats.participantStats.map((p) => ({
        id: p.id,
        icon: mdiAccountCircle,
        label: p.name,
        color: p.color ?? getParticipantColor(p.id, props.stats.divisionStats),
        value: `ca. ${p.coveredDistance?.toLocaleString(locale.value, NUMBER_FORMAT_OPTIONS)} km`,
    }))
);
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
                :tracks="stats.participantStats"
                locked
            />
            <Panel :header="$t('components.stats_display.title_general')">
                <div
                    class="grid grid-cols-[minmax(20%,_max-content)_minmax(50%,_1fr)] gap-3"
                >
                    <div
                        v-for="stat of generalStats"
                        :key="stat.messageCode"
                        class="grid grid-cols-subgrid col-span-2"
                    >
                        <span
                            class="text-ellipsis overflow-hidden whitespace-nowrap"
                        >
                            <MdiTextButtonIcon class="ml-1" :icon="stat.icon" />
                            {{ $t(stat.messageCode) }}
                        </span>
                        <span>{{ stat.value }}</span>
                    </div>
                </div>
            </Panel>
            <Panel :header="$t('components.stats_display.title_divisions')">
                <div
                    class="grid grid-cols-[minmax(20%,_max-content)_minmax(50%,_1fr)] gap-3"
                >
                    <div
                        v-for="stat of divisionStats"
                        :key="stat.label"
                        class="grid grid-cols-subgrid col-span-2"
                    >
                        <span
                            class="text-ellipsis overflow-hidden whitespace-nowrap"
                        >
                            <MdiTextButtonIcon
                                class="ml-1"
                                :style="{ color: stat.color }"
                                :icon="stat.icon"
                            />
                            {{ stat.label }}
                        </span>
                        <span>{{ stat.value }}</span>
                    </div>
                </div>
            </Panel>
            <Panel :header="$t('components.stats_display.title_participants')">
                <div
                    class="grid grid-cols-[minmax(20%,_max-content)_minmax(50%,_1fr)] gap-3"
                >
                    <div
                        v-for="stat of participantStats"
                        :key="stat.label"
                        class="grid grid-cols-subgrid col-span-2"
                    >
                        <span
                            class="text-ellipsis overflow-hidden whitespace-nowrap"
                        >
                            <MdiTextButtonIcon
                                class="ml-1"
                                :style="{ color: stat.color }"
                                :icon="stat.icon"
                            />
                            {{ stat.label }}
                        </span>
                        <span>{{ stat.value }}</span>
                    </div>
                </div>
            </Panel>
        </div>
    </div>
</template>
