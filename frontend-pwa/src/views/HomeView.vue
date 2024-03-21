<script setup lang="ts">
import Button from "primevue/button";
import Card from "primevue/card";
</script>

<template>
    <Card :pt="{ footer: { class: 'flex flex-row gap-2' } }">
        <template #title> Flat </template>
        <template #subtitle> Fleet Live Area Tracking </template>
        <template #content>
            The modern cross-platform route tracking app to orchestrate
            volunteer collection campaigns in your neighborhood.
        </template>
        <template #footer>
            <a href="https://github.com/flyndre/Flat">
                <Button label="Repository" />
            </a>
            <a href="https://flyndre.github.io">
                <Button text label="Team Website" />
            </a>
        </template>
    </Card>
</template>
