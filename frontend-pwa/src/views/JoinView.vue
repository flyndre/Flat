<script setup lang="ts">
import CardProgressIndicator from '@/components/card/CardProgressIndicator.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import getJoinId from '@/util/getJoinId';
import { isOnMobile } from '@/util/mobileDetection';
import validateJoinLink from '@/validation/validateJoinLink';
import validateJoinName from '@/validation/validateJoinName';
import {
    mdiAccount,
    mdiArrowLeft,
    mdiClose,
    mdiImport,
    mdiLink,
} from '@mdi/js';
import { useTimeoutFn } from '@vueuse/core';
import Button from 'primevue/button';
import Card from 'primevue/card';
import Dialog from 'primevue/dialog';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps<{
    id?: string;
}>();

const router = useRouter();
const joinLink = ref(props.id ? window.location.href : '');
const joinId = computed(() => getJoinId(joinLink.value));
const joinName = ref('');
const submittable = computed(
    () => validateJoinLink(joinLink.value) && validateJoinName(joinName.value)
);
const dialogVisible = ref(false);
const { start, stop } = useTimeoutFn(
    () => {
        // TODO: create WS connection etc.
        router.push({ name: 'track' });
    },
    3000,
    {
        immediate: false,
    }
);
function join() {
    dialogVisible.value = true;
    start();
    // TODO: send request
}
function cancel() {
    dialogVisible.value = false;
    stop();
    // TODO: cancel request or send cancelation request
}
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'home' }">
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> Join a Collection </template>
        <template #action-right>
            <Button
                label="Join"
                severity="primary"
                :disabled="!submittable"
                @click="join"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiImport" />
                </template>
            </Button>
        </template>
        <template #default>
            <Dialog
                v-model:visible="dialogVisible"
                :closable="false"
                :draggable="false"
                modal
                :position="isOnMobile ? 'bottom' : 'top'"
                class="overflow-hidden"
                header="Waiting to join..."
            >
                <CardProgressIndicator mode="indeterminate" />
                Please stand by as the collection's admin reviews your join
                request.
                <template #footer>
                    <div class="w-full flex flex-row justify-center">
                        <Button
                            label="Cancel request"
                            severity="danger"
                            text
                            @click="cancel"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                    </div>
                </template>
            </Dialog>
            <Card :pt="{ root: { class: 'overflow-hidden' } }">
                <template #header>
                    <div
                        class="w-full flex-grow bg-gray-500 bg-opacity-50 h-96 flex flex-col items-center justify-center text-gray-500 select-none"
                    >
                        Camera Placeholder
                    </div>
                </template>
                <template #content>
                    <div class="flex flex-col gap-2.5">
                        <IconField iconPosition="left">
                            <MdiInputIcon :icon="mdiLink" />
                            <InputText
                                class="w-full"
                                placeholder="Or enter a link manually"
                                v-model="joinLink"
                                :disabled="id !== undefined"
                            />
                        </IconField>
                        <IconField iconPosition="left">
                            <MdiInputIcon :icon="mdiAccount" />
                            <InputText
                                class="w-full"
                                placeholder="Your Name"
                                v-model="joinName"
                            />
                        </IconField>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
