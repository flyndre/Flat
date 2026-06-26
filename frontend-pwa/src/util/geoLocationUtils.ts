import { useGeolocation, UseGeolocationOptions } from "@vueuse/core";
import { computed } from "vue";

export function useSanitizedGeolocation(options?: UseGeolocationOptions) {
    const { coords, ...rest } = useGeolocation(options);
    return {
        ...rest,
        coords: computed(() => {
            const c = coords.value;
            if (Number.isFinite(c.latitude) && Number.isFinite(c.longitude)) {
                return c;
            }
            return undefined;
        }),
    }
}
