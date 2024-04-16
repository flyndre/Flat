# Custom Plugins

For some global functionalities, custom Vue plugins have been created.
Together with all other plugins, they are installed in `src/main.ts`.
To access them inside components, plugins commonly provide `useSomething(...)` hooks.

## Theme Plugin

The component library PrimeVue already provides the functionality to customize appearance using CSS file based themes.
Built on top of that, the `src/plugins/ThemePlugin.ts` plugin is realized.
Based on a `themeOverride` and the `prefers-color-scheme` option of the browser, it determines the currently to be active theme.
This is done by toggeling `style` tags inside the head with `useStyleTag` from `VueUse`.

The theme can be controlled via the `useTheme` hook:

```js
import { useTheme } from '@/plugins/ThemePlugin';

const { settingsTheme, activeTheme } = useTheme();
settingsTheme.value = 'dark';
```

> [!CAUTION]
> The `settingsTheme` is synced with the `settings.theme` value.
> It is synced unidirectional from `settings.theme -> settingsTheme`.
> So, if you'd like to change the theme, use the settings plugin!

## Settings Plugin

The `src/plugins/ThemePlugin.ts` plugin provides global functionality to read and write a settings object that is persisted in `localstorage`.
Initially, a default settings object is stored.
That object can be modified by code everywhere in the app using the `useSettings` hook:

```js
import { useSettings } from '@/plugins/SettingsPlugin';

const { settings, reset } = useSettings();
settings.handedness = 'left';
```
