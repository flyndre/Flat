import html2canvas from 'html2canvas';

/**
 * Takes a `HTMLElement` and creates a snapshot image of it.
 * @param element the `HTMLElement`
 * @returns a `File` that is the snapshot image
 */
export async function getElementSnapshot(element: HTMLElement) {
    const canvas = await html2canvas(element);
    const blob = await new Promise<Blob | null>((r) =>
        canvas.toBlob((b) => r(b))
    );
    if (blob == null) return undefined;
    return new File([blob], element.accessKey);
}
