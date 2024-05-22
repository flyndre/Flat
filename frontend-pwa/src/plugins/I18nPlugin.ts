import de from '@/assets/lang/de.json';
import en from '@/assets/lang/en.json';
import es from '@/assets/lang/es.json';
import { createI18n } from 'vue-i18n';

const fallbackLocale = 'en';
const messages = {
    en,
    de,
    es,
};
const availableLocales = Object.keys(messages).map((l) => l.toLowerCase());
const locale =
    navigator.languages.find((l) =>
        availableLocales.includes(l.toLowerCase())
    ) ?? fallbackLocale;

const I18nPlugin = createI18n({
    availableLocales,
    fallbackLocale,
    legacy: false,
    locale,
    messages,
});
export default I18nPlugin;
