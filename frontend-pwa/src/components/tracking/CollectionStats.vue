<script setup lang="ts">
import { computed } from 'vue';
import { calculateCollectionStats } from '@/util/statsUtils';
import { ActiveCollection } from '@/types/ActiveCollection';

const props = defineProps<{
    collection?: ActiveCollection;
}>();
const statsPresent = computed(() => props.collection != null);
const stats = computed(() =>
    statsPresent.value ? null : calculateCollectionStats(props.collection)
);
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
