<template>
    <header class="sticky left-0 top-0 right-0 p-2 z-10 backdrop-blur">
        <template v-if="!$slots['action-left'] && !$slots['action-right']">
            <header class="flex flex-row justify-center items-center gap-2">
                <h1 class="text-base font-semibold">
                    <slot name="title"></slot>
                </h1>
            </header>
        </template>
        <template v-else>
            <nav class="flex flex-row justify-between items-center gap-2">
                <div class="flex-grow basis-0 flex flex-row gap-2">
                    <slot name="action-left">
                        <div></div>
                    </slot>
                </div>
                <h1 class="text-base font-semibold leading-[0]">
                    <slot name="title"></slot>
                </h1>
                <div class="flex-grow basis-0 flex flex-row-reverse gap-2">
                    <slot name="action-right">
                        <div></div>
                    </slot>
                </div>
            </nav>
        </template>
    </header>
</template>
