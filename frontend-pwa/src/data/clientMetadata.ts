import { useLocalStorage } from "@vueuse/core";

export const clientId = useLocalStorage("clientId", crypto.randomUUID());
