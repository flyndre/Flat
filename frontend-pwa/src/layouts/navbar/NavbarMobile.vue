<script setup lang="ts">
import Button from "primevue/button";
import Card from "primevue/card";
import type { NavbarLinks } from "@/types/props/Navbar";
defineProps<{
    links: NavbarLinks;
}>();
</script>

<template>
    <Card
        class="fixed bottom-0 left-0 right-0 rounded-none"
        :pt="{ body: { class: 'p-2' } }"
    >
        <template #content>
            <div
                class="w-full flex flex-row justify-center items-stretch gap-2"
            >
                <router-link :to="links.primary.to" class="contents">
                    <Button
                        text
                        :icon="links.secondary1.icon"
                        class="flex-grow basis-0"
                        icon-class="text-xl"
                    />
                </router-link>
                <router-link :to="links.secondary1.to" class="contents">
                    <Button
                        :icon="links.primary.icon"
                        class="flex-grow basis-0"
                        icon-class="text-xl"
                    />
                </router-link>
                <router-link :to="links.secondary2.to" class="contents">
                    <Button
                        text
                        :icon="links.secondary2.icon"
                        class="flex-grow basis-0"
                        icon-class="text-xl"
                    />
                </router-link>
            </div>
        </template>
    </Card>
</template>
