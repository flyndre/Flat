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
            <Button label="Back" severity="secondary" text @click="router.back">
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiArrowLeft" />
                </template>
            </Button>
        </template>
        <template #title> Error </template>
        <template #action-right>
            <router-link :to="{ name: 'home' }">
                <Button label="Go Home">
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiHome" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #default>
            <Card>
                <template #title> 404 </template>
                <template #subtitle>Page not found</template>
            </Card>
        </template>
    </DefaultLayout>
</template>
