<script setup lang="ts">
import { mdiArrowLeft, mdiCheck, mdiCloseBox, mdiCrosshairs, mdiCrosshairsGps, mdiDeleteForever, mdiDotsHorizontal, mdiFitToScreen, mdiMagnify, mdiMap, mdiPalette, mdiTableColumn, mdiTableColumnRemove, mdiTextureBox, mdiVectorSquareEdit } from '@mdi/js';
import Button from 'primevue/button';
import Sidebar from 'primevue/sidebar';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';

const visible = defineModel<boolean>('visible', {
    default: false,
});
</script>

<template>
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
                {{ $t('components.map_help.title') }}
            </template>
            <template #default>
                <p>
                    {{ $t('components.map_help.introduction.0') }}
                </p>
                <p>
                    {{ $t('components.map_help.introduction.1') }}
                </p>
                <p>
                    {{ $t('components.map_help.introduction.2') }}
                </p>
                <p>
                    <p class="font-bold border border-solid rounded-lg p-2 w-max mt-8">
                        <MdiIcon
                            class="overflow-visible ml-1 mr-1.5"
                            :icon="mdiCheck"
                        />
                        {{ $t('universal.save') }}
                    </p>
                    {{ $t('components.map_help.explanation_save') }}
                </p>
                <p>
                    <p class="font-bold border border-solid rounded-lg p-2 w-max mt-8">
                        <MdiIcon
                            class="overflow-visible ml-1 mr-1.5"
                            :icon="mdiMap"
                        />
                        {{ $t('components.map_with_controls.map') }}
                    </p>
                    {{ $t('components.map_help.explanation_map.introduction') }}
                    <ul class="list-none [&>*]:mb-2.5 [&>*]:relative [&>li>*]:absolute [&>li>*]:top-1 [&>li>*]:-left-8">
                        <li>
                            <MdiIcon
                                :icon="mdiCrosshairsGps"
                            />
                            {{ $t('components.map_help.explanation_map.locate_me') }}
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiFitToScreen"
                            />
                            {{ $t('components.map_help.explanation_map.locate_shapes') }}
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiMagnify"
                            />
                            {{ $t('components.map_help.explanation_map.search') }}
                        </li>
                        
                        <li>
                            <MdiIcon
                                class="rotate-90"
                                :icon="mdiTableColumn"
                            />
                            {{ $t('components.map_help.explanation_map.map_type') }}
                        </li>
                    </ul>
                </p>

                <p>
                    <p class="font-bold border border-solid rounded-lg p-2 w-max mt-8">
                        <MdiIcon
                            class="overflow-visible ml-1 mr-1.5"
                            :icon="mdiPalette"
                        />
                        {{ $t('components.map_with_controls.tools') }}
                    </p>
                    {{ $t('components.map_help.explanation_tools.introduction') }}
                    <ul class="list-none [&>*]:mb-2.5 [&>*]:relative [&>li>*]:absolute [&>li>*]:top-1 [&>li>*]:-left-8">
                        <li>
                            <MdiIcon
                                :icon="mdiVectorSquareEdit"
                            />
                            {{ $t('components.map_help.explanation_tools.draw_division') }}
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiDeleteForever"
                            />
                            {{ $t('components.map_help.explanation_tools.delete_selected') }}
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiDotsHorizontal"
                            />
                            {{ $t('components.map_help.explanation_tools.select_color') }}
                        </li>
                    </ul>
                </p>

                <p>
                    <p class="font-bold border border-solid rounded-lg p-2 w-max mt-8">
                        <MdiIcon
                            class="overflow-visible ml-1 mr-1.5"
                            :icon="mdiTextureBox"
                        />
                        {{ $t('components.map_with_controls.divisions') }}
                    </p>
                    {{ $t('components.map_help.explanation_divisions.introduction') }}
                    <ul class="list-none [&>*]:mb-2.5 [&>*]:relative [&>li>*]:absolute [&>li>*]:top-1 [&>li>*]:-left-8">
                        <li>
                            <MdiIcon
                                :icon="mdiCrosshairs"
                            />
                            {{ $t('components.map_help.explanation_divisions.focus_division') }}
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiTextureBox"
                            />
                            {{ $t('components.map_help.explanation_divisions.division_name') }}
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiDeleteForever"
                            />
                            {{ $t('components.map_help.explanation_divisions.delete_division') }}
                        </li>
                    </ul>
                    {{ $t('components.map_help.explanation_divisions.delete_introduction') }}
                    <ul class="list-none [&>*]:mb-2.5 [&>*]:relative [&>li>*]:absolute [&>li>*]:top-1 [&>li>*]:-left-8">
                        <li>
                            <MdiIcon
                                :icon="mdiCloseBox"
                            />
                            {{ $t('components.map_help.explanation_divisions.delete_all') }}
                        </li>
                    </ul>
                </p>
            </template>
        </DefaultLayout>
    </Sidebar>
</template>
