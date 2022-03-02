import create from "zustand";

const useStore = create((set) => ({
  title: "Quest Game",
  connected: false,
  name: "",
  messages: [],
  players: ["bob", "Joe"],
  loadPlayers: async () => {await handleLoadPlayers(set)},
  setConnected: (connected) => set(() => ({ connected: connected })),
  setName: (name) => set(() => ({ name: name })),
  setPlayers: (players) => set(() => ({ players: players})),
  addNewMessage: (name, message) =>
    set((current) => ({
      messages: [...current.messages, { name: name, message: message }],
    })),
    addNewPlayer: (pnames) => set(() => ({players: pnames})),
}));

const handleLoadPlayers = async (setPlayers) => {
  fetch("http://localhost:8080/api/player")
    .then(response => response.json())
      .then(players => {
        setPlayers({players: players})
        // console.log(players) causes infinite loop!!!
      })
      .catch(error => {console.log("eror in handleLoadPlayers: " + error)})
}

export const useTitle = () => useStore((state) => state.title);
export const useConnected = () => useStore((state) => state.connected);
export const useName = () => useStore((state) => state.name);
export const useMessages = () => useStore((state) => state.messages);

export const usePlayers = () => useStore((state) => state.players);
export const useAddNewPlayer = () => useStore((state) => state.addNewPlayer);

export const useSetConnected = () => useStore((state) => state.setConnected);
export const useSetName = () => useStore((state) => state.setName);
export const useAddNewMessage = () => useStore((state) => state.addNewMessage);

export const useSetPlayers = () => useStore((state) => state.setPlayers);

export default useStore;
