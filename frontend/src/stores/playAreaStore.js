import create from 'zustand';

export const useStore = create((set, get) => ({
    playerPlayAreas: [],
    stageAreas: [],
    currentStage: 0,
    getPlayerPlayArea: (playerId) => get().playerPlayAreas.find(playArea => playArea.id === playerId),
    setStageAreas: (stageAreas) => set(() => ({ stageAreas: stageAreas })),
    updatePlayerPlayArea: (playAreaData) => set((current) => ({
        playerPlayAreas: (() => {
            let playerExist = false;
            for (let i in current.playerPlayAreas) {
                if (current.playerPlayAreas[i].id === playAreaData.id) {
                    playerExist = true;
                }
            }
            if (playerExist) {
                return current.playerPlayAreas.map((currArea) => {
                    if (currArea.id === playAreaData.id) {
                        return playAreaData;
                    } else {
                        return currArea;
                    }
                });
            } else {
                return [...current.playerPlayAreas, playAreaData];
            }
        })()
    })),
    updateStageArea: (stageAreaData) => set((current) => ({
        stageAreas: (() => {
            let stageExists = false;
            for (let i in current.stageAreas) {
                if (current.stageAreas[i].stageNum === stageAreaData.stageNum) {
                    stageExists = true;
                }
            }
            console.log("stageExists: " + stageExists);
            if (stageExists) {
                return current.stageAreas.map((currArea) => {
                    if (currArea.stageNum === stageAreaData.stageNum) {
                        return stageAreaData;
                    } else {
                        return currArea;
                    }
                });
            } else {
                return [...current.stageAreas, stageAreaData];
            }
        })()
    }))

}));

export const playAreaStore = () => useStore.getState();

export const usePlayerPlayAreas = () => useStore((state) => state.playerPlayAreas);
export const useStageAreas = () => useStore((state) => state.stageAreas);
export const useCurrentStage = () => useStore((state) => state.currentStage);
export const useGetPlayerPlayArea = (id) => useStore((state) => state.getPlayerPlayArea(id));

export const useUpdatePlayerPlayArea = () => useStore((state) => state.updatePlayerPlayArea)
export const useUpdateStageArea = () => useStore((state) => state.updateStageArea);
export const useSetStageAreas = () => useStore((state) => state.setStageAreas);
