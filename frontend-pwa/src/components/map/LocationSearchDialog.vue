<script setup lang="ts">
import { ref } from 'vue';
import { useToast } from 'primevue/usetoast';
import { TOAST_LIFE } from '@/data/constants';
import { mdiArrowLeft, mdiChevronRight, mdiMagnify } from '@mdi/js';
import { isOnMobile } from '@/util/mobileDetection';
import ScrollPanel from 'primevue/scrollpanel';
import Button from 'primevue/button';
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import InputText from 'primevue/inputtext';
import InputGroup from 'primevue/inputgroup';
import Dialog from 'primevue/dialog';

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
            <TextButtonIcon :icon="mdiMagnify" />
        </template>
    </Button>
    <Dialog
        class="w-full max-w-[800px]"
        v-model:visible="searchDialogVisible"
        modal
        :closable="false"
        :draggable="false"
        :position="isOnMobile ? 'bottom' : 'top'"
        :dismissable-mask="true"
        :pt="{
            header: {
                class: 'flex justify-stretch gap-2',
            },
            content: { class: 'h-full overflow-hidden' },
            footer: { class: 'justify-center' },
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
                    <TextButtonIcon :icon="mdiArrowLeft" />
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
            <ScrollPanel
                class="h-[50vh] max-h-[50vh]"
                :pt="{
                    content: {
                        class: 'flex flex-col gap-2 items-center justify-start',
                    },
                }"
            >
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
        </template>
        <template v-if="isOnMobile" #footer>
            <Button
                class="w-full"
                label="Close"
                severity="secondary"
                @click="searchDialogVisible = false"
                text
            />
        </template>
    </Dialog>
</template>
