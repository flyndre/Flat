<script setup lang="ts">
import { CollectionStats } from '@/types/stats/CollectionStats';
import Panel from 'primevue/panel';
import StatsListEntry from './StatsListEntry.vue';

const props = defineProps<{
    stats?: CollectionStats[];
}>();
</script>

<template>
    <Panel
        :header="$t('components.stats_list.header')"
        :pt="{
            root: { class: 'border-none' },
            header: { class: 'px-0' },
            content: { class: 'flex flex-col gap-1.5 p-0' },
        }"
    >
        <StatsListEntry v-for="stat of stats" :stats="stat" />
    </Panel>
</template>
