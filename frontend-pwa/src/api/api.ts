import { axiosDemoAdapter } from "@/util/demoUtils";
import axios from "axios";

const mode = import.meta.env.MODE;

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  adapter: mode === 'demo' ? axiosDemoAdapter : undefined,
});

export default api;
