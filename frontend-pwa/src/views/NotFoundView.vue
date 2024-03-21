<script setup lang="ts">
import Button from "primevue/button";
import Card from "primevue/card";
</script>

<template>
    <Card>
        <template #title> 404 </template>
        <template #subtitle>Page not found</template>
        <template #footer>
            <router-link :to="{ name: 'home' }">
                <Button label="Go Home" />
            </router-link>
        </template>
    </Card>
</template>
