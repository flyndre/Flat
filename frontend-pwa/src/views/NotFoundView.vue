<script setup lang="ts">
import MdiIcon from "@/components/MdiIcon.vue";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import { mdiArrowLeft, mdiHome } from "@mdi/js";
import Button from "primevue/button";
import Card from "primevue/card";
import { useRouter } from "vue-router";
const router = useRouter();
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <Button label="Back" severity="secondary" text @click="router.back">
                <template #icon>
                    <MdiIcon class="mr-2.5" :icon="mdiArrowLeft" />
                </template>
            </Button>
        </template>
        <template #title> Error </template>
        <template #action-right>
            <router-link :to="{ name: 'home' }">
                <Button label="Go Home">
                    <template #icon>
                        <MdiIcon class="mr-2.5" :icon="mdiHome" />
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
