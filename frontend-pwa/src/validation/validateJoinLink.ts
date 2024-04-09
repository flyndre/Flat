import getJoinId from "@/util/getJoinId";

export default (joinLink: string) =>
    ![
        joinLink !== undefined,
        joinLink.length > 0,
        joinLink.includes(window.location.host),
        getJoinId(joinLink) !== undefined,
    ].includes(false);
