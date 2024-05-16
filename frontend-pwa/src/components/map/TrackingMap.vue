<script setup lang="ts">
import { getShapeListBounds } from '@/util/googleMapsUtils';
import LocateMeButton from './controls/LocateMeButton.vue';
import LocateShapesButton from './controls/LocateShapesButton.vue';
import CustomMap from './CustomMap.vue';
import { computed, onMounted, ref } from 'vue';
import { collectionDB } from '@/data/collections';
import { useTrackingService } from '@/service/trackingService';

const collection = ref(undefined);
onMounted(async () => {
    try {
        const storedCollection = await collectionDB.get(
            '4fd5473d-0be7-4d7c-b923-546c32e0a09c'
        );
        collection.value = storedCollection;
    } catch (error) {
        console.log('propbel');
    }
});

const { coords } = useTrackingService();
const clientPos = computed(() => ({
    lat: coords.value.latitude,
    lng: coords.value.longitude,
}));
</script>

<template>
    <CustomMap
        controls="drawing"
        :divisions="collection?.divisions ?? []"
        :client-pos
    >
        <template #default="slotProps">
            <div class="flex flex-row gap-2">
                <LocateMeButton
                    :client-pos
                    :locate-me-handler="
                        (r) => {
                            slotProps.panMapToPos(r);
                        }
                    "
                />
                {{ slotProps.divisions }}
            </div>
        </template>
    </CustomMap>
</template>
