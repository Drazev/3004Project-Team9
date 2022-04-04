import create from "zustand";

const useStore = create((set) => ({
    sponsorSearchRequest: false,
    questJoinRequest: false,
    bidRequest: false,
    participantSetupRequest: false,
    bidRequestSetup: false,
    setSponsorSearchRequest: (x) => set(() => ({sponsorSearchRequest: x})),
    setQuestJoinRequest: (x) => set(() => ({questJoinRequest: x})),
    setBidRequest: (x) => set(() => ({bidRequest: x})),
    setParticipantSetupRequest: (x) => set(() => ({participantSetupRequest: x})),
    setBidRequestSetup: (x) => set(() => ({bidRequestSetup: x})),
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
export const useBidRequest = () => useStore((state) => state.bidRequest);
export const useParticipantSetupRequest = () => useStore((state) => state.participantSetupRequest);
export const useBidRequestSetup = () => useStore((state) => state.bidRequestSetup);

/**
 * Setters
 */
export const useSetSponsorSearchRequest = () => useStore((state) => state.setSponsorSearchRequest);
export const useSetQuestJoinRequest = () => useStore((state) => state.setQuestJoinRequest);
export const useSetBidRequest = () => useStore((state) => state.setBidRequest);
export const useSetParticipantSetupRequest = () => useStore((state) => state.setParticipantSetupRequest);
export const useSetBidRequestSetup = () => useStore((state) => state.setBidRequestSetup);