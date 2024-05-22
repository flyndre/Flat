<script setup lang="ts">
import { useSettings } from '@/plugins/SettingsPlugin';
import { mdiCogCounterclockwise } from '@mdi/js';
import Button from 'primevue/button';
import MdiTextButtonIcon from '../icons/MdiTextButtonIcon.vue';

const { reset } = useSettings();
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
</template>
