import { useTheme } from '@/plugins/ThemePlugin';
import { ColorSchemeType } from '@vueuse/core';
import { Plugin, ref, watch } from 'vue';
import I18nPlugin from './I18nPlugin';

const { settingsTheme: theme } = useTheme();
const { locale } = I18nPlugin.global;

const SETTINGS_KEY = 'flat/settings/settings';

type Settings = {
    theme: ColorSchemeType;
    homeLive: boolean;
    homeLatitude: number;
    homeLongitude: number;
    handedness: 'left' | 'right';
    locale: typeof locale.value;
};

const defaultSettings: Settings = {
    theme: 'no-preference',
    homeLive: true,
    homeLatitude: 8.297651,
    homeLongitude: -79.12684,
    handedness: 'right',
    locale: locale.value,
};

const settings = ref<Settings>(defaultSettings);

watch(
    settings,
    (newValue, _oldValue) => {
        theme.value = newValue.theme;
        locale.value = newValue.locale;
    },
    {
        deep: true,
    }
);

function read(key: string) {
    try {
        return JSON.parse(localStorage.getItem(key));
    } catch (e) {
        console.error(e);
        return undefined;
    }
}

function write(key: string, value: any) {
    localStorage.setItem(key, JSON.stringify(value));
}

function remove(key: string) {
    localStorage.removeItem(key);
}

function reset() {
    Object.entries(defaultSettings).forEach(
        ([key, value]) => (settings.value[key] = value)
    );
}

const SettingsPlugin: Plugin = {
    install(_app, ..._options) {
        settings.value = read(SETTINGS_KEY) ?? defaultSettings;
        watch(
            settings,
            (value, _oldValue) => {
                write(SETTINGS_KEY, value);
            },
            {
                deep: true,
            }
        );
    },
};
export default SettingsPlugin;

export const useSettings = () => ({
    settings,
    reset,
});
