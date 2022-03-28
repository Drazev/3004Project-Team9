import create from "zustand";

const useStore = create((set) => ({
    sponsorSearchRequest: false,
    questJoinRequest: false,
    setSponsorSearchRequest: (x) => set(() => ({sponsorSearchRequest: x})),
    setQuestJoinRequest: (x) => set(() => ({questJoinRequest: x})),
}));

/**
 * Getters for non-component use
 */
export const questRequestStore = () => useStore.getState();

/**
 * Getters
 */
export const useSponsorSearchRequest = () => useStore((state) => state.sponsorSearchRequest);
export const useQuestJoinRequest = () => useStore((state) => state.questJoinRequest);

/**
 * Setters
 */
export const useSetSponsorSearchRequest = () => useStore((state) => state.setSponsorSearchRequest);
export const useSetQuestJoinRequest = () => useStore((state) => state.setQuestJoinRequest);
