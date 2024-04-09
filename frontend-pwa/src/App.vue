<script setup lang="ts">
import Snackbar from '@/components/Snackbar.vue';
</script>

<template>
    <div class="max-w-[800px] m-auto flex flex-col">
        <router-view />
    </div>
    <Snackbar />
</template>
