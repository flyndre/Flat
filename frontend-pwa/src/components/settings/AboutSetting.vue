<script setup lang="ts">
import Card from 'primevue/card';
import Button from 'primevue/button';
</script>

<template>
    <Card
        :pt="{
            root: { class: 'shadow-none bg-transparent' },
            body: { class: 'p-0' },
            footer: { class: 'flex flex-row gap-2' },
        }"
    >
        <template #title> Flat </template>
        <template #subtitle> Fleet Live Area Tracking </template>
        <template #content>
            The modern cross-platform route tracking app to orchestrate
            volunteer collection campaigns in your neighborhood.
        </template>
        <template #footer>
            <a href="https://github.com/flyndre/Flat">
                <Button class="p-0" link label="Repository" />
            </a>
            <a href="https://flyndre.github.io">
                <Button class="p-0" link label="Team Website" />
            </a>
        </template>
    </Card>
</template>
