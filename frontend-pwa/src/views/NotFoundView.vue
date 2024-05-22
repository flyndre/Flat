<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { mdiArrowLeft, mdiHome } from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import { useRouter } from 'vue-router';
const router = useRouter();
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <Button
                :label="$t('universal.back')"
                severity="secondary"
                text
                @click="router.back"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiArrowLeft" />
                </template>
            </Button>
        </template>
        <template #title> {{ $t('not_found.title') }} </template>
        <template #action-right>
            <router-link :to="{ name: 'home' }">
                <Button :label="$t('not_found.go_home')">
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiHome" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #default>
            <Card>
                <template #title> 404 </template>
                <template #subtitle>{{ $t('not_found.error_text') }}</template>
            </Card>
        </template>
    </DefaultLayout>
</template>
