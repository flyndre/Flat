<script setup lang="ts">
import { ref } from 'vue';
import { useToast } from 'primevue/usetoast';
import { TOAST_LIFE } from '@/data/constants';
import { mdiArrowLeft, mdiChevronRight, mdiMagnify } from '@mdi/js';
import { isOnMobile } from '@/util/mobileDetection';
import ScrollPanel from 'primevue/scrollpanel';
import Button from 'primevue/button';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import InputText from 'primevue/inputtext';
import InputGroup from 'primevue/inputgroup';
import Sidebar from 'primevue/sidebar';

const props = defineProps<{
    placesService: google.maps.places.PlacesService;
    selectResultCallback: (result: google.maps.places.PlaceResult) => void;
}>();

const { add } = useToast();
const searchDialogVisible = ref(false);
const searchTerm = ref('');
const searchResults = ref<google.maps.places.PlaceResult[]>([]);
const searchLoading = ref(false);
function submitSearch() {
    searchLoading.value = true;
    try {
        props.placesService.textSearch(
            {
                query: searchTerm.value,
            },
            (results, status, _pagination) => {
                if (status !== google.maps.places.PlacesServiceStatus.OK) {
                    add({
                        closable: true,
                        life: TOAST_LIFE,
                        severity: 'error',
                        summary: 'Search Failed',
                        detail: `The Maps API responded with code ${status}.`,
                    });
                    return;
                }
                searchResults.value.length = 0;
                searchResults.value.push(...results);
                searchLoading.value = false;
            }
        );
    } catch (e) {
        searchLoading.value = false;
    }
}
function selectResult(result: google.maps.places.PlaceResult) {
    searchDialogVisible.value = false;
    props.selectResultCallback(result);
}
</script>

<template>
    <Button
        class="grow"
        label="Search"
        severity="secondary"
        @click="() => (searchDialogVisible = true)"
        outlined
        :pt="{ label: { class: 'text-left' } }"
    >
        <template #icon>
            <MdiTextButtonIcon :icon="mdiMagnify" />
        </template>
    </Button>
    <Sidebar
        class="w-full max-w-[787px] h-fit rounded-t-xl -bottom-px"
        v-model:visible="searchDialogVisible"
        modal
        position="bottom"
        :block-scroll="true"
        :show-close-icon="false"
        :pt="{
            header: {
                class: 'flex flex-row justify-stretch gap-2',
            },
            content: {
                class: 'h-full flex flex-col justify-stretch items-stretch',
            },
        }"
    >
        <template #header>
            <Button
                v-if="!isOnMobile"
                label="Back"
                severity="secondary"
                @click="searchDialogVisible = false"
                text
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiArrowLeft" />
                </template>
            </Button>
            <InputGroup>
                <InputText
                    type="search"
                    class="grow"
                    v-model="searchTerm"
                    placeholder="Look for a place"
                    @keydown.enter="submitSearch"
                    autofocus
                    :disabled="searchLoading"
                />
                <Button @click="submitSearch" :loading="searchLoading">
                    <template #icon>
                        <MdiIcon :icon="mdiMagnify" />
                    </template>
                </Button>
            </InputGroup>
        </template>
        <template #default>
            <ScrollPanel class="h-[65vh]">
                <Button
                    v-for="result of searchResults"
                    class="w-full shrink-0 text-left"
                    severity="contrast"
                    @click="selectResult(result)"
                    text
                >
                    <template #default>
                        <div
                            class="w-full flex flex-row justify-between items-center gap-2"
                        >
                            <span>
                                {{ result.name }}
                            </span>
                            <MdiIcon :icon="mdiChevronRight" />
                        </div>
                    </template>
                </Button>
            </ScrollPanel>
            <Button
                v-if="isOnMobile"
                class="w-full sticky bottom-0 left-0 right-0 z-10 shrink-0 mt-5"
                label="Close"
                severity="secondary"
                @click="searchDialogVisible = false"
                text
            />
        </template>
    </Sidebar>
</template>
