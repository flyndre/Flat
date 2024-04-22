/**
 * Clones an object in such a way that makes it safe to store in the database.
 * @param object any object
 * @returns a database-safe object of the same type
 */
export function dbSafe<T>(object: T) {
    return <T>JSON.parse(JSON.stringify(object));
}
