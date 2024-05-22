/**
 * Gets the date part of a date as formatted string.
 * @param date the date string
 */
export function getDate(date: string, locale: string = 'en') {
    return new Date(date).toLocaleDateString(locale, {
        dateStyle: 'medium',
    });
}

/**
 * Gets the time part of a date as formatted string.
 * @param date the date string
 */
export function getTime(date: string, locale: string = 'en') {
    return new Date(date).toLocaleTimeString(locale, {
        timeStyle: 'short',
    });
}

/**
 * Gets both, date and time part, of a date as formatted string.
 * @param date the date string
 */
export function getDateTime(date: string, locale: string = 'en') {
    return new Date(date).toLocaleString(locale, {
        dateStyle: 'medium',
        timeStyle: 'short',
    });
}

/**
 * Formats a date string using two dates.
 * If both are on the same day, the second date's dd.MM.YYYY is omitted.
 * @param from the starting date string
 * @param to the ending date string
 */
export function formatDateRange(from: any, to: any, locale: string = 'en') {
    let short = {
        timeStyle: 'short',
    } as Intl.DateTimeFormatOptions;
    let long = {
        dateStyle: 'short',
        timeStyle: 'short',
    } as Intl.DateTimeFormatOptions;
    let start = new Date(from);
    let end = new Date(to);
    if (isSameDay(start, end)) {
        return (
            start.toLocaleString(locale, long) +
            ' - ' +
            end.toLocaleString(locale, short)
        );
    }
    return (
        start.toLocaleString(locale, long) +
        ' - ' +
        end.toLocaleString(locale, long)
    );
}

/**
 * Determines whether two dates are on the same day.
 * @param a a date
 * @param b another date
 */
export function isSameDay(a: Date, b: Date) {
    let sameYear = a.getFullYear() === b.getFullYear();
    let sameMonth = a.getMonth() === b.getMonth();
    let sameDay = a.getDate() === b.getDate();
    return sameYear && sameMonth && sameDay;
}
