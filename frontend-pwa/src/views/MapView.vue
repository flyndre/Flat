<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapHelp from '@/components/map/MapHelp.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { clientId } from '@/data/clientMetadata';
import { collectionDraft, collectionService } from '@/data/collections';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import { areaFromShapeList, divisionToShape } from '@/util/converters';
import { dbSafe } from '@/util/dbUtils';
import { mdiArrowLeft, mdiCheck, mdiHelp } from '@mdi/js';
import Button from 'primevue/button';
import { useToast } from 'primevue/usetoast';
import { v4 as uuidv4 } from 'uuid';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const props = withDefaults(
    defineProps<{
        edit?: boolean;
        id?: string;
    }>(),
    {
        edit: false,
        id: undefined,
    }
);

const defaultCollection: Collection = {
    id: uuidv4(),
    adminClientId: clientId.value,
};

const router = useRouter();
const collection = ref<Collection>({
    ...defaultCollection,
    ...collectionDraft.get(),
});
const loading = ref(false);

onMounted(async () => {
    if (props.edit) {
        try {
            const storedCollection = await collectionService.get(props.id);
            if (storedCollection === undefined) {
                add({
                    life: TOAST_LIFE,
                    severity: 'error',
                    summary: 'The requested collection does not exits.',
                });
                await router.replace({ name: 'presets' });
            }
            collection.value = storedCollection;
        } catch (error) {
            add({
                life: TOAST_LIFE,
                severity: 'error',
                summary: 'Failed to get the requested collection.',
            });
            await router.replace({ name: 'presets' });
        }
    }
});

const submittable = computed(() => collection.value.divisions?.length > 0);

const { add } = useToast();

async function save() {
    loading.value = true;
    collection.value.area = areaFromShapeList(
        collection.value.divisions.map((d) => divisionToShape(d))
    );
    try {
        if (props.edit) {
            await collectionService.put(dbSafe(collection.value));
        } else {
            collectionDraft.set(collection.value);
        }
        await router.push({
            name: props.edit ? 'edit' : 'create',
            params: { id: props.id },
        });
    } catch (error) {
        add({
            life: TOAST_LIFE,
            severity: 'error',
            summary: 'Failed to save divisions.',
        });
    } finally {
        loading.value = false;
    }
}

const helpVisible = ref(false);
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link
                :to="{
                    name: edit ? 'edit' : 'create',
                    params: { id },
                }"
            >
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> Edit Map </template>
        <template #action-right>
            <Button
                label="Save"
                severity="primary"
                @click="save"
                :disabled="!submittable"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiCheck" />
                </template>
            </Button>
            <Button
                label="Help"
                severity="secondary"
                text
                @click="helpVisible = true"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiHelp" />
                </template>
            </Button>
        </template>
        <template #default>
            <MapHelp v-model:visible="helpVisible" />
            <MapWithControls v-model:divisions="collection.divisions" />
        </template>
    </DefaultLayout>
</template>
