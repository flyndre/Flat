<script setup lang="ts">
import { isOnMobile } from "@/util/mobileDetection";
</script>

<template>
    <main
        class="flex flex-col gap-2 justify-start items-stretch"
        :class="{ 'px-2': isOnMobile }"
    >
        <slot />
    </main>
</template>
