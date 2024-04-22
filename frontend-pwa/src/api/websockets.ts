import WebSocket from 'ws';
import { member } from "../types/member";

export let members = [] as member[]; 

const ws = new WebSocket(import.meta.env.BASE_URL + '/ws');

ws.on('message', (data) => {
    data ? members.push(data) : null;
});