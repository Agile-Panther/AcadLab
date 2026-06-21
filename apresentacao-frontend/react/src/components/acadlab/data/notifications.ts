import type { LucideIcon } from "lucide-react";
import { CalendarClock, CheckCircle2, GraduationCap, Megaphone, Wallet } from "lucide-react";

export type NotificationTone = "info" | "success" | "warning" | "danger";

export type AppNotification = {
  id: string;
  title: string;
  description: string;
  time: string;
  icon: LucideIcon;
  tone: NotificationTone;
  unread?: boolean;
};

export const notifications: AppNotification[] = [
  {
    id: "n1",
    title: "Matrícula confirmada",
    description: "Sua matrícula no período 2026.1 foi efetivada com sucesso.",
    time: "há 5 min",
    icon: CheckCircle2,
    tone: "success",
    unread: true,
  },
  {
    id: "n2",
    title: "Nova vaga de estágio",
    description: "Acme Tech publicou uma vaga compatível com seu perfil.",
    time: "há 1 h",
    icon: GraduationCap,
    tone: "info",
    unread: true,
  },
  {
    id: "n3",
    title: "Boleto disponível",
    description: "Mensalidade de junho disponível para pagamento.",
    time: "há 3 h",
    icon: Wallet,
    tone: "warning",
    unread: true,
  },
  {
    id: "n4",
    title: "Lembrete de prova",
    description: "Avaliação de Engenharia de Software amanhã, 14h, sala B-204.",
    time: "ontem",
    icon: CalendarClock,
    tone: "info",
  },
  {
    id: "n5",
    title: "Comunicado da coordenação",
    description: "Reunião de turma na sexta-feira às 19h.",
    time: "2 dias",
    icon: Megaphone,
    tone: "info",
  },
];
