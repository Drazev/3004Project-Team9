import create from 'zustand';

const useStore = create((set)=> ({
    playerPlayAreas: [{hand: []}, {hand: []}, {hand: []}, {hand: []}], //{hand: []} is temp
    //setPlayerPlayAreas : (playerPlayAreas,index) => set(()=>{playerPlayAreas[index] : playerPlayAreas}),
    stageAreas: [],
    //setStageAreas : (stageAreas) => set(()=>{stageAreas : stageAreas}),

    updatePlayArea: (playAreaData) => set((current) => { 
        if(!playAreaData.hasOwnProperty('source') && !playAreaData.hasOwnProperty('id')){
            console.log("Data packet should have 'source' and 'id'");
            return false;
        }

        //What kind of play area do I have?
        if(playAreaData.source==="PLAYER"){
            console.log("Player play area before: " + current.playerPlayAreas);
            playerPlayAreas: (() => {
                let playerExist = false;
                for (let i in current.playerPlayAreas) {
                    if (current.playerPlayAreas[i].id === playAreaData.id) {
                        playerExist = true;
                    }
                }
                if(playerExist){
                    return current.playerPlayAreas.map((currArea) => {
                        if (currArea.id === playAreaData.id) {
                          return playAreaData;
                        } else {
                          return currArea;
                        }
                      });
                }else{
                    return [...current.playerPlayAreas, playAreaData];
                }
            })()
            console.log("\nAfter: " + current.playerPlayAreas);
        }
        else if(playAreaData.source==="QUEST_STAGE") {
            console.log("Stage area before: " + current.stageAreas);
            stageAreas: (() => {
                let stageExists = false;
                for (let i in current.stageAreas) {
                    if (current.stageAreas[i].id === playAreaData.id) {
                        stageExists = true;
                    }
                }
                if(stageExists){
                    return current.stageAreas.map((currArea) => {
                        if (currArea.id === playAreaData.id) {
                          return playAreaData;
                        } else {
                          return currArea;
                        }
                      });
                }else{
                    return [...current.stageAreas, playAreaData];
                }
            })()
            console.log("\nAfter: " + current.stageAreas);
        }
    }),
/*
    // updatePlayArea: (playAreaData) => set((current) => {
    //     if(!playAreaData.hasOwnProperty('source') && !playAreaData.hasOwnProperty('id')){
    //         console.log("Data packet should have 'source' and 'id'");
    //         return false;
    //     }

    //     let targetPlayArea;

    //     //What kind of play area do I have?
    //     if(playAreaData.source==="PLAYER"){
    //         targetPlayArea=playerPlayAreas;
    //     }
    //     else if(playAreaData.source==="QUEST_STAGE") {
    //         targetPlayArea=stageAreas;
    //     }

    //     let id = targetPlayArea.id;

    //     for(let i=0;i<targetPlayArea.length;++i) {
    //         if(targetPlayArea[i].id===id) {
    //             targetPlayArea[i] = playAreaData;
    //         }
    //     }

    //     if(playAreaData.source==="PLAYER"){
    //         playerPlayArea = targetPlayArea;
    //     }
    //     else if(playAreaData.source==="QUEST_STAGE") {
    //         playerPlayArea = targetPlayArea;
    //     }
    // }),
}));

const processPlayAreaChaged = (playAreaData) =>
{
    if(!playAreaData.hasOwnProperty('source') && !playAreaData.hasOwnProperty('id'))
    {
        return false;
    }
    
    let targetPlayArea;
    if(playAreaData.source==="PLAYER") {
        
    }


    let playerId=playAreaData.playerId;
    

    const playAreaArray = useStore(state => state.playerPlayAreas);
    for(let i=0;i<playAreaArray.length;++i) {
        if(playAreaArray[i].playerId===playerId) {
            playAreaArray[i] = playAreaData;
        }
    }
}

function updatePlayArea(playerPlayArea) {

}*/}));

export const usePlayerPlayAreas = () => useStore((state) => state.playerPlayAreas);

export const useStageAreas = () => useStore((state) => state.stageAreas);

export const useUpdatePlayArea = () => useStore((state) => state.updatePlayArea);

export default useStore;