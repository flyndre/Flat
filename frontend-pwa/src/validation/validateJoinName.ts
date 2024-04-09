import { de, en } from "naughty-words";

export default (joinName: string) =>
    ![
        joinName !== undefined,
        joinName.length > 0,
        ![...de, ...en].some(w => joinName.toLowerCase().includes(w.toLowerCase())),
    ].includes(false);
