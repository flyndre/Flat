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
        class="rounded-none w-[200px]"
        :pt="{ body: { class: 'p-2 flex-grow' } }"
    >
        <template #content>
            <div class="flex flex-col justify-start gap-2">
                <router-link :to="links.primary.to">
                    <Button
                        :label="links.primary.label"
                        :icon="links.primary.icon"
                        class="text-left w-full"
                    />
                </router-link>
                <router-link :to="links.secondary1.to">
                    <Button
                        text
                        :label="links.secondary1.label"
                        :icon="links.secondary1.icon"
                        class="text-left w-full"
                    />
                </router-link>
                <router-link :to="links.secondary2.to">
                    <Button
                        text
                        :label="links.secondary2.label"
                        :icon="links.secondary2.icon"
                        class="text-left w-full"
                    />
                </router-link>
            </div>
        </template>
    </Card>
</template>
