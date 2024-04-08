<script setup lang="ts">
import AppBar from "@/components/AppBar.vue";
import MdiIcon from "@/components/MdiIcon.vue";
import Button from "primevue/button";
import Card from "primevue/card";
import InputText from "primevue/inputtext";
import MainWrapper from "@/components/MainWrapper.vue";
import { mdiArrowLeft, mdiImport } from "@mdi/js";
import { ref } from "vue";
const username = ref("");
const link = ref("");
</script>

<template>
    <div>
        <AppBar>
            <template #action-left>
                <router-link :to="{ name: 'home' }">
                    <Button label="Back" severity="secondary" text>
                        <template #icon>
                            <MdiIcon class="mr-2.5" :icon="mdiArrowLeft" />
                        </template>
                    </Button>
                </router-link>
            </template>
            <template #title> Join a Collection</template>
            <template #action-right>
                <router-link :to="{ name: 'join' }">
                    <Button label="Join" severity="primary" disabled>
                        <template #icon>
                            <MdiIcon class="mr-2.5" :icon="mdiImport" />
                        </template>
                    </Button>
                </router-link>
            </template>
        </AppBar>
        <MainWrapper>
            <Card :pt="{ root: { class: 'overflow-hidden' } }">
                <template #header>
                    <div
                        class="w-full flex-grow bg-gray-950 h-96 flex flex-col items-center justify-center text-gray-500"
                    >
                        Camera Placeholder
                    </div>
                </template>
                <template #content>
                    <div class="flex flex-col gap-2.5">
                        <InputText
                            placeholder="Or enter a link manually"
                            v-model="link"
                        />
                        <InputText
                            placeholder="Join as (name)"
                            v-model="username"
                        />
                    </div>
                </template>
            </Card>
        </MainWrapper>
    </div>
</template>
