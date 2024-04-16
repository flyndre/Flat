export function dbSafe<T>(object: T) {
    return <T>JSON.parse(JSON.stringify(object));
}
