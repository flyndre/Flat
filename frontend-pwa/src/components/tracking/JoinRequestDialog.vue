<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { JoinRequest } from '@/types/JoinRequest';
import { mdiAccountQuestion, mdiCheck, mdiClose } from '@mdi/js';
import Button from 'primevue/button';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import Sidebar from 'primevue/sidebar';
import { computed } from 'vue';

const props = withDefaults(
    defineProps<{
        requests?: JoinRequest[];
    }>(),
    {
        requests: () => [],
    }
);
const emit = defineEmits<{
    requestAnswered: [JoinRequest];
}>();
const visible = computed(() => props.requests?.length > 0);

function accept(request: JoinRequest) {
    request.accepted = true;
    emit('requestAnswered', request);
}

function decline(request: JoinRequest) {
    request.accepted = false;
    emit('requestAnswered', request);
}
</script>

<template>
    <Sidebar
        class="w-full max-w-[787px] h-fit rounded-t-xl -bottom-px p-0 overflow-hidden"
        v-model:visible="visible"
        modal
        position="bottom"
        :dismissable="false"
        :block-scroll="true"
        :show-close-icon="false"
        :pt="{
            header: {
                class: 'hidden',
            },
            content: {
                class: 'h-full flex flex-col justify-stretch items-stretch p-0',
            },
        }"
    >
        <DefaultLayout height="80vh">
            <template #title>Join Requests</template>
            <template #default>
                <div class="flex flex-col gap-2 items-stretch justify-start">
                    <InputGroup
                        v-for="request of requests"
                        :key="request.clientId"
                    >
                        <InputGroupAddon
                            class="grow justify-start items-center overflow-hidden text-ellipsis"
                        >
                            <MdiIcon
                                class="ml-2 mr-3"
                                :icon="mdiAccountQuestion"
                            />
                            {{ request.username }}
                        </InputGroupAddon>
                        <Button
                            class="shrink-0 w-16"
                            @click="decline(request)"
                            severity="secondary"
                        >
                            <template #icon>
                                <MdiIcon :icon="mdiClose" />
                            </template>
                        </Button>
                        <Button class="shrink-0 w-16" @click="accept(request)">
                            <template #icon>
                                <MdiIcon :icon="mdiCheck" />
                            </template>
                        </Button>
                    </InputGroup>
                </div>
            </template>
        </DefaultLayout>
    </Sidebar>
</template>
