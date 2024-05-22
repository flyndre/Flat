<script setup lang="ts">
import { CollectionStats } from '@/types/stats/CollectionStats';
import { computed } from 'vue';

const props = defineProps<{
    stats?: CollectionStats;
}>();
const statsPresent = computed(() => props.stats != null);
</script>

<template>
    <div>
        <span v-if="!statsPresent"> No Stats :/ </span>
        <div v-else>
            <!-- Date started -->
            <!-- Date stopped -->
            {{ JSON.stringify(stats, null, 4) }}
        </div>
    </div>
</template>
