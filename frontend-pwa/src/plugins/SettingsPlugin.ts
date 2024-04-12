import { useTheme } from '@/plugins/ThemePlugin';
import { ColorSchemeType } from '@vueuse/core';
import { Plugin, ref, watch } from 'vue';

const { settingsTheme: theme } = useTheme();

const SETTINGS_KEY = 'flat/settings/settings';

type Settings = {
    theme: ColorSchemeType;
};

const defaultSettings: Settings = {
    theme: 'no-preference',
};

const settings = ref<Settings>(defaultSettings);

watch(
    settings,
    (newValue, _oldValue) => {
        theme.value = newValue.theme;
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
    reset: () => remove(SETTINGS_KEY),
});