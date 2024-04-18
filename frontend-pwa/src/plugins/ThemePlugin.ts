import darkTheme from '@/assets/themes/dark.css?inline';
import lightTheme from '@/assets/themes/light.css?inline';
import {
    ColorSchemeType,
    usePreferredColorScheme,
    useStyleTag,
} from '@vueuse/core';
import { Plugin, computed, ref, watch } from 'vue';

const themeColorMetaTag = ref<HTMLMetaElement>(undefined);
const prefersColorScheme = usePreferredColorScheme();
const colorSchemeOverride = ref<ColorSchemeType>(undefined);
const colorScheme = computed(() =>
    colorSchemeOverride.value === 'no-preference'
        ? prefersColorScheme.value
        : colorSchemeOverride.value
);

const { load: loadLightTheme, unload: unloadLightTheme } = useStyleTag(
    lightTheme,
    {
        id: 'theme-light',
        immediate: false,
    }
);
const { load: loadDarkTheme, unload: unloadDarkTheme } = useStyleTag(
    darkTheme,
    { id: 'theme-dark', immediate: false }
);

watch(colorScheme, (newValue, _oldValue) => {
    loadScheme(newValue);
});

function loadScheme(scheme: ColorSchemeType) {
    if (scheme === 'dark') {
        unloadLightTheme();
        loadDarkTheme();
        setThemeColor('#121212');
    } else {
        unloadDarkTheme();
        loadLightTheme();
        setThemeColor('#f8fafc');
    }
}

function setThemeColor(color: string) {
    themeColorMetaTag.value.content = color;
}

const ThemePlugin: Plugin = {
    install(_app, ..._options) {
        const element =
            document.querySelector<HTMLMetaElement>(
                'meta[name="theme-color"]'
            ) ?? document.createElement('meta');
        element.name = 'theme-color';
        element.content = 'transparent';
        document.head.appendChild(element);
        themeColorMetaTag.value = element;
        loadScheme(colorScheme.value);
    },
};
export default ThemePlugin;

export const useTheme = () => ({
    settingsTheme: computed({
        get: () => colorSchemeOverride.value,
        set: (v) =>
            (colorSchemeOverride.value = v ?? colorSchemeOverride.value),
    }),
    activeTheme: colorScheme,
});
