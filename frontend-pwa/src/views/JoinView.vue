<script setup lang="ts">
import CardProgressIndicator from "@/components/card/CardProgressIndicator.vue";
import InputIcon from "@/components/icons/InputIcon.vue";
import TextButtonIcon from "@/components/icons/TextButtonIcon.vue";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import getJoinId from "@/util/getJoinId";
import { isOnMobile } from "@/util/mobileDetection";
import validateJoinLink from "@/validation/validateJoinLink";
import validateJoinName from "@/validation/validateJoinName";
import { mdiAccount, mdiArrowLeft, mdiClose, mdiImport, mdiLink } from "@mdi/js";
import Button from "primevue/button";
import Card from "primevue/card";
import Dialog from "primevue/dialog";
import IconField from "primevue/iconfield";
import InputText from "primevue/inputtext";
import { computed, ref } from "vue";
import { useRouter } from "vue-router";

const props = defineProps<{
    id?: string,
}>();

const router = useRouter();
const joinLink = ref(props.id ? window.location.href : '');
const joinId = computed(() => getJoinId(joinLink.value));
const joinName = ref('');
const submittable = computed(() => validateJoinLink(joinLink.value) && validateJoinName(joinName.value));
const dialogVisible = ref(false);
function join() {
    dialogVisible.value = true;
    // todo: send request
}
function cancel() {
    dialogVisible.value = false;
    // todo: cancel request or send cancelation request
}
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'home' }">
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <TextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> Join a Collection </template>
        <template #action-right>
            <Button label="Join" severity="primary" :disabled="!submittable" @click="join">
                <template #icon>
                    <TextButtonIcon :icon="mdiImport" />
                </template>
            </Button>
        </template>
        <template #default>
            <Dialog v-model:visible="dialogVisible" :closable="false" :draggable="false" modal
                :position="isOnMobile ? 'bottom' : 'top'" class="overflow-hidden" header="Waiting to join...">
                <CardProgressIndicator mode="indeterminate" />
                Please stand by as the collection's admin reviews your join request.
                <template #footer>
                    <div class="w-full flex flex-row justify-center">
                        <Button label="Cancel request" severity="danger" text @click="cancel">
                            <template #icon>
                                <TextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                    </div>
                </template>
            </Dialog>
            <Card :pt="{ root: { class: 'overflow-hidden' } }">
                <template #header>
                    <div
                        class="w-full flex-grow bg-gray-950 h-96 flex flex-col items-center justify-center text-gray-500">
                        Camera Placeholder
                    </div>
                </template>
                <template #content>
                    <div class="flex flex-col gap-2.5">
                        <IconField iconPosition="left">
                            <InputIcon :icon="mdiLink" />
                            <InputText class="w-full" placeholder="Or enter a link manually" v-model="joinLink"
                                :disabled="id !== undefined" />
                        </IconField>
                        <IconField iconPosition="left">
                            <InputIcon :icon="mdiAccount" />
                            <InputText class="w-full" placeholder="Your Name" v-model="joinName" />
                        </IconField>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
