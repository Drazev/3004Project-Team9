import create from "zustand";

const useStore = create((set) => ({
  title: "Quest Game",
  connected: false,
  name: "",
  messages: [],
  setConnected: (connected) => set(() => ({ connected: connected })),
  setName: (name) => set(() => ({ name: name })),
  addNewMessage: (name, message) =>
    set((current) => ({
      messages: [...current.messages, { name: name, message: message }],
    })),
}));

export const useTitle = () => useStore((state) => state.title);
export const useConnected = () => useStore((state) => state.connected);
export const useName = () => useStore((state) => state.name);
export const useMessages = () => useStore((satte) => satte.messages);

export const useSetConnected = () => useStore((state) => state.setConnected);
export const useSetName = () => useStore((state) => state.setName);
export const useAddNewMessage = () => useStore((state) => state.addNewMessage);

export default useStore;
