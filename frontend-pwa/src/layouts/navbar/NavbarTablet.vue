<script setup lang="ts">
import Button from "primevue/button";
import Card from "primevue/card";
import type { NavbarLinks } from "@/types/props/Navbar";
defineProps<{
    links: NavbarLinks;
}>();
const buttonPt = {
    root: {
        class: "w-14 h-14",
    },
};
</script>

<template>
    <Card class="rounded-none" :pt="{ body: { class: 'p-2 flex-grow' } }">
        <template #content>
            <div class="flex flex-col justify-start items-stretch gap-2">
                <router-link :to="links.primary.to">
                    <Button
                        :icon="links.primary.icon"
                        icon-class="text-3xl"
                        :pt="buttonPt"
                    />
                </router-link>
                <router-link :to="links.secondary1.to">
                    <Button
                        text
                        :icon="links.secondary1.icon"
                        icon-class="text-3xl"
                        :pt="buttonPt"
                    />
                </router-link>
                <router-link :to="links.secondary2.to">
                    <Button
                        text
                        :icon="links.secondary2.icon"
                        icon-class="text-3xl"
                        :pt="buttonPt"
                    />
                </router-link>
            </div>
        </template>
    </Card>
</template>
