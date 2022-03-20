import create from 'zustand';

const useStore = create((set) => ({
    playerPlayAreas: [{ hand: [] }, { hand: [] }, { hand: [] }, { hand: [] }], //{hand: []} is temp
    //setPlayerPlayAreas : (playerPlayAreas,index) => set(()=>{playerPlayAreas[index] : playerPlayAreas}),
    stageAreas: [],
    currentStage: 0,
    //setStageAreas : (stageAreas) => set(()=>{stageAreas : stageAreas}),

    updatePlayerArea: (playAreaData) =>
        set((current) => ({
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
    })),
}));

export const usePlayerPlayAreas = () => useStore((state) => state.playerPlayAreas);

export const useUpdatePlayerPlayArea = () => useStore((state) => state.updatePlayerArea)

export const useStageAreas = () => useStore((state) => state.stageAreas);

export const useCurrentStage = () => useStore((state) => state.currentStage);

export const useUpdateStageArea = () => useStore((state) => state.updateStageArea);

export default useStore;