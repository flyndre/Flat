import { de, en } from "naughty-words";

export default (joinName: string) =>
    ![
        joinName !== undefined,
        joinName.length > 0,
        ![...de, ...en].some(w => joinName.includes(w)),
    ].includes(false);
