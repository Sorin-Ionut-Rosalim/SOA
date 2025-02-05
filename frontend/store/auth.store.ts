import { User } from "@/types/identity";
import { create } from "zustand";

interface AuthStore {
  user?: User;
  setUser: (user?: User) => void;
}

const useAuthStore = create<AuthStore>()((set) => ({
  setUser(user) {
    set({ user });
  },
}));

export { useAuthStore };
