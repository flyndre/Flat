<script setup lang="ts">
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { clientId } from '@/data/clientMetadata';
import { collectionDraft, collectionService } from '@/data/collections';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import { mdiArrowLeft, mdiCheck } from '@mdi/js';
import Button from 'primevue/button';
import { v4 as uuidv4 } from 'uuid';
import { onMounted, ref } from 'vue';
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
                // todo: show toast
                await router.replace({ name: 'presets' });
            }
            collection.value = storedCollection;
        } catch (error) {
            // todo: show toast
            await router.replace({ name: 'presets' });
        }
    }
});

async function save() {
    loading.value = true;
    try {
        if (props.edit) {
            // await collectionService.put({ ...collection.value });
        } else {
            collectionDraft.set(collection.value);
        }
        await router.push({
            name: props.edit ? 'edit' : 'create',
            params: { id: props.id },
        });
    } catch (error) {
        // todo: show toast
    } finally {
        loading.value = false;
    }
}
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
                        <TextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> Edit Map </template>
        <template #action-right>
            <Button label="Save" severity="primary" @click="save">
                <template #icon>
                    <TextButtonIcon :icon="mdiCheck" />
                </template>
            </Button>
        </template>
        <template #default>
            <MapWithControls
                class="pb-2"
                v-model:areas="collection.divisions"
            />
        </template>
    </DefaultLayout>
    <!-- <div v-html="shapeHtml"></div>
    <button @click="addShape">Add Shape</button> -->
</template>
