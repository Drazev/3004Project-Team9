import create from "zustand";

export const useStore = create((set) => ({
  tournamentJoinRequest: false,
  tournamentStageStart: false,
  tournamentSetup: false,

  setTournamentSetup: (status) => set(() => ({tournamentSetup: status})),
  setTournamentStageStart: (hasStarted) => set(() => ({tournamentStageStart: hasStarted})),
  setTournamentJoinRequest: (status) => set(() => ({tournamentJoinRequest: status})),
}));

/**
 * Getter for non-component use
 */
export const tournamentStore = () => useStore.getState();

/**
 * Getters
 */
export const useTournamentSetup = () => useStore((state) => state.tournamentSetup);
export const useTournamentStageStart = () => useStore((state) => state.tournamentStageStart);
export const useTournamentJoinRequest = () => useStore((state) => state.tournamentJoinRequest);

/**
 * Setters
 */
export const useSetTournamentSetup = () => useStore((state) => state.setTournamentSetup);
export const useSetTournamentStageStart = () => useStore((state) => state.setTournamentStageStart);
export const useSetTournamentJoinRequest = () => useStore((state) => state.setTournamentJoinRequest);

export default useStore;
