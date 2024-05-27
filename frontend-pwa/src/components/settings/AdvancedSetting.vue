<script setup lang="ts">
import { useSettings } from '@/plugins/SettingsPlugin';
import { mdiCogCounterclockwise } from '@mdi/js';
import Button from 'primevue/button';
import MdiTextButtonIcon from '../icons/MdiTextButtonIcon.vue';

const { reset } = useSettings();
const buildDate = import.meta.env.BUILD_DATE;
</script>

<template>
    <Button
        :label="$t('components.advanced_setting.reset_settings')"
        severity="secondary"
        @click="reset"
    >
        <template #icon>
            <MdiTextButtonIcon :icon="mdiCogCounterclockwise" />
        </template>
    </Button>
    <span class="opacity-50">
        {{ buildDate }}
    </span>
</template>
