/**
 * Standardizes a CSS color string to its HEX string.
 * @param cssColor a CSS color value such as `red` or `rgba(255, 0, 0, 0.5)`
 * @returns the corresponding HEX value
 * @see https://stackoverflow.com/a/47355187/11793652
 */
export function standardizeCSStoHex(cssColor: string) {
    var ctx = document.createElement('canvas').getContext('2d');
    ctx.fillStyle = cssColor;
    return ctx.fillStyle;
}

/**
 * Blends two CSS color values together by an amount.
 * @param colorA The first CSS color value.
 * @param colorB The second CSS color value.
 * @param blendAmount blend amount, default `0.5`
 * @returns the HEX value of the blended color
 * @see https://stackoverflow.com/a/56348573/11793652
 */
export function blendColors(colorA: string, colorB: string, blendAmount = 0.5) {
    colorA = standardizeCSStoHex(colorA);
    colorB = standardizeCSStoHex(colorB);
    const [rA, gA, bA] = colorA.match(/\w\w/g).map((c) => parseInt(c, 16));
    const [rB, gB, bB] = colorB.match(/\w\w/g).map((c) => parseInt(c, 16));
    const r = Math.round(rA + (rB - rA) * blendAmount)
        .toString(16)
        .padStart(2, '0');
    const g = Math.round(gA + (gB - gA) * blendAmount)
        .toString(16)
        .padStart(2, '0');
    const b = Math.round(bA + (bB - bA) * blendAmount)
        .toString(16)
        .padStart(2, '0');
    return '#' + r + g + b;
}
