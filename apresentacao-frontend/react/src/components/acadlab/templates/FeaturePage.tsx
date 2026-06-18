import { useState, type ReactNode } from "react";
import { AppShell } from "./AppShell";
import { TabsRow, type TabItem } from "../molecules/TabsRow";

export type FeatureSection = TabItem & { content: ReactNode };

export function FeaturePage({
  title,
  subtitle,
  topRight,
  sections,
  // `rules` accepted but intentionally not rendered — kept for backward compat.
  rules: _rules,
}: {
  title: string;
  subtitle?: string;
  topRight?: ReactNode;
  sections: FeatureSection[];
  rules?: string[];
}) {
  const [active, setActive] = useState(sections[0]?.value ?? "");
  const current = sections.find((s) => s.value === active) ?? sections[0];
  return (
    <AppShell title={title} subtitle={subtitle} topRight={topRight}>
      <TabsRow items={sections} value={active} onChange={setActive} className="mb-6" />
      <div className="flex flex-col gap-6">{current?.content}</div>
    </AppShell>
  );
}
