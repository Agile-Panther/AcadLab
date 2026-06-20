import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";

export type ProfileOption = {
  value: string;
  label: string;
  description?: string;
};

type Ctx = {
  profiles: ProfileOption[];
  active: string | null;
  setActive: (v: string) => void;
  register: (profiles: ProfileOption[], initial?: string) => void;
  clear: () => void;
};

const ProfileSwitcherContext = createContext<Ctx | null>(null);

export function ProfileSwitcherProvider({ children }: { children: ReactNode }) {
  const [profiles, setProfiles] = useState<ProfileOption[]>([]);
  const [active, setActive] = useState<string | null>(null);

  const register = useCallback((opts: ProfileOption[], initial?: string) => {
    setProfiles(opts);
    setActive((prev) => {
      if (prev && opts.some((o) => o.value === prev)) return prev;
      return initial ?? opts[0]?.value ?? null;
    });
  }, []);

  const clear = useCallback(() => {
    setProfiles([]);
    setActive(null);
  }, []);

  const value = useMemo(
    () => ({ profiles, active, setActive, register, clear }),
    [profiles, active, register, clear],
  );

  return (
    <ProfileSwitcherContext.Provider value={value}>{children}</ProfileSwitcherContext.Provider>
  );
}

export function useProfileSwitcherContext() {
  const ctx = useContext(ProfileSwitcherContext);
  if (!ctx)
    throw new Error("useProfileSwitcherContext must be used within ProfileSwitcherProvider");
  return ctx;
}

/**
 * Registra opções de perfil para a tela atual. Retorna o perfil ativo.
 * Ao desmontar, limpa o switcher do TopBar.
 */
export function useProfileSwitcher(
  options: ProfileOption[],
  initial?: string,
): { active: string; setActive: (v: string) => void } {
  const { register, clear, active, setActive } = useProfileSwitcherContext();

  // Serializa as opções para estabilizar o efeito.
  const key = JSON.stringify(options.map((o) => [o.value, o.label]));

  useEffect(() => {
    register(options, initial);
    return () => clear();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [key, initial]);

  return {
    active: active ?? initial ?? options[0]?.value ?? "",
    setActive,
  };
}
