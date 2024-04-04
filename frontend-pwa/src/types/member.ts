import { position } from "./position";

export interface member{
    name: string,
    uuid: string, 
    currentPosition: position,
    positionList: position[][]
}