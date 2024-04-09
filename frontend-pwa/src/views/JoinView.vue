<script setup lang="ts">
import MdiIcon from "@/components/MdiIcon.vue";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import validateJoinLink from "@/validation/validateJoinLink";
import validateJoinName from "@/validation/validateJoinName";
import { mdiAccount, mdiArrowLeft, mdiImport, mdiLink, mdiLinkVariant } from "@mdi/js";
import Button from "primevue/button";
import Card from "primevue/card";
import InputText from "primevue/inputtext";
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import getJoinId from "@/util/getJoinId";
import IconField from "primevue/iconfield";
import InputIcon from "@/components/icons/InputIcon.vue";
import TextButtonIcon from "@/components/icons/TextButtonIcon.vue";

const props = defineProps<{
    id?: string,
}>();

const router = useRouter();
const joinLink = ref(props.id ? window.location.href : '');
const joinId = computed(() => getJoinId(joinLink.value));
const joinName = ref('');
const submittable = computed(() => validateJoinLink(joinLink.value) && validateJoinName(joinName.value));
function join() {

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
                            <InputText class="w-full" placeholder="Join as" v-model="joinName" />
                        </IconField>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
