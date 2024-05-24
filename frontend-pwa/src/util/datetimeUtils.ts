/**
 * Checks whether a `Date` instance is valid.
 * @param date the `Date` instance
 * @returns `true` if the date is valid, `false` otherwise
 * @see https://stackoverflow.com/a/1353711/11793652
 */
export function isValidDate(date: Date) {
    return date instanceof Date && !isNaN(date.getTime());
}

/**
 * Gets the date part of a date as formatted string.
 * @param date the date string
 */
export function getDate(date: string | Date, locale: string = 'en') {
    if (!isValidDate(new Date(date))) return undefined;
    return new Date(date).toLocaleDateString(locale, {
        dateStyle: 'medium',
    });
}

/**
 * Gets the time part of a date as formatted string.
 * @param date the date string
 */
export function getTime(date: string | Date, locale: string = 'en') {
    if (!isValidDate(new Date(date))) return undefined;
    return new Date(date).toLocaleTimeString(locale, {
        timeStyle: 'short',
    });
}

/**
 * Gets both, date and time part, of a date as formatted string.
 * @param date the date string
 */
export function getDateTime(date: string | Date, locale: string = 'en') {
    if (!isValidDate(new Date(date))) return undefined;
    return new Date(date).toLocaleString(locale, {
        dateStyle: 'medium',
        timeStyle: 'short',
    });
}

export function getDuration(from: string | Date, to: string | Date) {
    const start = new Date(from);
    const end = new Date(to);
    if (!(isValidDate(start) && isValidDate(end))) return undefined;
    const diffTime = Math.abs(end.getTime() - start.getTime());
    return msToTime(diffTime);
}

export function msToTime(duration: number) {
    const milliseconds = Math.floor((duration % 1000) / 100);
    const seconds = Math.floor((duration / 1000) % 60);
    const minutes = Math.floor((duration / (1000 * 60)) % 60);
    const hours = Math.floor((duration / (1000 * 60 * 60)) % 24);

    const hoursStr = hours < 10 ? '0' + hours : hours;
    const minutesStr = minutes < 10 ? '0' + minutes : minutes;
    const secondsStr = seconds < 10 ? '0' + seconds : seconds;

    return hoursStr + ':' + minutesStr + ':' + secondsStr + '.' + milliseconds;
}

/**
 * Formats a date string using two dates.
 * If both are on the same day, the second date's dd.MM.YYYY is omitted.
 * @param from the starting date string
 * @param to the ending date string
 */
export function formatDateRange(from: any, to: any, locale: string = 'en') {
    let short: Intl.DateTimeFormatOptions = {
        timeStyle: 'short',
    };
    let long: Intl.DateTimeFormatOptions = {
        dateStyle: 'long',
        timeStyle: 'short',
    };
    let start = new Date(from);
    let end = new Date(to);
    if (!(isValidDate(start) && isValidDate(to))) return undefined;
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
