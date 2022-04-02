import create from "zustand";

export const useStore = create((set) => ({
    stageTargetSelectionRequest: true,
    stageTargetSelectionRequestBody: {
        // requestID : 2,
        // requestPlayerID: 1,
        // requestCardCode: "MERLIN",
        // responseType:  "STAGE_TARGET_SELECTION" //Can also be CARD_TARGET_SELECTION for mordred
    },
    cardTargetSelectionRequest: true,
    cardTargetSelectionRequestBody: {
        // requestID : 2,
        // requestPlayerID: 1,
        // requestCardCode: "MERLIN",
        // responseType:  "CARD_TARGET_SELECTION" //Can also be CARD_TARGET_SELECTION for mordred
    },
    setStageTargetSelectionRequest: (x) => set(() => ({stageTargetSelectionRequest: x})),
    setStageTargetSelectionRequestBody: (x) => set(() => ({stageTargetSelectionRequestBody: x})),
    setCardTargetSelectionRequest: (x) => set(() => ({cardTargetSelectionRequest: x})),
    setCardTargetSelectionRequestBody: (x) => set(() => ({cardTargetSelectionRequestBody: x}))
}));

/**
 *  Getters for non-component use
 */
export const effectRequestStore = () => useStore.getState();

/**
 * Getters
 */
export const useCardTargetSelectionRequest = () => useStore((state) => state.cardTargetSelectionRequest);
export const useCardTargetSelectionRequestBody = () => useStore((state) => state.cardTargetSelectionRequestBody);
export const useStageTargetSelectionRequest = () => useStore((state) => state.stageTargetSelectionRequest);
export const useStageTargetSelectionRequestBody = () => useStore((state) => state.stageTargetSelectionRequestBody);


/**
 * Setters
 */
export const useSetCardTargetSelectionRequest = () => useStore((state) => state.setCardTargetSelectionRequest);
export const useSetCardTargetSelectionRequestBody = () => useStore((state) => state.setCardTargetSelectionRequestBody);
export const useSetStageTargetSelectionRequest = () => useStore((state) => state.setStageTargetSelectionRequest);
export const useSetStageTargetSelectionRequestBody = () => useStore((state) => state.setStageTargetSelectionRequestBody);
