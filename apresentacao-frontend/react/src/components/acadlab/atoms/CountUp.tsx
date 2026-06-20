import { useEffect, useRef, useState } from "react";

/**
 * CountUp — reactbits-style animated number counter.
 * Tweens to `end` using an ease-out curve when it enters the viewport, e
 * re-anima sempre que `end` muda (ex.: quando os dados reais chegam do backend).
 */
export function CountUp({
  end,
  duration = 1200,
  decimals = 0,
  prefix = "",
  suffix = "",
  className,
}: {
  end: number;
  duration?: number;
  decimals?: number;
  prefix?: string;
  suffix?: string;
  className?: string;
}) {
  const [value, setValue] = useState(0);
  const [visible, setVisible] = useState(false);
  const ref = useRef<HTMLSpanElement | null>(null);
  const fromRef = useRef(0);

  // Dispara quando o elemento entra na viewport (apenas uma vez).
  useEffect(() => {
    if (!ref.current) return;
    const node = ref.current;
    const io = new IntersectionObserver(
      (entries) => {
        if (entries.some((e) => e.isIntersecting)) {
          setVisible(true);
          io.disconnect();
        }
      },
      { threshold: 0.2 },
    );
    io.observe(node);
    return () => io.disconnect();
  }, []);

  // Anima do valor atual até `end` sempre que ficar visível ou o alvo mudar.
  useEffect(() => {
    if (!visible) return;
    const from = fromRef.current;
    const start = performance.now();
    let raf = 0;
    const tick = (now: number) => {
      const t = Math.min(1, (now - start) / duration);
      const eased = 1 - Math.pow(1 - t, 3);
      const current = from + (end - from) * eased;
      setValue(current);
      fromRef.current = current;
      if (t < 1) {
        raf = requestAnimationFrame(tick);
      } else {
        fromRef.current = end;
      }
    };
    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [visible, end, duration]);

  const formatted = value.toLocaleString("pt-BR", {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  });

  return (
    <span ref={ref} className={className}>
      {prefix}
      {formatted}
      {suffix}
    </span>
  );
}
