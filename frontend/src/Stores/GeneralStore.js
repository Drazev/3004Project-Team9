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
  sponsorRequest: "",
  isSponsoring: false,
  loadPlayers: async () => {
    await handleLoadPlayers(set);
  },
  setConnected: (connected) => set(() => ({ connected: connected })),
  setName: (name) => set(() => ({ name: name })),
  setTurn: (name) => set(() => ({ turn: name })),
  setIsSponsoring: (x) => set(() => ({isSponsoring: x})),
  setPopupType: (type) => set(() => ({ popupType: type })),
  setSponsorRequest: (name) => set(() => ({ sponsorRequest: name })),
  setGameStarted: (gameStarted) => set(() => ({ gameStarted: gameStarted })),
  setPlayers: (players) => set(() => ({ players: players })),
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

export const useSetConnected = () => useStore((state) => state.setConnected);
export const useSetGameStarted = () => useStore((state) => state.setGameStarted);
export const useSetName = () => useStore((state) => state.setName);
export const useAddNewMessage = () => useStore((state) => state.addNewMessage);

export const useUpdateHand = () => useStore((state) => state.updateHand);

export const useUpdatePlayer = () => useStore((state) => state.updatePlayer);

export const useSetPlayers = () => useStore((state) => state.setPlayers);

export const useSetIsSponsoring = () => useStore((state) => state.setIsSponsoring);
export const useIsSponsoring = () => useStore((state) => state.isSponsoring);

export const useLoadPlayers = () => useStore((state) => state.loadPlayers);

export const useSetTurn = () => useStore((state) => state.setTurn);

export const useSetSponsorRequest = () => useStore((state) => state.setSponsorRequest);

export const usePopupType = () => useStore((state) => state.popupType);

export const useSetPopupType = () => useStore((state) => state.setPopupType);

export default useStore;
