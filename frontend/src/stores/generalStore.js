import create from "zustand";

const useStore = create((set) => ({
  title: "Quest Game",
  connected: false,
  gameStarted: false,
  name: "",
  players: [],
  hands: [],
  events: "",
  popupType: "",
  turn: "",
  sponsorName: "",
  isSponsoring: false,
  joinRequest: false,
  handOversize: false,
  foeStageStart: false,
  storyCard: null,
  activePlayers: [],
  testStageStart: false,
  maxBid: 0,
  maxBidPlayer: {},
  currentBidder: {},
  loadPlayers: async () => {
    await handleLoadPlayers(set);
  },
  setConnected: (connected) => set(() => ({ connected: connected })),
  setName: (name) => set(() => ({ name: name })),
  setTurn: (name) => set(() => ({ turn: name })),
  setIsSponsoring: (x) => set(() => ({isSponsoring: x})),
  setSponsorName: (name) => set(() => ({ sponsorRequest: name })),
  setGameStarted: (gameStarted) => set(() => ({ gameStarted: gameStarted })),
  setPlayers: (players) => set(() => ({ players: players })),
  setJoinRequest: (status) => set(() => ({joinRequest: status})),
  setHandOversize: (status) => set(() => ({handOversize: status})),
  setActivePlayers: (players) => set(() => ({activePlayers: players})),
  setFoeStageStart: (status) => set(() => ({foeStageStart: status})),
  setStoryCard: (storyCard) => set(() => ({storyCard: storyCard})),
  setTestStageStart: (testStageStart) => set(() => ({testStageStart: testStageStart})),
  setMaxBid: (maxBid) => set(() => ({maxBid: maxBid})),
  setCurrentBidder: (currentBidder) => set(() => ({currentBidder: currentBidder})),
  setMaxBidPlayer: (maxBidPlayer) => set(() => ({maxBidPlayer: maxBidPlayer})),
  updatePlayer: (player) => set((current) => ({
    players: (() => {
      let playerExist = false;
      for (let i = 0; i < current.players.length; i++) {
        if (current.players[i].playerName === player.playerName) {
          playerExist = true;
        }
      }
      if (playerExist) {
        return current.players.map((currPlayer) => {
          if (currPlayer.playerName === player.playerName) {
            return player;
          } else {
            return currPlayer;
          }
        });
      } else {
        return [...current.players, player];
      }
    })
  })),
  updateHand: (hand) => set((current) => ({
    hands: (() => {
      let playerExist = false;
      for (let i in current.hands) {
        if (current.hands[i].playerName === hand.playerName) {
          playerExist = true;
        }
      }
      if (playerExist) {
        return current.hands.map((currHand) => {
          if (currHand.playerName === hand.playerName) {
            return hand;
          } else {
            return currHand;
          }
        });
      } else {
        return [...current.hands, hand];
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
export const useTurn = () => useStore((state) => state.turn);
export const usePlayers = () => useStore((state) => state.players);
export const useAddNewPlayer = () => useStore((state) => state.addNewPlayer);
export const useSponsorRequest = () => useStore((state) => state.sponsorRequest);
export const useJoinRequest = () => useStore((state) => state.joinRequest);
export const useHandOversize = () => useStore((state) => state.handOversize);
export const useActivePlayers = () => useStore((state) => state.activePlayers);
export const useFoeStageStart = () => useStore((state) => state.foeStageStart);
export const useTestStageStart = () => useStore((state) => state.testStageStart);
export const useMaxBid = () => useStore((state) => state.maxBid);
export const useMaxBidPlayer = () => useStore((state) => state.maxBidPlayer);
export const useCurrentBidder = () => useStore((state) => state.currentBidder);
export const useStoryCard = () => useStore((state) => state.storyCard);
export const useIsSponsoring = () => useStore((state) => state.isSponsoring);

export const useSetConnected = () => useStore((state) => state.setConnected);
export const useSetGameStarted = () => useStore((state) => state.setGameStarted);
export const useSetName = () => useStore((state) => state.setName);
export const useAddNewMessage = () => useStore((state) => state.addNewMessage);
export const useUpdateHand = () => useStore((state) => state.updateHand);
export const useUpdatePlayer = () => useStore((state) => state.updatePlayer);
export const useSetPlayers = () => useStore((state) => state.setPlayers);
export const useSetIsSponsoring = () => useStore((state) => state.setIsSponsoring);
export const useLoadPlayers = () => useStore((state) => state.loadPlayers);
export const useSetTurn = () => useStore((state) => state.setTurn);
export const useSetSponsorName = () => useStore((state) => state.setSponsorName);
export const usePopupType = () => useStore((state) => state.popupType);
export const useSetJoinRequest = () => useStore((state) => state.setJoinRequest);
export const useSetHandOversize = () => useStore((state) => state.setHandOversize);
export const useSetActivePlayers = () => useStore((state) => state.setActivePlayers);
export const useSetFoeStageStart= () => useStore((state) => state.setFoeStageStart);
export const useSetTestStageStart = () => useStore((state) => state.setTestStageStart);
export const useSetMaxBid = () => useStore((state) => state.setMaxBid);
export const useSetMaxBidPlayer = () => useStore((state) => state.setMaxBidPlayer);
export const useSetCurrentBidder = () => useStore((state) => state.setCurrentBidder);
export const useSetStoryCard = () => useStore((state) => state.setStoryCard)

export default useStore;
