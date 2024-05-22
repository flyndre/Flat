<script setup lang="ts">
import { accessRequest } from '@/api/rest';
import CardProgressIndicator from '@/components/card/CardProgressIndicator.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { clientId } from '@/data/clientMetadata';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { isOnMobile } from '@/util/mobileDetection';
import validateJoinName from '@/validation/validateJoinName';
import {
    mdiAccount,
    mdiArrowLeft,
    mdiClose,
    mdiIdentifier,
    mdiImport,
} from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import Dialog from 'primevue/dialog';
import IconField from 'primevue/iconfield';
import Image from 'primevue/image';
import InputText from 'primevue/inputtext';
import { useToast } from 'primevue/usetoast';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import bannerSrc from '@/assets/images/branding-white.webp?url';

const props = defineProps<{
    id: string;
}>();
const { add } = useToast();
const router = useRouter();
const { t } = useI18n();

const joinName = ref('');
const submittable = computed(() => validateJoinName(joinName.value));
const dialogVisible = ref(false);

async function join() {
    dialogVisible.value = true;
    const response = await accessRequest(
        joinName.value,
        clientId.value,
        props.id
    );
    if (response.status == 200 && response.data['accepted'] == true) {
        router.push({ name: 'track', params: { id: props.id } });
    } else {
        const messageCode =
            response.status == 200
                ? 'join.error_rejected'
                : 'join.error_failed';
        add({
            closable: true,
            life: TOAST_LIFE,
            severity: 'error',
            summary: t(messageCode),
        });
        dialogVisible.value = false;
    }
}

function cancel() {
    dialogVisible.value = false;
}
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'scan' }">
                <Button :label="$t('universal.back')" severity="secondary" text>
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> {{ $t('join.title') }} </template>
        <template #action-right>
            <Button
                :label="$t('universal.join')"
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
                :header="$t('join.waiting_title')"
            >
                <CardProgressIndicator mode="indeterminate" />
                {{ $t('join.waiting_text') }}
                <template #footer>
                    <div class="w-full flex flex-row justify-center">
                        <Button
                            :label="$t('join.cancel_waiting')"
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
                    <img
                        class="w-full h-[40vh] object-contain bg-flyndre"
                        :src="bannerSrc"
                    />
                </template>
                <template #content>
                    <div class="flex flex-col gap-2.5">
                        <IconField iconPosition="left">
                            <MdiInputIcon :icon="mdiIdentifier" />
                            <InputText
                                class="w-full"
                                :placeholder="$t('join.enter_link')"
                                :value="id"
                                :disabled="id !== undefined"
                            />
                        </IconField>
                        <IconField iconPosition="left">
                            <MdiInputIcon :icon="mdiAccount" />
                            <InputText
                                class="w-full"
                                :placeholder="$t('join.nickname')"
                                v-model="joinName"
                            />
                        </IconField>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
