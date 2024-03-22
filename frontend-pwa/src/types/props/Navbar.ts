import { RouteLocationRaw } from "vue-router";
export type NavbarLink = {
    label: string;
    icon: string;
    to: RouteLocationRaw;
};
export type NavbarLinks = {
    primary: NavbarLink;
    secondary1: NavbarLink;
    secondary2: NavbarLink;
};
