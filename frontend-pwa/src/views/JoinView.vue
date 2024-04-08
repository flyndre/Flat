<script setup lang="ts">
import MdiIcon from "@/components/MdiIcon.vue";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import validateJoinLink from "@/validation/validateJoinLink";
import validateJoinName from "@/validation/validateJoinName";
import { mdiArrowLeft, mdiImport } from "@mdi/js";
import Button from "primevue/button";
import Card from "primevue/card";
import InputText from "primevue/inputtext";
import { computed, ref } from "vue";

const joinName = ref("");
const joinLink = ref("");
const submittable = computed(() => validateJoinLink(joinLink.value) && validateJoinName(joinName.value));
</script>

<template>
    <DefaultLayout>
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
            <Button label="Join" severity="primary" :disabled="!submittable">
                <template #icon>
                    <MdiIcon class="mr-2.5" :icon="mdiImport" />
                </template>
            </Button>
        </template>
        <template #default>
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
                            v-model="joinLink"
                        />
                        <InputText
                            placeholder="Join as (name)"
                            v-model="joinName"
                        />
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
