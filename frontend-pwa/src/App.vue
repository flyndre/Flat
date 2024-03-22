<script setup lang="ts">
import NavbarMobile from "@/layouts/navbar/NavbarMobile.vue";
import NavbarTablet from "@/layouts/navbar/NavbarTablet.vue";
import NavbarDesktop from "@/layouts/navbar/NavbarDesktop.vue";
const navbarLinks = {
    primary: {
        label: "Create",
        icon: "mdi mdi-plus",
        to: { name: "home" },
    },
    secondary1: {
        label: "Join",
        icon: "mdi mdi-import",
        to: { name: "home" },
    },
    secondary2: {
        label: "Presets",
        icon: "mdi mdi-archive",
        to: { name: "home" },
    },
};
</script>

<template>
    <div class="h-screen flex flex-col sm:flex-row">
        <NavbarMobile :links="navbarLinks" class="sm:hidden" />
        <NavbarTablet :links="navbarLinks" class="hidden sm:block lg:hidden" />
        <NavbarDesktop :links="navbarLinks" class="hidden lg:block" />
        <div class="flex flex-col gap-4 m-4">
            <router-view />
        </div>
    </div>
</template>
