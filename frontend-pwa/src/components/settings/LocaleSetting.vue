<script setup lang="ts">
import { useSettings } from '@/plugins/SettingsPlugin';
import SelectButton from 'primevue/selectbutton';
import { useI18n } from 'vue-i18n';

defineProps<{
    condensed?: boolean;
}>();

const { availableLocales } = useI18n();
const { settings } = useSettings();
</script>

<template>
    <SelectButton
        class="flex w-full flex-row"
        v-model="settings.locale"
        :options="availableLocales"
        :allow-empty="false"
        :pt="{ button: { class: 'w-full' } }"
    >
        <template #option="slotProps">
            <div
                class="flex flex-row justify-center items-center flex-nowrap w-full gap-3 min-h-6"
            >
                <span class="uppercase opacity-75 font-semibold">
                    {{ slotProps.option }}
                </span>
                <span
                    v-if="condensed != true"
                    class="max-[400px]:hidden text-ellipsis overflow-hidden z-10"
                >
                    <span>{{ $t(`locales.${slotProps.option}`) }}</span>
                </span>
            </div>
        </template>
    </SelectButton>
</template>
