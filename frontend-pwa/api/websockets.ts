import WebSocket from 'ws';
import { member } from "../src/types/member";

let members = [] as member[]; 

const ws = new WebSocket('ws://www.host.com/path');

ws.on('message', (data) => {
    data ? members.push(data) : null;
});

export function getMembers(){
    return members; 
}