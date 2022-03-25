import create from 'zustand';
const useStore = create((set) => ({
  notifyStageStart: false,
  notifyStageEnd: false,
  notifyQuestEnd: false,
  notifyHandOversize: false,
  notifyHandNotOversize: false,
  setNotifyStageStart: (status) => set(() => ({notifyStageStart: status})),
  setNotifyStageEnd: (status) => set(() => ({notifyStageEnd: status})),
  setNotifyQuestEnd: (status) => set(() => ({notifyQuestEnd: status})),
  setNotifyHandOversize: (status) => set(() => ({notifyHandOversize: status})),
  setNotifyHandNotOversize: (status) => set(() => ({notifyHandNotOversize: status})),
}));

export const useNotifyStageStart = () => useStore((state) => state.notifyStageStart);
export const useNotifyStageEnd = () => useStore((state) => state.notifyStageEnd);
export const useNotifyQuestEnd = () => useStore((state) => state.notifyQuestEnd);
export const useNotifyHandOversize = () => useStore((state) => state.notifyHandOversize)
export const useNotifyHandNotOversize = () => useStore((state) => state.notifyHandNotOversize)

export const useSetNotifyStageStart = () => useStore((state) => state.setNotifyStageStart);
export const useSetNotifyStageEnd = () => useStore((state) => state.setNotifyStageEnd);
export const useSetNotifyQuestEnd = () => useStore((state) => state.setNotifyQuestEnd);
export const useSetNotifyHandOversize = () => useStore((state) => state.setNotifyHandOversize);
export const useSetNotifyHandNotOversize = () => useStore((state) => state.setNotifyHandNotOversize);