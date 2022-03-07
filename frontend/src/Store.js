import create from "zustand";


/*
  currentStage: 3
  stages: {0: {stageType: "foe", stageCard: {}, activeCards: [], totalBP: 5},1:{stageType: "test", stageCard: {}, highestBid: 10},2:{stageType: "foe"}}
  playerActiveCards: {playerName: {cardsActive[{},{}]}, playerName2:{}}
  activePlayers: ["player1","player2"]
  turns will be managed by overall gamestate store
  questSponsor: "PlayerName"


  Overall GameState Store
    updateTurn
    endTurn
  
  Story Store
    UpdateStages (get stages from backend)
    UpdateActivePlayerCards (remove old active cards)
    GetRewards (distributes reward to players)

    PlayerPlaysCard (Card, PlayerName)
    PlayerJoinsQuest (PlayerName)
    StartQuest: {questCard}
    SponsorStageCard (sponsor plays card to sponsor stage) {stageNo, card, PlayerName}
*/

const useStore = create((set) => ({
  title: "Quest Game",
  connected: false,
  gameStarted: false,
  name: "",
  messages: [],
  players: [],
  hands: [],
  loadPlayers: async () => {
    await handleLoadPlayers(set);
  },
  setConnected: (connected) => set(() => ({ connected: connected })),
  setName: (name) => set(() => ({ name: name })),
  setGameStarted: (gameStarted) => set(() => ({ gameStarted: gameStarted })),
  setPlayers: (players) => set(() => ({ players: players })),
  /*
  updateHand: (hand) => set((currentState) => {
    hands: [...currentState.hands, hand]
  }),*/
  updateHand: (hand) =>
    set((current) => ({
      hands: (() => {
        console.log("hand = " + hand);
        let playerExist = false;
        for (let i in current.hands) {
          if (current.hands[i].name === hand.name) {
            playerExist = true;
          }
        }
        console.log("playerExist = " + playerExist)
        if (playerExist) {
          return current.hands.map((currHand) => {
            if (currHand.name === hand.name) {
              return hand;
            } else {
              return currHand;
            }
          });
        } else {
          return [...current.hands, hand]
        }
      })(),
    })),
  addNewMessage: (name, message) =>
    set((current) => ({
      messages: [...current.messages, { name: name, message: message }],
    })),
}));

const handleLoadPlayers = async (setPlayers) => {
  fetch("http://localhost:8080/api/player")
    .then((response) => response.json())
    .then((players) => {
      setPlayers({ players: players });
    })
    .catch((error) => {
      console.log("eror in handleLoadPlayers: " + error);
    });
};

export const useTitle = () => useStore((state) => state.title);
export const useConnected = () => useStore((state) => state.connected);
export const useName = () => useStore((state) => state.name);
export const useMessages = () => useStore((state) => state.messages);
export const useGameStarted = () => useStore((state) => state.gameStarted);
export const usePlayerHands = () => useStore((state) => state.hands);

export const usePlayers = () => useStore((state) => state.players);
export const useAddNewPlayer = () => useStore((state) => state.addNewPlayer);

export const useSetConnected = () => useStore((state) => state.setConnected);
export const useSetGameStarted = () =>
  useStore((state) => state.setGameStarted);
export const useSetName = () => useStore((state) => state.setName);
export const useAddNewMessage = () => useStore((state) => state.addNewMessage);

export const useUpdateHand = () => useStore((state) => state.updateHand);

export const useSetPlayers = () => useStore((state) => state.setPlayers);

export const useLoadPlayers = () => useStore((state) => state.loadPlayers);

export default useStore;
