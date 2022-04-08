
import create from "zustand";

export const useStore = create((set) => ({
    gameEndRequest: false,
    gameEndRequestBody: {},
    setGameEndRequest: (status) => set(() => ({gameEndRequest: status})),
    setGameEndRequestBody: (body) => set(() => ({gameEndRequestBody: body})),
}));

export const generalRequestStore = () => useStore.getState();

export const useGameEndRequest = () => useStore((state) => state.gameEndRequest);
export const useGameEndRequestBody = () => useStore((state) => state.gameEndRequestBody);

export const useSetGameEndRequest = () => useStore((state) => state.setGameEndRequest);
export const useSetGameEndRequestBody = () => useStore((state) => state.setGameEndRequestBody);
