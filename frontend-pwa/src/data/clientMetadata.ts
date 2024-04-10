import { useLocalStorage } from '@vueuse/core';
import { v4 as uuidv4 } from 'uuid';

export const clientId = useLocalStorage('clientId', uuidv4());
