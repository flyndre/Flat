export function parseIntElse(string: string | string[]) {
    if (Array.isArray(string)) {
        string = string.join("");
    }
    try {
        return Number.parseInt(string);
    } catch (e) {
        return undefined;
    }
}
