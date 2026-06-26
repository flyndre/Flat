<script setup lang="ts">
import { query } from '@/api/locationSearch';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { mdiArrowLeft, mdiChevronRight, mdiMagnify } from '@mdi/js';
import { LngLatBoundsLike } from 'maplibre-gl';
import Button from 'primevue/button';
import InputGroup from 'primevue/inputgroup';
import InputText from 'primevue/inputtext';
import Sidebar from 'primevue/sidebar';
import { useToast } from 'primevue/usetoast';
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

type Result = { title: string, subtitle?: string, bounds: LngLatBoundsLike };

const { add } = useToast();
const { t } = useI18n();

const emit = defineEmits<{
    select: [LngLatBoundsLike]
}>()

const visible = ref(false);
const searchTerm = ref('');
const results = ref<Result[]>([]);
const searchLoading = ref(false);

async function submitSearch() {
    searchLoading.value = true;
    try {
        const { features } = await query(searchTerm.value);
        for (const feature of features) {
            if (feature.bbox) {
                results.value.push({
                    title: feature.properties?.name,
                    subtitle: feature.properties?.display_name,
                    bounds: feature.bbox as LngLatBoundsLike,
                });
            }
        }
    } catch (error) {
        add({
            closable: true,
            life: TOAST_LIFE,
            severity: 'error',
            summary: t(
                'components.location_search_dialog.search_failed'
            ),
            detail: t(
                'components.location_search_dialog.search_failed_text',
                { code: error }
            ),
        });
    } finally {
        searchLoading.value = false;
    }
}
function selectResult(result: Result) {
    visible.value = false;
    results.value.length = 0;
    emit('select', result.bounds);
}
</script>

<template>
    <Button
        class="grow"
        :label="$t('components.location_search_dialog.action_search')"
        severity="secondary"
        @click="() => (visible = true)"
        outlined
        :pt="{ label: { class: 'text-left' } }"
    >
        <template #icon>
            <MdiTextButtonIcon :icon="mdiMagnify" />
        </template>
    </Button>
    <Sidebar
        class="w-full max-w-[787px] h-fit rounded-t-xl -bottom-px p-0 overflow-hidden"
        v-model:visible="visible"
        modal
        position="bottom"
        :block-scroll="true"
        :show-close-icon="false"
        :pt="{
            header: {
                class: 'hidden',
            },
            content: {
                class: 'h-full flex flex-col justify-stretch items-stretch p-0',
            },
        }"
    >
        <DefaultLayout height="80vh">
            <template #action-left>
                <Button
                    :label="$t('universal.back')"
                    severity="secondary"
                    @click="visible = false"
                    text
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </template>
            <template #title>
                <InputGroup>
                    <InputText
                        type="search"
                        class="grow"
                        v-model.trim="searchTerm"
                        :placeholder="
                            $t('components.location_search_dialog.find_place')
                        "
                        @keydown.enter="submitSearch"
                        autofocus
                        :disabled="searchLoading"
                    />
                    <Button :loading="searchLoading" @click="submitSearch">
                        <template #icon>
                            <MdiIcon :icon="mdiMagnify" />
                        </template>
                    </Button>
                </InputGroup>
            </template>
            <template #default>
                <Button
                    v-for="result of results"
                    class="w-full shrink-0 text-left"
                    severity="contrast"
                    @click="selectResult(result)"
                    text
                >
                    <template #default>
                        <div
                            class="w-full flex flex-row justify-between items-center gap-2"
                        >
                            <div class="flex flex-col">
                                <span class="font-bold">
                                    {{ result.title }}
                                </span>
                                <span v-if="result.subtitle" class="opacity-60">
                                    {{ result.subtitle }}
                                </span>
                            </div>
                            <MdiIcon :icon="mdiChevronRight" />
                        </div>
                    </template>
                </Button>
            </template>
        </DefaultLayout>
    </Sidebar>
</template>
