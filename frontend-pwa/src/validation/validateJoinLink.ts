export default (joinLink: string) =>
    ![
        joinLink !== undefined,
        joinLink.length > 0,
        joinLink.includes(window.location.host),
    ].includes(false);
