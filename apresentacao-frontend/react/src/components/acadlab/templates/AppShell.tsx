import type { ReactNode } from "react";
import { Sidebar } from "../organisms/Sidebar";
import { TopBar } from "../organisms/TopBar";

export function AppShell({
  title,
  subtitle,
  topRight,
  children,
}: {
  title: string;
  subtitle?: string;
  topRight?: ReactNode;
  children: ReactNode;
}) {
  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <div className="lg:pl-[248px]">
        <TopBar title={title} subtitle={subtitle} right={topRight} />
        <main className="px-6 py-7 lg:px-8">
          <div className="mx-auto max-w-[1100px]">{children}</div>
        </main>
      </div>
    </div>
  );
}
