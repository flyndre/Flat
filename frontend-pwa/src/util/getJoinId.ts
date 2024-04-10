export default (joinLink: string) => {
    const match = joinLink?.match(/\/join\/(.+)/);
    if (match === null || match.length <= 1) {
        return undefined;
    }
    return match[1];
};
