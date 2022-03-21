import create from 'zustand';

const useStore = create((set) => ({
    playerPlayAreas: [], //{hand: []} is temp
    //setPlayerPlayAreas : (playerPlayAreas,index) => set(()=>{playerPlayAreas[index] : playerPlayAreas}),
    stageAreas: [],
    currentStage: 0,
    //setStageAreas : (stageAreas) => set(()=>{stageAreas : stageAreas}),
    //playerPlayAreas: [{ hand: [] }, { hand: [] }, { hand: [] }, { hand: [] }],
    updatePlayerArea: (playAreaData) => set((current) => ({
        /*
        {
            "source": "PLAYER",
            "id": 2,
            "bids": 0,
            "battlePoints": 15,
            "acceptedCardTypes": [
              "WEAPON",
              "ALLY",
              "AMOUR"
            ],
            "cardsInPlay": [
              {
                "cardID": 111,
                "cardCode": null,
                "cardName": null,
                "subType": null,
                "imgSrc": "./Assets/Adventure Deck (346x470)/Adventure Deck Card Back.png",
                "bids": 0,
                "battlePoints": 0,
                "effectDescription": null,
                "hasActiveEffect": false
              }
            ]
          }
        */
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