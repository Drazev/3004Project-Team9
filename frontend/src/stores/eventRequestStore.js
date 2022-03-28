import create from "zustand";

export const useStore = create((set) => ({
    targetSelectionRequest: false,
    setTargetSelectionRequest: (x) => set(() => ({targetSelectionRequest: x}))
}));

/**
 *  Getters for non-component use
 */
export const eventRequestStore = () => useStore.getState();

/**
 * Getters
 */
export const useTargetSelectionRequest = () => useStore((state) => state.targetSelectionRequest);


/**
 * Setters
 */
export const useSetTargetSelectionRequest = () => useStore((state) => state.setTargetSelectionRequest);
