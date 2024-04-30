<script setup lang="ts">
import CodeReader from '@/components/camera/CodeReader.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import getJoinId from '@/util/getJoinId';
import validateJoinLink from '@/validation/validateJoinLink';
import { mdiArrowLeft, mdiArrowRight, mdiLink } from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const joinLink = ref('');
const joinId = computed(() => getJoinId(joinLink.value));
const submittable = computed(() => validateJoinLink(joinLink.value));

function processScan(scans: string[]) {
    const id = scans.map((s) => getJoinId(s)).find((s) => s != null);
    if (id == null) return;
    // TODO: check if recognized collection is present in backend
    router.push({ name: 'join', params: { id } });
}

function next() {
    if (!submittable.value) return;
    router.push({ name: 'join', params: { id: joinId.value } });
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
                label="Next"
                severity="primary"
                :disabled="!submittable"
                @click="next"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiArrowRight" />
                </template>
            </Button>
        </template>
        <template #default>
            <Card :pt="{ root: { class: 'overflow-hidden' } }">
                <template #header>
                    <div
                        class="w-full h-[40vh] bg-gray-500 bg-opacity-50 flex flex-col justify-center items-center select-none rounded-2xl overflow-hidden"
                    >
                        <CodeReader @scanned="processScan" />
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
                            />
                        </IconField>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
